package main;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import auth.User;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

public class MenuBarBuilder {
    public static JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Game Menu
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem quitItem = new JMenuItem("Quit");
        JMenu levelMenu = new JMenu("Select Level");

        JMenuItem level1Item = new JMenuItem("Level 1");
        JMenuItem level2Item = new JMenuItem("Level 2");
        JMenuItem level3Item = new JMenuItem("Level 3");
        JMenuItem level4Item = new JMenuItem("Level 4");

        newGameItem.addActionListener(e -> Game.getInstance().startNewGame());
        quitItem.addActionListener(e -> System.exit(0));

        level1Item.addActionListener(e -> Game.getInstance().selectLevel(0));
        level2Item.addActionListener(e -> Game.getInstance().selectLevel(1));
        level3Item.addActionListener(e -> Game.getInstance().selectLevel(2));
        level4Item.addActionListener(e -> Game.getInstance().selectLevel(3));

        levelMenu.add(level1Item);
        levelMenu.add(level2Item);
        levelMenu.add(level3Item);
        levelMenu.add(level4Item);

        gameMenu.add(newGameItem);
        gameMenu.add(levelMenu);
        gameMenu.add(quitItem);

        // Options Menu
        JMenu optionsMenu = new JMenu("Options");
        JMenuItem highScoreItem = new JMenuItem("High Score");
        JMenu difficultyMenu = new JMenu("Difficulty");

        JMenuItem noviceItem = new JMenuItem("Novice");
        JMenuItem intermediateItem = new JMenuItem("Intermediate");
        JMenuItem advancedItem = new JMenuItem("Advanced");
        
        highScoreItem.addActionListener(e -> showHighScores());
        
        noviceItem.addActionListener(e -> changeDifficulty("Novice"));
        intermediateItem.addActionListener(e -> changeDifficulty("Intermediate"));
        advancedItem.addActionListener(e -> changeDifficulty("Advanced"));
        
        difficultyMenu.add(noviceItem);
        difficultyMenu.add(intermediateItem);
        difficultyMenu.add(advancedItem);
        
        optionsMenu.add(highScoreItem);
        optionsMenu.add(difficultyMenu);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");

        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(aboutItem);

