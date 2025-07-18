package utilz;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class SoundManager {
    private static SoundManager instance;
    private Clip backgroundMusic;
    private Clip jumpSound;
    private Clip shootSound;
    private Clip hitSound;
    private Clip gameOverSound;
    private Clip itemCollectSound;
    private Clip balloonPopSound;
    private Clip stoneBreakSound;
    private Clip cubeGunHitSound;
    private Clip gunShootSound;
    
    private SoundManager() {
        loadSounds();
    }
    
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    private void loadSounds() {
        try {
            AudioInputStream backgroundStream = AudioSystem.getAudioInputStream(new File("res/sound/pang-040.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(backgroundStream);
            
            AudioInputStream shootStream = AudioSystem.getAudioInputStream(new File("res/sound/shoot.wav"));
            shootSound = AudioSystem.getClip();
            shootSound.open(shootStream);

            AudioInputStream hitStream = AudioSystem.getAudioInputStream(new File("res/sound/hit.wav"));
            hitSound = AudioSystem.getClip();
            hitSound.open(hitStream);

            AudioInputStream itemStream = AudioSystem.getAudioInputStream(new File("res/sound/PickUp.wav"));
            itemCollectSound = AudioSystem.getClip();
            itemCollectSound.open(itemStream);

            AudioInputStream popStream = AudioSystem.getAudioInputStream(new File("res/sound/BalloonPop.wav"));
            balloonPopSound = AudioSystem.getClip();
            balloonPopSound.open(popStream);

            AudioInputStream stoneStream = AudioSystem.getAudioInputStream(new File("res/sound/cubehit.wav"));
            stoneBreakSound = AudioSystem.getClip();
            stoneBreakSound.open(stoneStream);
            
            AudioInputStream cubeGunHitStream = AudioSystem.getAudioInputStream(new File("res/sound/cubeGunHit.wav"));
            cubeGunHitSound = AudioSystem.getClip();
            cubeGunHitSound.open(cubeGunHitStream);
            
            AudioInputStream gunShootStream = AudioSystem.getAudioInputStream(new File("res/sound/gunShoot.wav"));
            gunShootSound = AudioSystem.getClip();
            gunShootSound.open(gunShootStream);
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sounds: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void playBackgroundMusic() {
        if (backgroundMusic != null) {
            try {
                backgroundMusic.setFramePosition(0);
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            } catch (Exception e) {
                System.err.println("Error playing background music: " + e.getMessage());
            }
        }
    }
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            try {
                backgroundMusic.stop();
                backgroundMusic.setFramePosition(0);
            } catch (Exception e) {
                System.err.println("Error stopping background music: " + e.getMessage());
            }
        }
    }
    
    public void playJumpSound() {
        playSound(jumpSound);
    }
    
    public void playShootSound() {
        playSound(shootSound);
    }
    
    public void playHitSound() {
        playSound(hitSound);
    }
    
    public void playGameOverSound() {
        playSound(gameOverSound);
    }
    
    public void playItemCollectSound() {
        playSound(itemCollectSound);
    }
    
    public void playBalloonPopSound() {
        playSound(balloonPopSound);
    }
    
    public void playStoneBreakSound() {
        playSound(stoneBreakSound);
    }
    
    public void playCubeGunHitSound() {
        playSound(cubeGunHitSound);
    }
    
    public void playGunShootSound() {
        playSound(gunShootSound);
    }
    
    private void playSound(Clip clip) {
        if (clip != null) {
            try {
                clip.setFramePosition(0);
                clip.start();
            } catch (Exception e) {
                System.err.println("Error playing sound: " + e.getMessage());
            }
        }
    }
} 