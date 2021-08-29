package map.tiles;

public class StairsUp extends Tile {

    public StairsUp(int givenX, int givenY, int stairID) {
        x = givenX;
        y = givenY;
        asci = "<";
        setWall(false);
        isStairs = 1;
        this.stairID = stairID;
    }
}
