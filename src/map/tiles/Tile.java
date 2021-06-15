package map.tiles;

public class Tile {
    protected int x, y;
    private boolean wall, occupiedMon, occupiedIte, visible, transparent;
    protected String asci;

    public void setWall(boolean wall) {
        this.wall = wall;
    }

    public void setOccupiedIte(boolean occupiedIte) {
        this.occupiedIte = occupiedIte;
    }

    public void setOccupiedMon(boolean occupiedMon) {
        this.occupiedMon = occupiedMon;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    public boolean isWall() {
        return wall;
    }

    public boolean isOccupiedMon() {
        return occupiedMon;
    }

    public boolean isOccupiedIte() {
        return occupiedIte;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isTransparent() {
        return transparent;
    }

    public String getAsci() {
        return asci;
    }
}