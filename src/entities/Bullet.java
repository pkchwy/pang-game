package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import levels.LevelManager;
import entities.GameObject;
import utilz.HelpMethods;
import java.awt.geom.Rectangle2D;

public class Bullet extends Entity {
    private BufferedImage bulletImage;
    private int scale = 2;
    private static final int BULLET_WIDTH = 16;
    private static final int BULLET_HEIGHT = 13;
    private LevelManager levelManager;
    private float ySpeed = -0.7f; 
    private boolean active = true;

    public Bullet(float x, float y, LevelManager levelManager) {
        super(x, y, BULLET_WIDTH, BULLET_HEIGHT);
        this.levelManager = levelManager;
        loadBulletImage();
        initHitbox(x,y,width*2,height*2);
    }

    private void loadBulletImage() {
        try {
            bulletImage = ImageIO.read(new File("res/bullet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startShoot(float x, float y) {
        this.x = x;
        this.y = y;
        this.active = true;
    }

    @Override
    public void update() {
        if (!active) return;
        updateHitbox();
        
        float nextY = y + ySpeed;
        if (HelpMethods.canMoveTo(x, nextY, (int)hitbox.width, (int)hitbox.height, levelManager.getCurrentLevel())) {
            y = nextY;
        } else {
            setActive(false);
            return;
        }

        if (!HelpMethods.canMoveTo(hitbox.x, hitbox.y, (int)hitbox.width, (int)hitbox.height, levelManager.getCurrentLevel())) {
            utilz.SoundManager.getInstance().playCubeGunHitSound();
            for (GameObject obj : levelManager.getCurrentLevel().getObjects()) {
                if (obj.getType() == GameObject.ObjectType.STONE && !obj.isStoneDestroyed() && hitbox.intersects(obj.getHitbox())) {
                    obj.hitStone();
                    setActive(false);
                    return;
                }
            }
            setActive(false);
        }
    }

    @Override
    public void render(Graphics g) {
        if (!active) return;
        if (bulletImage != null) {
            g.drawImage(bulletImage, (int)x, (int)y, BULLET_WIDTH * scale, BULLET_HEIGHT * scale, null);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
} 