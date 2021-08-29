package map.tiles;

public class Wall extends Tile {

    public Wall(int givenX, int givenY) {
        x = givenX;
        y = givenY;
        asci = "#";
        setWall(true);
    }

}
