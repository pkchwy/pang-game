package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import levels.LevelManager;

public class SpecialArrow extends Arrow {
    private long landedTime = -1;
    private long stayDuration = 3500; // ms
    private boolean landed = false;

    public SpecialArrow(float x, float y, BufferedImage image, LevelManager levelManager) {
        super(x, y, image, levelManager);
    }

    @Override
    public void update() {
        if (!isActive()) return;
        if (!landed) {
            super.update();
            if (!isActive()) {
                landed = true;
                landedTime = System.currentTimeMillis();
                setActive(true); 
            }
        } else {
            if (System.currentTimeMillis() - landedTime > stayDuration) {
                setActive(false);
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (!isActive()) return;
        super.render(g);
    }
} 