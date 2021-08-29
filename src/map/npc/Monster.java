package map.npc;

import logic.gameData.DungeonFloor;
import logic.gameData.GameData;
import map.items.item;
import map.tiles.Tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Monster {
    protected int x, y;
    protected double currentHealth;
    protected double maxHealth;
    protected int lvl;
    protected item drop;
    protected double speed;
    protected double accuracy;
    protected double minDmg;
    protected double maxDmg;
    protected double lastMove;
    protected double nextMove;
    protected double dodge; //% chance to dodge

    public int reduceHealth(double hp) {
        currentHealth -= hp;
        if (currentHealth < 0) {
            return -1;
        } else return 0;
    }

    public BufferedImage getPng() {
        try {
            String name = "src/graphics/npc/" + this.getClass().getName().substring(8) + ".png";
            return ImageIO.read(new File(name));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public double makeNextMove(int playerX, int playerY, GameData gameData) {
        if (checkForTouch(playerX, playerY)) {
            if (accuracy * (1 - gameData.getCharacterData().getDodge()) < gameData.getRng().nextDouble()) {
                gameData.getCharacterData().takeDamage(minDmg + gameData.getRng().nextInt((int) (maxDmg - minDmg)));
            }
        } else {
            bresenhamForOneMove(playerX, playerY, gameData);
        }
        lastMove = nextMove;
        nextMove = lastMove + speed;
        return nextMove;
    }

    private void moveOnMap(int xChange, int yChange, DungeonFloor dungeonFloor) {
        if (!dungeonFloor.getTileFromMap(x+xChange, y+yChange).isWall() && dungeonFloor.checkForMonster(x+xChange, y+yChange) == null) {
            x += xChange;
            y += yChange;
        }
    }

    private void bresenhamForOneMove(int x2, int y2, GameData gameData) {
        int dx = Math.abs(x2 - x);
        int dy = Math.abs(y2 - y);
        int s1 = sign(x2 - x);
        int s2 = sign(y2 - y);
        boolean interchange;
        if (dy > dx) { //dx zawsze większe od dy (jeśli zamienione to interchange)
            int tmp = dx;
            dx = dy;
            dy = tmp;
            interchange = true;
        } else {
            interchange = false;
        }
        if (2 * dy - dx < 0) {
            if (interchange) {
                moveOnMap(0,s2,gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()));
            } else {
                moveOnMap(s1,0,gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()));
            }
        } else {
            moveOnMap(s1,s2,gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()));
        }

    }

    private int sign(int num) {
        if (num < 0) {
            return -1;
        } else if (num > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public double getDodge() {
        return dodge;
    }

    public int getXP() {
        return lvl * 20;
    }

    public String getKey() {
        return x + "," + y;
    }

    public double getSpeed() {
        return speed;
    }

    private boolean checkForTouch(int xPos, int yPos) {
        if (x >= xPos - 1 && x <= xPos + 1) {
            if (y >= yPos - 1 && y <= yPos + 1) {
                return true;
            }
        }
        return false;
    }

    public void changeNextMove(double nowTime) {
        if (nextMove == 0)
            nextMove = nowTime+speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getNextMove() {
        return nextMove;
    }
}
