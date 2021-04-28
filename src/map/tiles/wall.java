package map.tiles;

public class wall extends tile {

    public wall(int givenX, int givenY) {
        x = givenX;
        y = givenY;
        asci = '#';
        setWall(true);
        setTransparent(false);
    }

}
