package map.tiles;

public class Entrance extends Tile {
    public Entrance(int givenX, int givenY) {
        x = givenX;
        y = givenY;
        asci = "A";
        setWall(false);
    }
}
