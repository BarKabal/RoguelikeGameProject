package logic.gameData;

import logic.generation.POISprinkler;
import map.items.item;
import map.npc.Monster;
import map.tiles.Tile;

import java.util.*;

public class DungeonFloor {
    private GameData gameData;
    private Tile[][] map;
    private HashMap<String, Monster> monsters;
    private HashMap<String, item> items;
    private ArrayList<Tile> stairsDowns;
    private ArrayList<Tile> stairsUps;
    private POISprinkler POISprinkler;
    private int entranceX, entranceY;
    private int floorNumber;
    private MonsterQueue monsterQueue;

    public DungeonFloor(GameData gameData, int floorNumber) {
        this.floorNumber = floorNumber;
        this.gameData = gameData;
        if (gameData.getGameType() == 0) {
            if (gameData.getRng().nextInt(2) == 0) {
                POISprinkler = new POISprinkler(1, gameData, this);
            } else {
                POISprinkler = new POISprinkler(2, gameData, this);
            }
        } else if (gameData.getGameType() == 1) {
            POISprinkler = new POISprinkler(1, gameData, this);
        } else if (gameData.getGameType() == 2) {
            POISprinkler = new POISprinkler(2, gameData, this);
        }
        map = POISprinkler.tileMap();
        monsters = POISprinkler.getMonstersGenerated();
        items = POISprinkler.getItemsGenerated();
        stairsDowns = POISprinkler.getStairsDowns();
        stairsUps = POISprinkler.getStairsUps();
        if (floorNumber == 0) {
            entranceX = POISprinkler.getEntry()[0];
            entranceY = POISprinkler.getEntry()[1];
        }
        monsterQueue = new MonsterQueue();
        monsterQueue.addMonster(null, gameData.getTime()+gameData.getCharacterData().getActionTime());
    }

    public Tile getTileFromMap(int x, int y) {
        return map[x][y];
    }

    public int getEntranceX() {
        return entranceX;
    }

    public int getEntranceY() {
        return entranceY;
    }

    public int[] getDimensions() {
        return new int[]{map.length, map[0].length};
    }

    public boolean isFloor0() {
        return floorNumber == 0;
    }

    public Monster checkForMonster(int x, int y) {
        String coor = x + "," + y;
        return monsters.get(coor);
    }

    public item checkForItem(int x, int y) {
        String coor = x + "," + y;
        return items.get(coor);
    }

    public void pickUpItem(int x, int y) {
        String coor = x + "," + y;
        items.remove(coor);
    }

    public ArrayList<Tile> getStairsDowns() {
        return stairsDowns;
    }

    public ArrayList<Tile> getStairsUps() {
        return stairsUps;
    }

    public void resetVisibility() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j].resetVisible();
            }
        }
    }

    public void passTimeToNextPLayerRound() {
        boolean nextPlayerRound = false;
        String initialKey;
        while (!nextPlayerRound) {
            gameData.setTime(monsterQueue.peekNextTime());
            Monster nextMonster = monsterQueue.getNextMonsterTurn();
            if (nextMonster == null) {
                nextPlayerRound = true;
                monsterQueue.addMonster(null, gameData.getCharacterData().getActionTime() + gameData.getTime());
            } else {
                initialKey = nextMonster.getX() + "," + nextMonster.getY();
                monsterQueue.addMonster(nextMonster, nextMonster.makeNextMove(gameData.getCharacterData().getPositionX(), gameData.getCharacterData().getPositionY(), gameData));
                monsters.remove(initialKey, nextMonster);
                if (nextMonster.reduceHealth(0) != -1) {
                    monsters.put(nextMonster.getX() + "," + nextMonster.getY(), nextMonster);
                } else {
                    monsterQueue.removeMonster(nextMonster);
                }
            }
        }
    }

    public void removeMonster(String monsterCoor) {
        monsters.remove(monsterCoor);
    }

    public void addMonsterIfNotPresentInQueue(Monster monster) {
        if (!monsterQueue.checkIfMonsterBelongs(monster) && monster != null) {
            monster.changeNextMove(gameData.getTime());
            monsterQueue.addMonster(monster,gameData.getTime() + monster.getSpeed());
        }
    }
}
