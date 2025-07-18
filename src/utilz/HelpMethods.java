package utilz;

import java.awt.geom.Rectangle2D;
import levels.Level;
import entities.GameObject;


public class HelpMethods {
    public static boolean canMoveTo(float newX, float newY, int width, int height, Level level) {
        Rectangle2D.Float futureHitbox = new Rectangle2D.Float(newX, newY, width, height);
        for (GameObject obj : level.getObjects()) {
            if ((obj.getType() == GameObject.ObjectType.BORDER || obj.getType() == GameObject.ObjectType.STONE || obj.getType()==GameObject.ObjectType.STONE2) 
                && !obj.isStoneDestroyed() 
                && futureHitbox.intersects(obj.getHitbox())) {
                return false;
            }
        }
        return true;
    }
} 