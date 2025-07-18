package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import entities.Balloon;
import entities.Bullet;
import entities.Player;
import levels.LevelManager;
import main.MenuBarBuilder;
import auth.User;
import auth.UserManager;
import auth.LoginDialog;
import levels.Level;
import entities.Item;
import utilz.SoundManager;

public class Game implements Runnable {
    private static Game instance;
    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;

    private Player player;
    private LevelManager levelManager;
    private List<Balloon> balloons;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private int currentScore = 0;
    private int scoreToAdd = 0;
    private String currentDifficulty = "Novice";
    private long levelStartTime;
    private long levelTimeLimit = 90000; 

    UserManager userManager;
    private String loggedInUser;

    public List<Item> items = new ArrayList<>();

    public enum ArrowType {
        NORMAL,
        SPECIAL
    }
    private ArrowType currentArrowType = ArrowType.NORMAL;
    private long specialArrowEndTime = 0;

    public enum GunType {
        NONE,
        ACTIVE
    }
    private GunType currentGunType = GunType.NONE;
    long gunEndTime = 0;

    public Game() {
        instance = this;

        userManager = new UserManager();
        LoginDialog loginDialog = new LoginDialog(null, userManager);
        loginDialog.setVisible(true);
        if (!loginDialog.isLoginSuccess()) {
            System.exit(0); 
        }
        loggedInUser = loginDialog.getLoggedInUser();

        initClasses();
        startLevel();
        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gameWindow.setJMenuBar(MenuBarBuilder.createMenuBar());
        gamePanel.requestFocus();
        SoundManager.getInstance().playBackgroundMusic();
        startGameLoop();
        
    }

    public void startNewGame() {
        gameOver = false;
        gameWon = false;
        currentScore = 0;
        levelStartTime = System.currentTimeMillis();

        levelManager = LevelManager.initLevels();
        Level lvl = levelManager.getCurrentLevel();
        player = new Player(lvl.getPlayerStartX(), lvl.getPlayerStartY(), levelManager);
        balloons = new ArrayList<>(levelManager.spawnBalloonsForCurrentLevel());

        SoundManager.getInstance().playBackgroundMusic();
    }

