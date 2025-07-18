package entities;

import static utilz.Constants.BalloonConstants.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import levels.LevelManager;
import utilz.HelpMethods;
import main.Game;
import utilz.SoundManager;

public class Balloon extends Entity {

    public enum BalloonSize {
        BIG,
        MID,
        SMALL,
        XSMALL
    }

    private BufferedImage[][] animations;
    private BufferedImage[] balloonImages;
    private int aniTick = 0, aniIndex = 0, aniSpeed =9;
    private int balloonAction = BIG;
    private boolean exploding = false;
    private boolean shouldCreateNewBalloons = false;

    private LevelManager levelManager;
    private Arrow arrow;
    private BalloonSize size;
    private final int scale=2;

    private float ySpeed = -0.8f;
    private float xSpeed = 1.0f;
    private float maxXSpeed = 1.0f;
    private Random random;

    public Balloon(float x, float y, BalloonSize size, LevelManager levelManager) {
        super(x, y, getWidthForSize(size), getHeightForSize(size));
        this.levelManager = levelManager;
        this.size = size;

        loadBalloons();
        loadAnimations();
        random = new Random();

        // Zorluk seviyesine göre hız ayarı
        String sel = main.Game.getInstance().getCurrentDifficulty();
        switch (sel) {
            case "Novice":
                maxXSpeed = 1.0f;
                ySpeed = -0.8f;
                break;
            case "Intermediate":
                maxXSpeed = 1.5f;
                ySpeed = -1.2f;
                break;
            case "Advanced":
                maxXSpeed = 2.0f;
                ySpeed = -1.7f;
                break;
            default:
                maxXSpeed = 1.0f;
                ySpeed = -0.8f;
        }
        xSpeed = random.nextBoolean() ?  maxXSpeed: -maxXSpeed;

        initHitbox(x, y, 2 * getWidthForSize(size), 2 * getHeightForSize(size));
    }

    @Override
    public void update() {
        if (!exploding) {
            updatePosition();
        }
        updateAnimationTick();
        setAnimation();
        updateHitbox();
        checkArrowCollision();
    }


    private void checkArrowCollision() {
        if (exploding) return;

        arrow = Game.getInstance().getPlayer().getArrow();
        if (arrow != null && arrow.isActive() && hitbox.intersects(arrow.getHitbox())) {
            hit();
            arrow.setActive(false);
            SoundManager.getInstance().playBalloonPopSound();
            return;
        }

        for (Bullet bullet : Game.getInstance().getPlayer().getBullets()) {
            if (bullet != null && bullet.isActive() && hitbox.intersects(bullet.getHitbox())) {
            	SoundManager.getInstance().playBalloonPopSound();
            	hit();
                bullet.setActive(false);
                break;
            }
        }
    }

    public void hit() {
        exploding = true;
        resetAniTick();
    }

    private void updatePosition() {
        int hbW = width * scale;
        int hbH = height * scale;

        float nextX = x + xSpeed;
        float nextY = y + ySpeed;

        boolean canMoveX  = HelpMethods.canMoveTo(nextX, y,  hbW, hbH, levelManager.getCurrentLevel());
        boolean canMoveY  = HelpMethods.canMoveTo(x,     nextY, hbW, hbH, levelManager.getCurrentLevel());
        boolean canMoveXY = HelpMethods.canMoveTo(nextX, nextY, hbW, hbH, levelManager.getCurrentLevel());

        if (canMoveXY) {
            x = nextX;
            y = nextY;
        } else {
            if (!canMoveX && !canMoveY) {
                xSpeed = -xSpeed;
                ySpeed = -ySpeed;
                x += xSpeed;
                y += ySpeed;
            } else {
                if (!canMoveX) {
                    xSpeed = -xSpeed;
                } else {
                    x = nextX;
                }
                if (!canMoveY) {
                    ySpeed = -ySpeed;
                } else {
                    y = nextY;
                }
            }
        }
    }




