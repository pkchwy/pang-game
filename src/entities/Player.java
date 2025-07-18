package entities;

import static utilz.Constants.PlayerConstants.*;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import entities.Item.ItemType;
import levels.LevelManager;
import main.Game;
import levels.Level;
import utilz.HelpMethods;

public class Player extends Entity {
	private BufferedImage[][] animations;
	private int aniTick, aniIndex, aniSpeed = 20;
	private int playerAction = IDLE;
	private boolean moving, atacking = false;
	private boolean left, up, right, down;
	private float playerSpeed = 1.2f;
	private boolean facingRight = true;
	private Arrow arrow;
	private List<Bullet> bullets = new ArrayList<>();
	private boolean hasGun = false;
	private static final int PLAYER_WIDTH = 32;
	private static final int PLAYER_HEIGHT = 32;
	private static final int SCALE=2;
	private LevelManager levelManager;
	

	private float gravity = 0.2f;
	private float fallSpeed = 0;
	private float maxFallSpeed = 10f;
	private boolean inAir = false;
	private boolean onLadder = false;
	private int health = 3;
	private BufferedImage healthIcon;
	private boolean isInvincible = false;
	private int invincibilityTimer = 0;
	private static final int INVINCIBILITY_DURATION = 180;

	public enum ArrowType {
		NORMAL,
		SPECIAL
	}

	private ArrowType activeArrowType = ArrowType.NORMAL;
	public long specialArrowEndTime = 0;
	private static final long SPECIAL_ARROW_DURATION = 6000; 

	public Player(float x, float y, LevelManager levelManager) {
		super(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);
		this.levelManager = levelManager;
		loadAnimations();
		loadHealthIcon();
		initHitbox(x, y, PLAYER_WIDTH*SCALE, PLAYER_HEIGHT*SCALE);
	}

	public void update() {
		if (isInvincible) {
			invincibilityTimer--;
			if (invincibilityTimer <= 0) {
				isInvincible = false;
			}
		}
		
		if (activeArrowType == ArrowType.SPECIAL && System.currentTimeMillis() > specialArrowEndTime) {
			activeArrowType = ArrowType.NORMAL;
		}
		
		updatePos();
		updateAnimationTick();
		setAnimation();
		updateHitbox();
		if (arrow != null) {
			arrow.update();
			if (!arrow.isActive())
				arrow = null;
		}
		
		for (int i = bullets.size() - 1; i >= 0; i--) {
			Bullet bullet = bullets.get(i);
			bullet.update();
			if (!bullet.isActive()) {
				bullets.remove(i);
			}
		}
	}
	
	public void render(Graphics g) {
		int drawX = (int) x;
		int drawY = (int) y;
		int width = PLAYER_WIDTH * SCALE;
		int height = PLAYER_HEIGHT * SCALE;

		BufferedImage currentFrame = animations[playerAction][aniIndex];
		Graphics2D g2d = (Graphics2D) g;

		if (isInvincible) {
			if (invincibilityTimer % 25 < 10) {
				if (facingRight) {
					g2d.drawImage(currentFrame, drawX + width, drawY, -width, height, null);
				} else {
					g2d.drawImage(currentFrame, drawX, drawY, width, height, null);
				}
			}
		} else {
			if (facingRight) {
				g2d.drawImage(currentFrame, drawX + width, drawY, -width, height, null);
			} else {
				g2d.drawImage(currentFrame, drawX, drawY, width, height, null);
			}
		}
		
		if (arrow != null)
			arrow.render(g);
		
		// Render bullets
		for (Bullet bullet : bullets) {
			bullet.render(g);
		}
	}

