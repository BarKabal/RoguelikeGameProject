package logic.generation;

import logic.gameData.DungeonFloor;
import logic.gameData.GameData;
import logic.generation.cave.CaveAlgorithm;
import logic.generation.room.RoomAlgorithm;
import map.items.item;
import map.npc.Monster;
import map.tiles.*;

import java.util.ArrayList;
import java.util.HashMap;

public class POISprinkler {
    int[][] map;
    int entryX;
    int entryY;
    Tile[][] tiledMap;
    HashMap<String, Monster> monstersGenerated;
    HashMap<String, item> itemsGenerated;
    private ArrayList<Tile> stairsDowns;
    private ArrayList<Tile> stairsUps;
    DungeonFloor floorData;
    GameData gameData;
    ArrayList<Integer[]> possibleCoordinates; //use .remove(int index) to get the element when adding stairs monsters etc.

    public POISprinkler(int mapType, GameData gameData, DungeonFloor floorData) { //1 - room; 2 - cave
        this.floorData = floorData;
        this.gameData = gameData;
        switch (mapType) {
            case 1 -> {
                RoomAlgorithm roomAlgorithm = new RoomAlgorithm(gameData);
                roomAlgorithm.generateRoom();
                map = new int[roomAlgorithm.returnSize()[0]][roomAlgorithm.returnSize()[1]];
                map = roomAlgorithm.returnMap();
            }
            case 2 -> {
                CaveAlgorithm caveAlgorithm = new CaveAlgorithm(gameData);
                caveAlgorithm.generateCave();
                map = new int[caveAlgorithm.returnSize()[0]][caveAlgorithm.returnSize()[1]];
                map = caveAlgorithm.returnCave();
            }
        }
        possibleCoordinates = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] != 1) {
                    possibleCoordinates.add(new Integer[]{i, j});
                }
            }
        }
        stairsDowns = new ArrayList<>();
        stairsUps = new ArrayList<>();
    }

    private void generateItems() {
        int itemCount = possibleCoordinates.size() / 160;
        itemsGenerated = new HashMap<>();
        ItemGenerator itemGenerator = new ItemGenerator(gameData);
        for (int i = 0; i < itemCount; i++) {
            Integer[] tmp = possibleCoordinates.remove(gameData.getRng().nextInt(possibleCoordinates.size()));
            item generatedItem = itemGenerator.generateItem();
            itemsGenerated.put(tmp[0] + "," + tmp[1], generatedItem);
        }
    }

    private void generateMonsters() {
        int monsterCount = possibleCoordinates.size() / 80;
        monstersGenerated = new HashMap<>();
        MonsterGenerator monsterGenerator = new MonsterGenerator(gameData);
        for (int i = 0; i < monsterCount; i++) {
            Integer[] tmp = possibleCoordinates.remove(gameData.getRng().nextInt(possibleCoordinates.size()));
            Monster generatedMonster = monsterGenerator.generateMonster(tmp[0], tmp[1]);
            monstersGenerated.put(tmp[0] + "," + tmp[1], generatedMonster);
        }
    }

    private void generateStairs() {
        Integer[] tmp;
        if (floorData.isFloor0()) {
            tmp = possibleCoordinates.remove(gameData.getRng().nextInt(possibleCoordinates.size()));
            map[tmp[0]][tmp[1]] = 99;
            entryX = tmp[0];
            entryY = tmp[1];
        }
        for (int i = 0; i < 3; i++) {
            tmp = possibleCoordinates.remove(gameData.getRng().nextInt(possibleCoordinates.size()));
            map[tmp[0]][tmp[1]] = 89;
            if (!floorData.isFloor0()) {
                tmp = possibleCoordinates.remove(gameData.getRng().nextInt(possibleCoordinates.size()));
                map[tmp[0]][tmp[1]] = 79;
            }
        }
    }

    public Tile[][] tileMap() {
        generateStairs();
        generateItems();
        generateMonsters();
        int stairUpGenerated = 0;
        int stairDownGenerated = 0;
        tiledMap = new Tile[map.length][map[0].length];
        for (int i = 0; i < tiledMap.length; i++) {
            for (int j = 0; j < tiledMap[0].length; j++) {
                switch (map[i][j]) {
                    default -> tiledMap[i][j] = new Floor(i, j);
                    case 1 -> tiledMap[i][j] = new Wall(i, j);
                    case 79 -> {
                        stairsUps.add(new StairsUp(i, j, stairUpGenerated));
                        tiledMap[i][j] = stairsUps.get(stairUpGenerated);
                        stairUpGenerated++;
                    }
                    case 89 -> {
                        stairsDowns.add(new StairsDown(i, j, stairDownGenerated));
                        tiledMap[i][j] = stairsDowns.get(stairDownGenerated);
                        stairDownGenerated++;

                    }
                    case 99 -> tiledMap[i][j] = new Entrance(i, j);
                }
            }
        }
        return tiledMap;
    }

    public int[] getEntry() { //x,y
        return new int[]{entryX, entryY};
    }

    public DungeonFloor getFloorData() {
        return floorData;
    }

    public HashMap<String, Monster> getMonstersGenerated() {
        return monstersGenerated;
    }

    public HashMap<String, item> getItemsGenerated() {
        return itemsGenerated;
    }

    public ArrayList<Tile> getStairsDowns() {
        return stairsDowns;
    }

    public ArrayList<Tile> getStairsUps() {
        return stairsUps;
    }
}
