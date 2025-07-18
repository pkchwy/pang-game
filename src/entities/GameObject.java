package entities;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.Rectangle2D;

public class GameObject extends Entity {
    
    public enum ObjectType {
        STAIRS,
        STONE,
        STONE2,
        BORDER
    }
    
    private ObjectType type;
    private BufferedImage borderImage;
    private BufferedImage ladderImage;
    private BufferedImage[] stoneFrames;
    private BufferedImage stone2Image;
    private int stoneFrameIndex = 0;
    private int stoneAnimationTick = 0;
    private static final int STONE_ANIMATION_SPEED = 20;
    boolean isStoneHit = false;
    private boolean isStoneDestroyed = false;
    
    public GameObject(float x, float y, int width, int height, ObjectType type) {
        super(x, y, width, height);
        this.type = type;
        if(type == ObjectType.BORDER || type == ObjectType.STAIRS)
        	initHitbox(x, y, width, height);
        if(type == ObjectType.STONE || type == ObjectType.STONE2)
        	initHitbox(x,y,width*2,height*2);
        try {
            borderImage = ImageIO.read(new File("res/border.png"));
            ladderImage = ImageIO.read(new File("res/ladder1.png"));

            if (type == ObjectType.STONE) {
                BufferedImage stoneSprite = ImageIO.read(new File("res/platform.png"));
                stoneFrames = new BufferedImage[4];
                for (int i = 0; i < 4; i++) {
                    stoneFrames[i] = stoneSprite.getSubimage(i * 32, 0, 32, 8);
                }
            } else if (type == ObjectType.STONE2) {
                stone2Image = ImageIO.read(new File("res/platform1.png"));
            }
        } catch (IOException e) {
            System.out.println("Images could not be loaded: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        updateHitbox();

        if (type == ObjectType.STONE && isStoneHit && !isStoneDestroyed) {
            stoneAnimationTick++;
            if (stoneAnimationTick >= STONE_ANIMATION_SPEED) {
                stoneAnimationTick = 0;
                stoneFrameIndex++;
                if (stoneFrameIndex >= 4) {
                    isStoneDestroyed = true;
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        switch (type) {
            case BORDER:
                int borderImgWidth = borderImage.getWidth();
                int borderImgHeight = borderImage.getHeight();
                g.drawImage(borderImage, (int)x, (int)y, borderImgWidth, borderImgHeight, null);
                break;
            case STAIRS:
                int ladderImgWidth = ladderImage.getWidth();
                int ladderImgHeight = ladderImage.getHeight();
                g.drawImage(ladderImage, (int)x, (int)y, ladderImgWidth, ladderImgHeight, null);
                
                break;
            case STONE:
                if (isStoneDestroyed) {
                    return;
                }
                if (stoneFrames != null && stoneFrames.length > 0 && stoneFrames[stoneFrameIndex] != null) {
                    g.drawImage(stoneFrames[stoneFrameIndex], (int)x, (int)y, width*2, height*2, null);
                }
                break;
            case STONE2:
                    g.drawImage(stone2Image, (int)x, (int)y, width*2, height*2, null);
                break;
        }
    }
    
    public void hitStone() {
        if (type == ObjectType.STONE && !isStoneHit) {
            isStoneHit = true;
            stoneFrameIndex = 0;
            utilz.SoundManager.getInstance().playStoneBreakSound();
        }
    }
    
    public boolean isStoneDestroyed() {
        return isStoneDestroyed;
    }
    
    public ObjectType getType() {
        return type;
    }

    public Rectangle2D.Float getClimbableArea() {
        if (type == ObjectType.STAIRS) {
            float climbX = x + width * 0.49f;
            float climbWidth = width * 0.01f;
            return new java.awt.geom.Rectangle2D.Float(climbX, y, climbWidth, height);
        }
        return null;
    }

    public void setStoneHit(boolean hit) {
        if (type == ObjectType.STONE && !isStoneDestroyed) {
            isStoneHit = hit;
            if (hit) stoneFrameIndex = 0;
        }
    }
} 