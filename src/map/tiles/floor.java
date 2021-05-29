package map.tiles;

public class floor extends tile {
    public floor(int givenX, int givenY) {
        x = givenX;
        y = givenY;
        asci = '.';
        setWall(false);
        setTransparent(true);
    }
}
