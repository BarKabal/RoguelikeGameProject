package logic.gameData;

import java.util.ArrayList;

public class MapData { //przechowuje wszystkie dane mapy, ustawienie potworów, przedmiotów, gracza
    private final ArrayList<DungeonFloor> dungeonFloors;
    GameData gameData;

    public MapData(GameData gameData) {
        this.gameData = gameData;
        dungeonFloors = new ArrayList<>();
        int dungeonLength = 10;
        for (int i = 0; i < dungeonLength; i++) {
            System.out.print(i + "\n");
            dungeonFloors.add(new DungeonFloor(gameData, i));
        }
    }

    public DungeonFloor getDungeonFloor(int number) {
        return dungeonFloors.get(number);
    }

    public DungeonFloor getFloorDown(int number) {
        return dungeonFloors.get(number+1);
    }

    public DungeonFloor getFloorUp(int number) {
        return dungeonFloors.get(number-1);
    }
}
