package levels;

import java.util.List;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Arrays;
import entities.GameObject;
import entities.Balloon;

public class LevelManager extends Object {
    private List<Level> levels;
    private int currentLevelIndex;
    private Image backgroundImage;

    public LevelManager(List<Level> levels) {
        super();
        this.levels = levels;
        this.currentLevelIndex = 0;
        if (!levels.isEmpty()) {
            this.backgroundImage = levels.get(0).getBackground();
        }
    }

    public Level getCurrentLevel() {
        return levels.get(currentLevelIndex);
    }

    public void nextLevel() {
        if (currentLevelIndex < levels.size() - 1) {
            currentLevelIndex++;
            backgroundImage = getCurrentLevel().getBackground();
        }
    }

    public boolean isLastLevel() {
        return currentLevelIndex == levels.size() - 1;
    }

    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }

    public void setCurrentLevelIndex(int index) {
        if (index >= 0 && index < levels.size()) {
            currentLevelIndex = index;
            backgroundImage = getCurrentLevel().getBackground();
        }
    }

    public List<Level> getLevels() {
        return levels;
    }
   
    public void render(Graphics g) {
        Level current = getCurrentLevel();
        if (current != null && backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, null);
        }
        if (current != null) {
            for (GameObject obj : current.getObjects()) {
                obj.render(g);
            }
        }
    }

    public List<Balloon> spawnBalloonsForCurrentLevel() {
        List<Balloon> balloons = new ArrayList<>();
        Level currentLevel = getCurrentLevel();
        
        if (currentLevel != null) {
            int levelIndex = getCurrentLevelIndex();
            
            switch (levelIndex) {
                case 0: // Level 1
                    balloons.add(new Balloon(400, 150, Balloon.BalloonSize.BIG, this));
                    break;
                case 1: // Level 2
                    balloons.add(new Balloon(400, 60, Balloon.BalloonSize.BIG, this));
                    break;
                case 2: // Level 3
                    balloons.add(new Balloon(600, 60, Balloon.BalloonSize.BIG, this));
                    balloons.add(new Balloon(100, 60, Balloon.BalloonSize.BIG, this));
                    break;
                case 3: // Level 4
                    balloons.add(new Balloon(200, 60, Balloon.BalloonSize.BIG, this));
                    balloons.add(new Balloon(600, 60, Balloon.BalloonSize.BIG, this));
                    break;
            }
        }
        return balloons;
    }

    public void update() {
        Level current = getCurrentLevel();
        if (current != null) {
            for (GameObject obj : current.getObjects()) {
                obj.update();
            }
        }
    }

    public static LevelManager initLevels() {
        try {
            List<Level> levels = new ArrayList<>();
            
            // Level 1
            Image bg1 = ImageIO.read(new File("res/background.png"));
            Level level1 = createLevel1(bg1);
            levels.add(level1);

            // Level 2
            Image bg2 = ImageIO.read(new File("res/background1.png"));
            Level level2 = createLevel2(bg2);
            levels.add(level2);

            // Level 3
            Image bg3 = ImageIO.read(new File("res/background2.png"));
            Level level3 = createLevel3(bg3);
            levels.add(level3);

            // Level 4
            Image bg4 = ImageIO.read(new File("res/background3.png"));
            Level level4 = createLevel4(bg4);
            levels.add(level4);

            LevelManager manager = new LevelManager(levels);
            return manager;
        } catch (IOException e) {
            e.printStackTrace();
            return new LevelManager(new ArrayList<>());
        }
    }

    private static Level createLevel1(Image background) {
        Level level = new Level(background);
        ArrayList<GameObject> objects = new ArrayList<>();
        
        int panelWidth = 800;
        int panelHeight = 600;
        int barHeight = 200;
        int gameHeight = panelHeight - barHeight;
        int borderThickness = 15;

        // Borders
        objects.add(new GameObject(0, 0, panelWidth, borderThickness, GameObject.ObjectType.BORDER));
        objects.add(new GameObject(0, gameHeight - borderThickness, panelWidth, borderThickness, GameObject.ObjectType.BORDER));
        objects.add(new GameObject(0, 0, borderThickness, gameHeight, GameObject.ObjectType.BORDER));
        objects.add(new GameObject(panelWidth - borderThickness, 0, borderThickness, gameHeight, GameObject.ObjectType.BORDER));

        
        level.setObjects(objects);
        level.setPlayerStartX(400);
        level.setPlayerStartY(320);
        return level;
    }

    private static Level createLevel2(Image background) {
        Level level = new Level(background);
        ArrayList<GameObject> objects = new ArrayList<>();
        
        int panelWidth = 800;
        int panelHeight = 600;
        int barHeight = 200;
        int gameHeight = panelHeight - barHeight;
        int borderThickness = 15;


        objects.add(new GameObject(0, 0, panelWidth, borderThickness, GameObject.ObjectType.BORDER));
        objects.add(new GameObject(0, gameHeight - borderThickness, panelWidth, borderThickness, GameObject.ObjectType.BORDER));
        objects.add(new GameObject(0, 0, borderThickness, gameHeight, GameObject.ObjectType.BORDER));
        objects.add(new GameObject(panelWidth - borderThickness, 0, borderThickness, gameHeight, GameObject.ObjectType.BORDER));


        int ladderWidth = 54;
        int ladderHeight = 40;
        

        objects.add(new GameObject(100, gameHeight - ladderHeight - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
        objects.add(new GameObject(100, gameHeight - ladderHeight * 2 - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
        objects.add(new GameObject(100, gameHeight - ladderHeight * 3 - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));

        objects.add(new GameObject(700, gameHeight - ladderHeight - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
        objects.add(new GameObject(700, gameHeight - ladderHeight * 2 - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
        objects.add(new GameObject(700, gameHeight - ladderHeight * 3 - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
        
        int stoneWidth = 32;
        int stoneHeight = 8;

        objects.add(new GameObject(154, gameHeight - ladderHeight * 3 - borderThickness + 2 , stoneWidth * 2, stoneHeight, GameObject.ObjectType.STONE2));
        objects.add(new GameObject(600, gameHeight - ladderHeight * 3 - borderThickness + 2 , stoneWidth , stoneHeight, GameObject.ObjectType.STONE2));

        objects.add(new GameObject(300, gameHeight - ladderHeight * 3 - borderThickness - 80, stoneWidth , stoneHeight, GameObject.ObjectType.STONE));
        objects.add(new GameObject(364,  gameHeight - ladderHeight * 3 - borderThickness - 80, stoneWidth , stoneHeight, GameObject.ObjectType.STONE));
        objects.add(new GameObject(364+64,  gameHeight - ladderHeight * 3 - borderThickness - 80, stoneWidth , stoneHeight, GameObject.ObjectType.STONE));
        objects.add(new GameObject(364+64*2,  gameHeight - ladderHeight * 3 - borderThickness - 80, stoneWidth , stoneHeight, GameObject.ObjectType.STONE));
        objects.add(new GameObject(364+64*3,  gameHeight - ladderHeight * 3 - borderThickness - 80, stoneWidth , stoneHeight, GameObject.ObjectType.STONE));
        
        level.setObjects(objects);
        level.setPlayerStartX(400);
        level.setPlayerStartY(320);
        return level;
    }

    private static Level createLevel3(Image background) {
        Level level = new Level(background);
        ArrayList<GameObject> objects = new ArrayList<>();
        
        int panelWidth = 800;
        int panelHeight = 600;
        int barHeight = 200;
        int gameHeight = panelHeight - barHeight;
        int borderThickness = 15;

        objects.add(new GameObject(0, 0, panelWidth, borderThickness, GameObject.ObjectType.BORDER));
        objects.add(new GameObject(0, gameHeight - borderThickness, panelWidth, borderThickness, GameObject.ObjectType.BORDER));
        objects.add(new GameObject(0, 0, borderThickness, gameHeight, GameObject.ObjectType.BORDER));
        objects.add(new GameObject(panelWidth - borderThickness, 0, borderThickness, gameHeight, GameObject.ObjectType.BORDER));

        int ladderWidth = 54;
        int ladderHeight = 40;
        objects.add(new GameObject(150, gameHeight - ladderHeight - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
        objects.add(new GameObject(350, gameHeight - ladderHeight - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
        objects.add(new GameObject(550, gameHeight - ladderHeight - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
        objects.add(new GameObject(650, gameHeight - ladderHeight - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
        
        int stoneWidth = 32;
        int stoneHeight = 8;
        
        objects.add(new GameObject(100, 300, stoneWidth * 2, stoneHeight, GameObject.ObjectType.STONE));
        objects.add(new GameObject(400, 300, stoneWidth * 2, stoneHeight, GameObject.ObjectType.STONE));
        objects.add(new GameObject(600, 300, stoneWidth * 2, stoneHeight, GameObject.ObjectType.STONE));
        
        objects.add(new GameObject(200, 200, stoneWidth * 3, stoneHeight, GameObject.ObjectType.STONE2));
        objects.add(new GameObject(500, 200, stoneWidth * 3, stoneHeight, GameObject.ObjectType.STONE2)); 
        objects.add(new GameObject(300, 100, stoneWidth * 4, stoneHeight, GameObject.ObjectType.STONE));
        
        level.setObjects(objects);
        level.setPlayerStartX(400);
        level.setPlayerStartY(320);
        return level;
    }

    private static Level createLevel4(Image background) {
        Level level = new Level(background);
        ArrayList<GameObject> objects = new ArrayList<>();
        
        int panelWidth = 800;
        int panelHeight = 600;
        int barHeight = 200;
        int gameHeight = panelHeight - barHeight;
        int borderThickness = 15;

        objects.add(new GameObject(0, 0, panelWidth, borderThickness, GameObject.ObjectType.BORDER));
        objects.add(new GameObject(0, gameHeight - borderThickness, panelWidth, borderThickness, GameObject.ObjectType.BORDER));
        objects.add(new GameObject(0, 0, borderThickness, gameHeight, GameObject.ObjectType.BORDER));
        objects.add(new GameObject(panelWidth - borderThickness, 0, borderThickness, gameHeight, GameObject.ObjectType.BORDER));

        int ladderWidth = 54;
        int ladderHeight = 40;

        objects.add(new GameObject(100, gameHeight - ladderHeight - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
        objects.add(new GameObject(100, gameHeight - ladderHeight * 2 - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
        objects.add(new GameObject(100, gameHeight - ladderHeight * 3 - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
   
        objects.add(new GameObject(600, gameHeight - ladderHeight - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
        objects.add(new GameObject(600, gameHeight - ladderHeight * 2 - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));
        objects.add(new GameObject(600, gameHeight - ladderHeight * 3 - borderThickness, ladderWidth, ladderHeight, GameObject.ObjectType.STAIRS));

        int stoneWidth = 32;
        int stoneHeight = 8;

        objects.add(new GameObject(154, gameHeight - ladderHeight * 3 - borderThickness + 2, stoneWidth * 2, stoneHeight, GameObject.ObjectType.STONE2));
        objects.add(new GameObject(654, gameHeight - ladderHeight * 3 - borderThickness + 2, stoneWidth * 2, stoneHeight, GameObject.ObjectType.STONE2));

        objects.add(new GameObject(200, gameHeight - ladderHeight * 2 - borderThickness - 80, stoneWidth * 3, stoneHeight, GameObject.ObjectType.STONE));
        objects.add(new GameObject(500, gameHeight - ladderHeight * 2 - borderThickness - 80, stoneWidth * 3, stoneHeight, GameObject.ObjectType.STONE));

        objects.add(new GameObject(300, gameHeight - ladderHeight * 3 - borderThickness - 80, stoneWidth * 4, stoneHeight, GameObject.ObjectType.STONE2));
        objects.add(new GameObject(100, gameHeight - ladderHeight * 2 - borderThickness - 140, stoneWidth * 2, stoneHeight, GameObject.ObjectType.STONE));
        objects.add(new GameObject(600, gameHeight - ladderHeight * 3 - borderThickness - 100, stoneWidth * 2, stoneHeight, GameObject.ObjectType.STONE));

        level.setObjects(objects);
        level.setPlayerStartX(400);
        level.setPlayerStartY(320);
        return level;
    }
} 