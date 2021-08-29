package logic.generation;

import logic.gameData.GameData;
import map.items.Armour;
import map.items.Boots;
import map.items.Helmet;
import map.items.item;
import map.items.Shield;
import map.items.Sword;

public class ItemGenerator { //future more complex item generation goes here
    private final GameData gameData;

    public ItemGenerator(GameData gameData) {
        this.gameData = gameData;
    }

    public item generateItem() {
        int itemType = gameData.getRng().nextInt(5);
        return switch (itemType) {
            case 0 -> new Armour();
            case 1 -> new Boots();
            case 2 -> new Helmet();
            case 3 -> new Shield();
            case 4 -> new Sword();
            default -> null;
        };
    }
}
