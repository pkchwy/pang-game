package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import levels.LevelManager;
import main.Game;
import utilz.HelpMethods;
import utilz.SoundManager;

public class Item extends Entity {
    public enum ItemType {
        NORMAL_ARROW,
        SPECIAL_ARROW,
        FOOD_1,
        FOOD_2,
        FOOD_3,
        GUN
    }

    public static BufferedImage specialArrowImg;
    public static BufferedImage food1Img;
    public static BufferedImage food2Img;
    public static BufferedImage food3Img;
    public static BufferedImage gunImg;
    static {
        try {
            specialArrowImg = ImageIO.read(new File("res/item1.png"));
            food1Img = ImageIO.read(new File("res/food1.png"));
            food2Img = ImageIO.read(new File("res/food2.png"));
            food3Img = ImageIO.read(new File("res/food3.png"));
            gunImg = ImageIO.read(new File("res/gun-item.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage image;
    private ItemType type;
    private boolean active = true;
    private float gravity = 0.08f;
    private float fallSpeed = 0;
    private float maxFallSpeed = 2.5f;
    private boolean landed = false;
    private long landedTime = -1;
    private long stayDuration = 3500; 
    private LevelManager levelManager;

    public Item(float x, float y, int width, int height, BufferedImage image, ItemType type, LevelManager levelManager) {
        super(x, y, width, height);
        this.image = image;
        this.type = type;
        this.levelManager = levelManager;
        initHitbox(x, y, width*2, height*2);
    }

    @Override
    public void update() {
        if (!active) return;
        if (!landed) {
            fallSpeed = Math.min(fallSpeed + gravity, maxFallSpeed);
            float nextY = y + fallSpeed;
            if (levelManager != null && HelpMethods.canMoveTo(x, nextY, width*2, height*2, levelManager.getCurrentLevel())) {
                y = nextY;
            } else {
                landed = true;
                landedTime = System.currentTimeMillis();
                fallSpeed = 0;
            }
            updateHitbox();
        } else {
            if (System.currentTimeMillis() - landedTime > stayDuration) {
                setActive(false);
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (active && image != null) {
            g.drawImage(image, (int)x, (int)y, width*2, height*2, null);
        }
    }

    public void collect(Player player) {
        if (!active) return;
        
        switch (type) {
            case SPECIAL_ARROW:
                if (Game.getInstance().getCurrentGunType() == Game.GunType.ACTIVE) {
                    Game.getInstance().deactivateGun();
                }
                player.activateSpecialArrow();
                break;
            case GUN:
                if (player.getActiveArrowType() == entities.Player.ArrowType.SPECIAL) {
                    player.setActiveArrowType(entities.Player.ArrowType.NORMAL);
                }
                Game.getInstance().activateGun(6000);
                break;
            case FOOD_1:
                Game.getInstance().addScore(75);
                break;
            case FOOD_2:
                Game.getInstance().addScore(125);
                break;
            case FOOD_3:
                Game.getInstance().addScore(200);
                break;
        }
        
        active = false;
        SoundManager.getInstance().playItemCollectSound();
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public ItemType getType() { return type; }
    public BufferedImage getImage() { return image; }
} 