        menuBar.add(gameMenu);
        menuBar.add(optionsMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private static void showHighScores() {
        StringBuilder message = new StringBuilder("Top 5 Players:\n\n");
        List<User> topPlayers = Game.getInstance().userManager.getTop5Players();
        
        if (topPlayers.isEmpty()) {
            message.append("No scores yet!");
        } else {
            for (int i = 0; i < topPlayers.size(); i++) {
                User player = topPlayers.get(i);
                message.append(String.format("%d. %s - %d points\n", 
                    i + 1, player.getUsername(), player.getScore()));
            }
        }
        
        JOptionPane.showMessageDialog(null,
            message.toString(),
            "High Scores",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private static void changeDifficulty(String difficulty) {
        Game.getInstance().setCurrentDifficulty(difficulty);
        JOptionPane.showMessageDialog(null,
            "Difficulty changed to " + difficulty,
            "Difficulty Changed",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private static void showAboutDialog() {
        JOptionPane.showMessageDialog(null,
            "Developer Information:\n" +
            "Name: Salih Mert Canseven\n" +
            "School Number: -\n" +
            "Email: salihmert.canseven@std.yeditepe.edu.tr",
            "About",
            JOptionPane.INFORMATION_MESSAGE);
    }

    public static Font getCustomFont() {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File("res/fonts/DepartureMono-Regular.otf")).deriveFont(18f);
        } catch (FontFormatException | java.io.IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public static void drawHudBar(Graphics g, Game game, int panelWidth, int panelHeight) {
        int barHeight = 200;
        g.setColor(Color.BLACK);
        g.fillRect(0, panelHeight - barHeight, panelWidth, barHeight);

        Font customFont = getCustomFont().deriveFont(22f);
        Font customFontBold = getCustomFont().deriveFont(Font.BOLD, 28f);
        Font customFontPlain = getCustomFont().deriveFont(Font.PLAIN, 22f);
        g.setColor(Color.WHITE);
        g.setFont(customFontBold);

        g.drawString("PLAYER-1", 60, panelHeight - barHeight + 48);
        g.setFont(customFontPlain);
        int currentScore = game.getCurrentScore();
        int scoreToAdd = game.getScoreToAdd();
        g.drawString(String.valueOf(currentScore), 80, panelHeight - barHeight + 90);
        if (scoreToAdd > 0) {
            g.setColor(new Color(255, 215, 0)); // Altın sarısı renk
            g.drawString("+" + scoreToAdd, 140, panelHeight - barHeight + 90);
            g.setColor(Color.WHITE);
        }

        entities.Player player = game.getPlayer();
        BufferedImage healthIcon = player.getHealthIcon();
        if (healthIcon != null) {
            int iconSize = 32;
            int spacing = 8;
            int startX = 60;
            int startY = panelHeight - barHeight + 110;
            for (int i = 0; i < player.getHealth(); i++) {
                g.drawImage(healthIcon, startX + (i * (iconSize + spacing)), startY, iconSize, iconSize, null);
            }
        }

        if (player.getActiveArrowType() == entities.Player.ArrowType.SPECIAL) {
            long remaining = (player.specialArrowEndTime - System.currentTimeMillis()) / 1000;
            if (remaining < 0) remaining = 0;
            BufferedImage specialArrowImg = entities.Item.specialArrowImg;
            if (specialArrowImg != null) {
                g.drawImage(specialArrowImg, panelWidth/2 - 100, panelHeight - barHeight + 150, 32, 32, null);
            }
            g.setFont(customFontBold);
            g.setColor(Color.YELLOW);
            g.drawString("SPEC-ARROW! " + remaining + "s", panelWidth/2 - 60, panelHeight - barHeight + 175);
            g.setColor(Color.WHITE);
        }

        if (game.getCurrentGunType() == Game.GunType.ACTIVE) {
            long remaining = (game.gunEndTime - System.currentTimeMillis()) / 1000;
            if (remaining < 0) remaining = 0;
            BufferedImage gunImg = entities.Item.gunImg;
            if (gunImg != null) {
                g.drawImage(gunImg, panelWidth/2 - 100, panelHeight - barHeight + 150, 32, 32, null);
            }
            g.setFont(customFontBold);
            g.setColor(Color.RED);
            g.drawString("GUN! " + remaining + "s", panelWidth/2 - 60, panelHeight - barHeight + 175);
            g.setColor(Color.WHITE);
        }

        String topPlayer = "NO PLAYER";
        int hiScore = 0;
        if (game != null && game.userManager != null) {
            hiScore = game.userManager.getHighestScore();
            topPlayer = game.userManager.getTopPlayerName();
        }
        g.setFont(customFontBold);
        g.drawString(topPlayer, panelWidth/2 - 60, panelHeight - barHeight + 48);
        g.setFont(customFontPlain);
        int currentLevel = game.getLevelManager().getCurrentLevelIndex() + 1;
        g.drawString(currentLevel + "-1 STAGE", panelWidth/2 - 60, panelHeight - barHeight + 90);
        g.drawString("HI:" + hiScore, panelWidth/2 - 60, panelHeight - barHeight + 130);


        g.setFont(customFontBold);
        g.drawString("PLAYER-2", panelWidth - 220, panelHeight - barHeight + 48);
        g.setFont(customFontPlain);
        g.drawString("PUSH BUTTON", panelWidth - 220, panelHeight - barHeight + 90);
        
        g.setFont(customFontBold);
        long remainingTime = Math.max(0, game.getLevelTimeLimit() - (System.currentTimeMillis() - game.getLevelStartTime()));
        int seconds = (int) (remainingTime / 1000);
        String timeText = String.format("Time: %02d:%02d", seconds / 60, seconds % 60);
        g.setColor(Color.CYAN);
        g.drawString(timeText, panelWidth - 230, 50);
        g.setColor(Color.WHITE);
    }
} 