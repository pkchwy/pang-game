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

public class Arrow extends Item {
    private BufferedImage[] animations;
    private int aniTick = 0, aniIndex = 0, aniSpeed = 6;
    private int scale = 2;
    private static final int ARROW_WIDTH = 17;
    private static final int ARROW_HEIGHT = 33;
    private LevelManager levelManager;

    public Arrow(float x, float y, BufferedImage image, LevelManager levelManager) {
        super(x, y, ARROW_WIDTH * 2, ARROW_HEIGHT * 2, image, ItemType.NORMAL_ARROW, levelManager);
        this.levelManager = levelManager;
        loadAnimations();
        initHitbox(x, y, ARROW_WIDTH * 2, ARROW_HEIGHT * 2);
    }

    private void loadAnimations() {
        animations = new BufferedImage[70];
        for (int i = 0; i < 23; i++) {
            try {
                String path = String.format("res/arrow1/cut_%03d.png", i + 1);
                File file = new File(path);
                if (!file.exists()) {
                    System.err.println("File not found: " + path);
                    continue;
                }
                animations[i] = ImageIO.read(file);
                if (animations[i] == null) {
                    System.err.println("Failed to load image: " + path);
                }
            } catch (IOException e) {
                System.err.println("Error loading frame: " + e.getMessage());
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 23; i++) {
            try {
                String path = String.format("res/arrow2/slice_%02d.png", i + 1);
                File file = new File(path);
                if (!file.exists()) {
                    System.err.println("File not found: " + path);
                    continue;
                }
                animations[i + 23] = ImageIO.read(file);
                if (animations[i + 23] == null) {
                    System.err.println("Failed to load image: " + path);
                }
            } catch (IOException e) {
                System.err.println("Error loading frame: " + e.getMessage());
                e.printStackTrace();
            }
        }

        for (int i = 0; i < 24; i++) {
            try {
                String path = String.format("res/arrow3/slice_%02d.png", i + 1);
                File file = new File(path);
                if (!file.exists()) {
                    System.err.println("File not found: " + path);
                    continue;
                }
                animations[i + 46] = ImageIO.read(file);
                if (animations[i + 46] == null) {
                    System.err.println("Failed to load image: " + path);
                }
            } catch (IOException e) {
                System.err.println("Error loading frame: " + e.getMessage());
                e.printStackTrace();
            }
        }

        for (int i = 0; i < animations.length; i++) {
            if (animations[i] == null) {
                System.err.println("Warning: Frame " + i + " is null");
            }
        }
    }

    @Override
    public void update() {
        if (!isActive()) return;
        updateHitbox();
        if (aniIndex < animations.length && animations[aniIndex] != null) {
            BufferedImage frame = animations[aniIndex];
            int frameWidth = frame.getWidth();
            int frameHeight = frame.getHeight();
            hitbox.x = x;
            hitbox.y = y - frameHeight * scale;
            hitbox.width = frameWidth * scale;
            hitbox.height = frameHeight * scale;
        }
        // Collision check
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
        
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= animations.length) {
                setActive(false);
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (!isActive()) return;
        if (aniIndex >= animations.length || animations[aniIndex] == null) {
            setActive(false);
            return;
        }
        BufferedImage frame = animations[aniIndex];
        int fw = frame.getWidth(), fh = frame.getHeight();
        int drawX = (int) x;
        int drawY = (int) (y - fh * scale);
        g.drawImage(frame, drawX, drawY, fw * scale, fh * scale, null);
        
    }

    public boolean isActive() {
        return super.isActive();
    }

	public void setActive(boolean b) {
		super.setActive(b);
		
	}
}
