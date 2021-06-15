package logic.generation;

import logic.generation.cave.CaveAlgorithm;
import logic.generation.room.RoomAlgorithm;
import map.tiles.Entrance;
import map.tiles.Floor;
import map.tiles.Tile;
import map.tiles.Wall;

import java.util.ArrayList;
import java.util.Random;

public class EntryGenerator {
    int[][] map;
    int entryX;
    int entryY;
    Random rng;
    Tile[][] tiledMap;

    public EntryGenerator(int mapType, long seed) { //1 - room; 2 - cave
        switch (mapType) {
            case 1 -> {
                RoomAlgorithm roomAlgorithm = new RoomAlgorithm(seed);
                roomAlgorithm.generateRoom();
                map = new int[roomAlgorithm.returnSize()[0]][roomAlgorithm.returnSize()[1]];
                map = roomAlgorithm.returnMap();
            }
            case 2 -> {
                CaveAlgorithm caveAlgorithm = new CaveAlgorithm(seed);
                caveAlgorithm.generateCave();
                map = new int[caveAlgorithm.returnSize()[0]][caveAlgorithm.returnSize()[1]];
                map = caveAlgorithm.returnCave();
            }
        }
        rng = new Random(seed);
    }

    private void generateEntrance() {
        ArrayList<Integer[]> possibleCoordinates = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 0) {
                    possibleCoordinates.add(new Integer[]{i,j});
                }
            }
        }
        Integer[] tmp = possibleCoordinates.get(rng.nextInt(possibleCoordinates.size()));
        entryX = tmp[0];
        entryY = tmp[1];
        map[entryX][entryY] = 99;
    }

    public Tile[][] tileMap() {
        generateEntrance();
        tiledMap = new Tile[map.length][map[0].length];
        for (int i = 0; i < tiledMap.length; i++) {
            for (int j = 0; j < tiledMap[0].length; j++) {
                switch (map[i][j]) {
                    case 0: tiledMap[i][j] = new Floor(i,j); break;
                    case 1: tiledMap[i][j] = new Wall(i,j); break;
                    case 99: tiledMap[i][j] = new Entrance(i,j); break;
                }
            }
        }
        return tiledMap;
    }

    public int[] getEntry() { //x,y
        return new int[]{entryX,entryY};
    }
}
