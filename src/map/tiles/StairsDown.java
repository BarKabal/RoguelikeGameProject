package map.tiles;

public class StairsDown extends Tile {

    public StairsDown(int givenX, int givenY, int stairID) {
        x = givenX;
        y = givenY;
        asci = ">";
        setWall(false);
        isStairs = -1;
        this.stairID = stairID;
    }
}
