package map.tiles;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public abstract class Tile {
    protected int x, y, stairID;
    private boolean wall, occupiedMon, occupiedIte, visible, transparent, seen;
    protected String asci;
    protected int isStairs; //-1 for down, 0 for not stairs, 1 for up

    public void setWall(boolean wall) {
        this.wall = wall;
    }

    public boolean isWall() {
        return wall;
    }

    public boolean markAsVisible() {
        seen = true;
        visible = true;
        if (wall)
            return true;
        else
            return false;
    }

    public BufferedImage getPng() {
        try {
            String name = "src/graphics/tiles/" + this.getClass().getName().substring(10) + ".png";
            return ImageIO.read(new File(name));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int isStairs() {
        return isStairs;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void resetVisible() {
        visible = false;
    }

    public boolean getVisible() {
        return visible;
    }

    public boolean getSeen() {
        return seen;
    }
}