    @Override
    public void render(Graphics g) {
        if (exploding) {
            g.drawImage(animations[balloonAction][aniIndex], (int)x, (int)y, width * 2, height * 2, null);
        } else {
            g.drawImage(balloonImages[balloonAction],    (int)x, (int)y, width * 2, height * 2, null);
        }
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(balloonAction)) {
                aniIndex = 0;
                if (exploding) {
                    exploding = false;
                    shouldCreateNewBalloons = true;
                    Random rand = new Random();
                    double chance = rand.nextDouble();
                    if (chance < 0.15) { 
                        Item item = new Item(x, y, 15, 16, Item.specialArrowImg, Item.ItemType.SPECIAL_ARROW, levelManager);
                        Game.getInstance().items.add(item);
                    }
                    else if (chance < 0.30) {
                        Item gun = new Item(x, y, 16, 13, Item.gunImg, Item.ItemType.GUN, levelManager);
                        Game.getInstance().items.add(gun);
                    }
                    else if (chance < 0.6) {
                        Item food1 = new Item(x, y, 16, 16, Item.food1Img, Item.ItemType.FOOD_1, levelManager);
                        Game.getInstance().items.add(food1);
                    } else if (chance < 0.9) {
                        Item food2 = new Item(x, y, 16, 14, Item.food2Img, Item.ItemType.FOOD_2, levelManager);
                        Game.getInstance().items.add(food2);
                    } else if (chance < 1) {
                        Item food3 = new Item(x, y, 16, 16, Item.food3Img, Item.ItemType.FOOD_3, levelManager);
                        Game.getInstance().items.add(food3);
                    }
                }
            }
        }
    }

    private void setAnimation() {
        balloonAction = size.ordinal();
    }

    private void resetAniTick() {
        aniTick = 0;
        aniIndex = 0;
    }

    public boolean shouldCreateNewBalloons() {
        return shouldCreateNewBalloons;
    }

    public List<Balloon> createNewBalloons() {
        List<Balloon> newBalloons = new ArrayList<>();
        shouldCreateNewBalloons = false;

        BalloonSize nextSize = getNextSize();
        if (nextSize == null) return newBalloons; 

        for (int i = 0; i < 2; i++) {
            float offsetX = (i == 0) ? -20 : 20;
            Balloon b = new Balloon(x + offsetX, y, nextSize, levelManager);

            b.xSpeed = (i == 0 ? -maxXSpeed : +maxXSpeed);

            newBalloons.add(b);
        }

        return newBalloons;
    }


    private BalloonSize getNextSize() {
        switch (size) {
            case BIG:    return BalloonSize.MID;
            case MID:    return BalloonSize.SMALL;
            case SMALL:  return BalloonSize.XSMALL;
            case XSMALL: return null;
            default:     return null;
        }
    }

    private void loadBalloons() {
        balloonImages = new BufferedImage[4];
        try {
            balloonImages[BIG]    = ImageIO.read(new File("res/big.png"));
            balloonImages[MID]    = ImageIO.read(new File("res/mid.png"));
            balloonImages[SMALL]  = ImageIO.read(new File("res/small.png"));
            balloonImages[XSMALL] = ImageIO.read(new File("res/xsmall.png"));
        } catch (IOException e) {
            System.err.println("Error loading balloon images");
            e.printStackTrace();
        }
    }

    private void loadAnimations() {
        animations = new BufferedImage[4][];
        loadAnimationFrames("res/exp_big.png",    BIG,    4, 20, 46);
        loadAnimationFrames("res/exp_mid.png",    MID,    4, 15, 30);
        loadAnimationFrames("res/exp_small.png",  SMALL,  4, 10, 16);
        loadAnimationFrames("res/exp_xsmall.png", XSMALL, 4, 5,  8);
    }

    private void loadAnimationFrames(String path, int actionIndex, int frameCount, int frameWidth, int frameHeight) {
        try {
            BufferedImage sheet = ImageIO.read(new File(path));
            animations[actionIndex] = new BufferedImage[frameCount];
            for (int i = 0; i < frameCount; i++) {
                animations[actionIndex][i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
            }
        } catch (IOException e) {
            System.err.println("Error loading animation from: " + path);
            e.printStackTrace();
        }
    }

    private static int getWidthForSize(BalloonSize size) {
        switch (size) {
            case BIG:    return 49;
            case MID:    return 36;
            case SMALL:  return 20;
            case XSMALL: return 12;
            default:     return 20;
        }
    }

    private static int getHeightForSize(BalloonSize size) {
        switch (size) {
            case BIG:    return 46;
            case MID:    return 30;
            case SMALL:  return 16;
            case XSMALL: return 8;
            default:     return 16;
        }
    }

    public BalloonSize getSize() {
        return this.size;
    }
}
