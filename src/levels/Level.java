package levels;
import java.awt.Image;
import java.util.List;
import java.util.ArrayList;
import entities.GameObject;

public class Level extends Object {
    private Image background;
    private List<GameObject> objects;
    private int playerStartX, playerStartY;

    public Level(Image background) {
        super();
        this.background = background;
        this.objects = new ArrayList<>();
    }

    public Image getBackground() {
        return background;
    }

    public List<GameObject> getObjects() {
        return objects;
    }

    public void setObjects(List<GameObject> objects) {
        this.objects = objects;
    }

    public int getPlayerStartX() {
        return playerStartX;
    }
    public void setPlayerStartX(int x) {
        this.playerStartX = x;
    }
    public int getPlayerStartY() {
        return playerStartY;
    }
    public void setPlayerStartY(int y) {
        this.playerStartY = y;
    }
} 