package map.tiles;

public class Floor extends Tile {
    public Floor(int givenX, int givenY) {
        x = givenX;
        y = givenY;
        asci = ".";
        setWall(false);
    }
}
