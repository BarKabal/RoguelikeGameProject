package logic.gameData;

import java.util.Random;

public class GameData { //przechowuje wszystkie dane gry
    private final CharacterData characterData;
    private MapData mapData;
    private double time;
    private final Random rng;
    private int gameType; //0-mixed, 1-dungeon, 2-cave

    public GameData(long seed) {
        rng = new Random(seed);
        characterData = new CharacterData(rng);
        time = 0;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public CharacterData getCharacterData() {
        return characterData;
    }

    public double getTime() {
        return time;
    }

    public Random getRng() {
        return rng;
    }

    public int getGameType() {
        return gameType;
    }

    public void generateMap() {
        mapData = new MapData(this);
        characterData.moveCharacter(mapData.getDungeonFloor(0).getEntranceX(), mapData.getDungeonFloor(0).getEntranceY(), this);
    }

    public MapData getMapData() {
        return mapData;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
