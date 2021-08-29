package logic.generation;

import logic.gameData.GameData;
import map.npc.Skeleton;
import map.npc.Zombie;
import map.npc.Monster;

public class MonsterGenerator {
    private GameData gameData;

    public MonsterGenerator(GameData gameData) {
        this.gameData = gameData;
    }

    public Monster generateMonster(int x, int y) {
        int itemType = gameData.getRng().nextInt(2);
        return switch (itemType) {
            case 0 -> new Skeleton(x, y);
            case 1 -> new Zombie(x, y);
            default -> null;
        };
    }
}