	private void updatePos() {
		moving = false;
		
		onLadder = false;
		for (GameObject obj : levelManager.getCurrentLevel().getObjects()) {
			if (obj.getType() == GameObject.ObjectType.STAIRS) {
				java.awt.geom.Rectangle2D.Float climbArea = obj.getClimbableArea();
				if (climbArea != null && hitbox.intersects(climbArea)) {
					onLadder = true;
					break;
				}
			}
		}

		if (left && !right) {
			if (HelpMethods.canMoveTo(x - playerSpeed, y, PLAYER_WIDTH*SCALE, PLAYER_HEIGHT*SCALE, levelManager.getCurrentLevel())) {
				x -= playerSpeed;
				moving = true;
				facingRight = true;
			}
		} else if (right && !left) {
			if (HelpMethods.canMoveTo(x + playerSpeed, y, PLAYER_WIDTH*SCALE, PLAYER_HEIGHT*SCALE, levelManager.getCurrentLevel())) {
				x += playerSpeed;
				moving = true;
				facingRight = false;
			}
		}

		if (onLadder) {
			if (up && !down) {
	
				if (HelpMethods.canMoveTo(x, y - playerSpeed, PLAYER_WIDTH*SCALE, PLAYER_HEIGHT*SCALE, levelManager.getCurrentLevel())) {
					y -= playerSpeed;
					moving = true;
					inAir = false;
					fallSpeed = 0;
				}
			} else if (down && !up) {
				if (HelpMethods.canMoveTo(x, y + playerSpeed, PLAYER_WIDTH*SCALE, PLAYER_HEIGHT*SCALE, levelManager.getCurrentLevel())) {
					y += playerSpeed;
					moving = true;
					inAir = false;
					fallSpeed = 0;
				}
			}
		}  else {
	        if (!inAir) {
	            boolean hasGround = false;
	            Rectangle2D.Float groundCheck = new Rectangle2D.Float(
	                hitbox.x,
	                hitbox.y + hitbox.height,
	                hitbox.width,
	                5     
	            );
	            for (GameObject obj : levelManager.getCurrentLevel().getObjects()) {
	                if (obj.getType()==GameObject.ObjectType.BORDER 
	                 || obj.getType()==GameObject.ObjectType.STONE) {
	                    if (groundCheck.intersects(obj.getHitbox())) {
	                        hasGround = true;
	                        break;
	                    }
	                }
	            }
	            if (!hasGround) {
	                inAir = true;
	                fallSpeed = 0;
	            }
	        }

	        if (inAir) {
	            fallSpeed = Math.min(fallSpeed + gravity, maxFallSpeed);
	            float nextY = y + fallSpeed;
	            if (HelpMethods.canMoveTo(x, nextY, PLAYER_WIDTH*SCALE, PLAYER_HEIGHT*SCALE,levelManager.getCurrentLevel())) {
	                y = nextY;
	            } else {
	                for (GameObject obj : levelManager.getCurrentLevel().getObjects()) {
	                    if (obj.getType()==GameObject.ObjectType.BORDER 
	                     || obj.getType()==GameObject.ObjectType.STONE) {
	                        Rectangle2D.Float objHB = obj.getHitbox();
	                        if (y + PLAYER_HEIGHT*SCALE <= objHB.y
	                         && nextY + PLAYER_HEIGHT*SCALE >= objHB.y) {
	                            y = objHB.y - PLAYER_HEIGHT*SCALE;
	                            inAir = false;
	                            fallSpeed = 0;
	                            break;
	                        }
	                    }
	                }
	            }
	        }
	    }

	   
	    updateHitbox();
	}

	private void updateAnimationTick() {
		aniTick++;
		if (aniTick < aniSpeed) return;
		aniTick = 0;
		aniIndex++;

		if (playerAction == ATACKING) {
			if (aniIndex == 1) {
				float pw = 32 * 2, ph = 32 * 2, fw = 17;
				float arrowX = x + (pw - fw) / 2;
				float arrowY = y + ph;
				
				if (activeArrowType == ArrowType.SPECIAL) {
					arrow = new SpecialArrow(arrowX, arrowY, Item.specialArrowImg, levelManager);
				} else {
					arrow = new Arrow(arrowX, arrowY, null , levelManager);
				}
			}
			if (aniIndex >= GetSpriteAmount(ATACKING)) {
				playerAction = IDLE;
				aniIndex     = 0;
			}
			return;  
		}

		if (aniIndex >= GetSpriteAmount(playerAction)) {
			aniIndex = 0;
		}
	}