    public void selectLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < levelManager.getLevels().size()) {
            gameOver = false;
            gameWon = false;
            levelStartTime = System.currentTimeMillis();
            initClasses();
            levelManager.setCurrentLevelIndex(levelIndex);
            startLevel();
            SoundManager.getInstance().playBackgroundMusic();
        }
    }

    public void resetGame() {
        gameOver = false;
        gameWon = false;
        currentScore = 0;
        levelStartTime = System.currentTimeMillis();
        initClasses();
        startLevel();
        SoundManager.getInstance().playBackgroundMusic();
    }

    public static Game getInstance() {
        return instance;
    }

    private void initClasses() {
        levelManager = LevelManager.initLevels();
        Level lvl = levelManager.getCurrentLevel();
        player = new Player(lvl.getPlayerStartX(), lvl.getPlayerStartY(), levelManager);
        balloons = new ArrayList<>();
    }

    private void startLevel() {
        Level currentLevel = levelManager.getCurrentLevel();
        player.setLevelManager(levelManager);
        player.setX(currentLevel.getPlayerStartX());
        player.setY(currentLevel.getPlayerStartY());
        balloons = new ArrayList<>(levelManager.spawnBalloonsForCurrentLevel());
        gameOver = false;
        gameWon = false;
        levelStartTime = System.currentTimeMillis();
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setGameOver(boolean flag) {
        this.gameOver = flag;
        if (flag) {
            handleGameEnd();
        }
    }

    public void setGameWon(boolean flag) {
        this.gameWon = flag;
        if (flag) {
            handleGameEnd();
        }
    }

    public void addScore(int points) {
        currentScore += points;
        scoreToAdd = points;
        new Thread(() -> {
            try {
                Thread.sleep(1000); 
                scoreToAdd = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getScoreToAdd() {
        return scoreToAdd;
    }

    public void setCurrentScore(int score) {
        this.currentScore = score;
    }

    public void update() {
        if (gameOver || gameWon) return;

        if (System.currentTimeMillis() - levelStartTime > levelTimeLimit) {
            setGameOver(true);
            return;
        }

        player.update();
        checkPlayerCollisions();
        
        levelManager.update();
        updateArrow();
        updateBalloons();
        updateItems();

        checkLevelCompletion();
    }

    private void checkPlayerCollisions() {
        for (Balloon balloon : balloons) {
            if (player.getHitbox().intersects(balloon.getHitbox())) {
                player.takeDamage();
                if (player.getHealth() <= 0) {
                    setGameOver(true);
                    return;
                }
            }
        }
    }

    private void updateArrow() {
        if (player.getArrow() != null) {
            player.getArrow().update();
        }
        
        for (Bullet bullet : player.getBullets()) {
            bullet.update();
            
            for (int i = 0; i < balloons.size(); i++) {
                Balloon balloon = balloons.get(i);
                if (bullet.getHitbox().intersects(balloon.getHitbox())) {
                    addScore(getBalloonScore(balloon.getSize()));
                    balloons.remove(i);
                    List<Balloon> newB = balloon.createNewBalloons();
                    if (!newB.isEmpty()) {
                        balloons.addAll(newB);
                    }
                    bullet.setActive(false);
                    break;
                }
            }
        }
        
        if (currentArrowType == ArrowType.SPECIAL && System.currentTimeMillis() > specialArrowEndTime) {
            currentArrowType = ArrowType.NORMAL;
        }
        if (currentGunType == GunType.ACTIVE && System.currentTimeMillis() > gunEndTime) {
            currentGunType = GunType.NONE;
            player.setHasGun(false);
        }
    }

    private void updateBalloons() {
        for (int i = 0; i < balloons.size(); i++) {
            Balloon balloon = balloons.get(i);
            balloon.update();
            
            if (balloon.shouldCreateNewBalloons()) {
                addScore(getBalloonScore(balloon.getSize()));
                balloons.remove(i);
                List<Balloon> newB = balloon.createNewBalloons();
                if (!newB.isEmpty()) {
                    balloons.addAll(newB);
                }
                i--;
            }
        }
    }

    private void updateItems() {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            item.update();
            if (player.getHitbox().intersects(item.getHitbox())) {
                item.collect(player);
            }
            if (!item.isActive()) {
                items.remove(i);
                i--;
            }
        }
    }

    private void checkLevelCompletion() {
        if (balloons.isEmpty() && !gameOver && !gameWon) {
            if (levelManager.isLastLevel()) {
                setGameWon(true);
            } else {
                levelManager.nextLevel();
                startLevel();
            }
        }
    }

    public void render(Graphics g) {
        if (levelManager != null) {
            levelManager.render(g);
        }

        player.render(g);

        for (Balloon balloon : balloons) {
            balloon.render(g);
        }

        for (Item item : items) {
            item.render(g);
        }

        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, 800, 600);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 64));
            String gameOverText = "GAME OVER";
            FontMetrics metrics = g.getFontMetrics();
            int x = (800 - metrics.stringWidth(gameOverText)) / 2;
            int y = 300;
            g.drawString(gameOverText, x, y);
            
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            String restartText = "Press R to restart";
            metrics = g.getFontMetrics();
            x = (800 - metrics.stringWidth(restartText)) / 2;
            y = 350;
            g.drawString(restartText, x, y);
        }

        if (gameWon) {
            g.setColor(new Color(0, 0, 0, 200));
            g.fillRect(0, 0, 800, 600);
            
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 64));
            String victoryText = "YOU WON!";
            FontMetrics metrics = g.getFontMetrics();
            int x = (800 - metrics.stringWidth(victoryText)) / 2;
            int y = 250;
            g.drawString(victoryText, x, y);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            String scoreText = "Final Score: " + currentScore;
            metrics = g.getFontMetrics();
            x = (800 - metrics.stringWidth(scoreText)) / 2;
            y = 320;
            g.drawString(scoreText, x, y);
            
            g.setFont(new Font("Arial", Font.PLAIN, 24));
            String restartText = "Press R to play again";
            metrics = g.getFontMetrics();
            x = (800 - metrics.stringWidth(restartText)) / 2;
            y = 380;
            g.drawString(restartText, x, y);
        }

        MenuBarBuilder.drawHudBar(g, this, 800, 600);
    }

    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();
        double deltaU = 0, deltaF = 0;

        int frames = 0, updates = 0;
        long lastCheck = System.currentTimeMillis();

        while (true) {
            long currentTime = System.nanoTime();
            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }
            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
//                System.out.println("FPS: " + frames + " | UPS: " + updates); 
                frames = 0;
                updates = 0;
                lastCheck = System.currentTimeMillis();
            }
        }
    }

    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    public Player getPlayer() {
        return player;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public void setGameWindow(GameWindow window) {
        this.gameWindow = window;
    }

    public String getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(String difficulty) {
        this.currentDifficulty = difficulty;
        switch (difficulty) {
            case "Novice":
                levelTimeLimit = 120000; // 120 saniye
                break;
            case "Intermediate":
                levelTimeLimit = 90000; // 90 saniye
                break;
            case "Advanced":
                levelTimeLimit = 60000; // 60 saniye
                break;
            default:
                levelTimeLimit = 90000;
        }
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public long getLevelStartTime() {
        return levelStartTime;
    }

    public long getLevelTimeLimit() {
        return levelTimeLimit;
    }

    private int getBalloonScore(Balloon.BalloonSize size) {
        switch (size) {
            case BIG: return 50;
            case MID: return 75;
            case SMALL: return 100;
            case XSMALL: return 125;
            default: return 0;
        }
    }

    public ArrowType getCurrentArrowType() {
        return currentArrowType;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    private void handleGameEnd() {
        if (loggedInUser != null && userManager != null) {
            userManager.updateScore(loggedInUser, currentScore);
        }
        SoundManager.getInstance().stopBackgroundMusic();
    }

    public GunType getCurrentGunType() {
        return currentGunType;
    }

    public void activateGun(long duration) {
        currentGunType = GunType.ACTIVE;
        gunEndTime = System.currentTimeMillis() + duration;
        player.setHasGun(true);
    }

    public void deactivateGun() {
        currentGunType = GunType.NONE;
        gunEndTime = 0;
        player.setHasGun(false);
    }
}
