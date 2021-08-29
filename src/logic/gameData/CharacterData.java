package logic.gameData;

import map.items.item;
import map.items.weapon;
import map.npc.Monster;

import java.util.ArrayList;
import java.util.Random;

public class CharacterData { //przechowuje dane postaci
    private double HP;
    private double maxHP;
    private int xp;
    private int lvl;
    private double STR, DEX, INT, CON, WIS, LUC;
    private ArrayList<item> inventory;
    private weapon equippedWeapon;
    private item mainArmor;
    private item helmet;
    private item shield;
    private item boots;
    private final int inventoryLimit;
    private int currentFloor; //0-9
    private int positionX;
    private int positionY;
    private double accuracy;
    private final Random rng;
    private double dodge;
    private boolean dead;

    public CharacterData(Random rng) {
        STR = 10;
        DEX = 10;
        INT = 10;
        CON = 10;
        WIS = 10;
        LUC = 10;
        lvl = 1;
        xp = 0;
        calculateMaxHP();
        calculateAccuracy();
        calculateDodge();
        HP = maxHP;
        inventoryLimit = 64;
        inventory = new ArrayList<>();
        this.rng = rng;
    }

    private void calculateDodge() {
        dodge = (LUC+DEX)/200;
    }

    private void calculateMaxHP() {
        maxHP = CON * 10;
    }

    private void calculateAccuracy() {
        accuracy = 0.85 + LUC/100;
    }

    public void takeDamage(double damage) {
        HP -= damage;
        if (HP <= 0) {
            dead = true;
        }
    }

    public void moveCharacter(int changeX, int changeY, GameData gameData) {
        Monster monster = gameData.getMapData().getDungeonFloor(currentFloor).checkForMonster(positionX + changeX, positionY + changeY);
        if (monster == null) {
            positionX += changeX;
            positionY += changeY;
        } else {
            if (rng.nextDouble() < accuracy * (1 - monster.getDodge())) {
                if (monster.reduceHealth(calculateDamage()) == -1) {    //monster dead
                    gainXp(monster.getXP());
                    gameData.getMapData().getDungeonFloor(currentFloor).removeMonster(monster.getKey());

                }
            }
        }
    }

    public double getActionTime() {
        return 10 / DEX;
    }

    public double getHP() {
        return HP;
    }

    public double getMaxHP() {
        return maxHP;
    }

    public double getSTR() {
        return STR;
    }

    public double getDEX() {
        return DEX;
    }

    public double getINT() {
        return INT;
    }

    public double getCON() {
        return CON;
    }

    public double getWIS() {
        return WIS;
    }

    public double getLUC() {
        return LUC;
    }

    public int getLvl() {
        return lvl;
    }

    public int getXp() {
        return xp;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public ArrayList<item> getInventoryContent() {
        return inventory;
    }

    public boolean checkForInventorySpace() {
        return inventory.size() < inventoryLimit;
    }

    public void addItemToInventory(item item) {
        inventory.add(item);
    }

    public void moveCharacterToAnotherFloor(int stairs, int newPosX, int newPosY) {
        if (stairs == -1) {
            currentFloor++;
        } else {
            currentFloor--;
        }
        positionX = newPosX;
        positionY = newPosY;
    }

    private double getBaseDamage() {
        return STR * 2 / 10;
    }

    private double calculateDamage() {
        if (equippedWeapon == null) {
            return getBaseDamage();
        } else {
            double[] dmg = equippedWeapon.minMaxDamage();
            return (int) dmg[0] + rng.nextInt((int) (dmg[1] - dmg[0]));
        }
    }

    public double getDodge() {
        return dodge;
    }

    public void equipItem(int index) {
        item item = inventory.get(index);
        switch (item.getClass().toString()) {
            case "class map.items.Shield" -> {
                item shieldOld = shield;
                shield = item;
                addItemToInventory(shieldOld);
            }
            case "class map.items.Sword" -> {
                item equippedWeaponOld = equippedWeapon;
                equippedWeapon = (weapon) item;
                addItemToInventory(equippedWeaponOld);
            }
            case "class map.items.Boots" -> {
                item bootsOld = boots;
                boots = item;
                addItemToInventory(bootsOld);
            }
            case "class map.items.Helmet" -> {
                item helmetOld = helmet;
                helmet = item;
                addItemToInventory(helmetOld);
            }
            case "class map.items.Armour" -> {
                item mainArmorOld = mainArmor;
                mainArmor = item;
                addItemToInventory(mainArmorOld);
            }
        }
        inventory.remove(item);
    }

    public item getBoots() {
        return boots;
    }

    public item getShield() {
        return shield;
    }

    public item getHelmet() {
        return helmet;
    }

    public item getMainArmor() {
        return mainArmor;
    }

    public weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    private void gainXp(int newXP) {
        xp += newXP;
        if (xp >= lvl * 1000) {
            levelUp();
            xp -= 1000;
        }
    }

    private void levelUp() {
        STR++;
        DEX++;
        INT++;
        CON++;
        WIS++;
        LUC++;
        lvl++;
        calculateMaxHP();
        calculateAccuracy();
        HP = maxHP;
    }

    public boolean getDead() {
        return dead;
    }
}