	private void setAnimation() {
		int startAni = playerAction;

		if (playerAction == ATACKING) {
			// Keep attacking animation
		}
		else if (onLadder && (up || down)) {
			playerAction = JUMP;
		}
		else if (moving && (left || right)) {
			playerAction = RUNNING;
		}
		else {
			playerAction = IDLE;
		}

		if (startAni != playerAction) {
			resetAniTick();
		}
	}

	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;
	}
	
	public void shoot() {
		if (hasGun) {
			float pw = 32 * 2, ph = 32 * 2, fw = 16;
			float bulletX = x + (pw - fw) / 2;
			float bulletY = y + (ph / 2);
			Bullet newBullet = new Bullet(bulletX, bulletY, levelManager);
			bullets.add(newBullet);
			utilz.SoundManager.getInstance().playGunShootSound();
		} else {
			if (arrow == null || !arrow.isActive()) {
				playerAction = ATACKING;
				aniIndex = 0;
				aniTick = 0;
				utilz.SoundManager.getInstance().playShootSound();
			}
		}
	}

	private void loadAnimations() {
		animations = new BufferedImage[9][]; 

		loadAnimationFrames("res/idle_atackAni.png", IDLE, 1, 32, 32);
		loadAnimationFrames("res/upAni.png", JUMP, 4, 34, 32);
		loadAnimationFrames("res/walkAni.png", RUNNING, 5, 34, 32);
		loadAnimationFrames("res/deathAni.png", DEATH, 1, 41, 32);
		loadAnimationFrames("res/idle_atackAni.png", ATACKING, 2, 32, 32);
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

	private void loadHealthIcon() {
		try {
			healthIcon = ImageIO.read(new File("res/heal.png"));
		} catch (IOException e) {
			System.err.println("Error loading health icon: " + e.getMessage());
		}
	}

	public void takeDamage() {
		if (!isInvincible) {
			health--;
			isInvincible = true;
			invincibilityTimer = INVINCIBILITY_DURATION;
			utilz.SoundManager.getInstance().playHitSound();
			
			if (health <= 0) {
				Game.getInstance().setGameOver(true);
				utilz.SoundManager.getInstance().playHitSound();
			}
		}
	}

	public int getHealth() {
		return health;
	}

	public BufferedImage getHealthIcon() {
		return healthIcon;
	}

	public void resetDirBooleans() {
		left = false;
		right = false;
		up = false;
		down = false;
	}

	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

	public Arrow getArrow() {
		return arrow;
	}

	public float getY() {
		return y;
	}

	public int getHeight() {
		return PLAYER_HEIGHT * SCALE;
	}

	public void setX(int i) {
		this.x=i;
		
	}

	public void setY(int i) {
		this.y=i;
		
	}

	public void setLevelManager(LevelManager levelManager) {
		this.levelManager = levelManager;
	}

	public void activateSpecialArrow() {
		activeArrowType = ArrowType.SPECIAL;
		specialArrowEndTime = System.currentTimeMillis() + SPECIAL_ARROW_DURATION;
	}

	public ArrowType getActiveArrowType() {
		return activeArrowType;
	}

	public void setActiveArrowType(ArrowType type) {
		this.activeArrowType = type;
		if (type == ArrowType.NORMAL) {
			specialArrowEndTime = 0;
		}
	}

	public void setHasGun(boolean hasGun) {
		this.hasGun = hasGun;
	}

	public boolean hasGun() {
		return hasGun;
	}

	public List<Bullet> getBullets() {
		return bullets;
	}

	public Bullet getBullet() {
		if (bullets.isEmpty()) {
			return null;
		}
		return bullets.get(0);
	}
}
