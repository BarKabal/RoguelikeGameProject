package map.npc;

public class Skeleton extends Monster {

    public Skeleton(int xPos, int yPos) {
        x = xPos;
        y = yPos;
        maxHealth = 20;
        currentHealth = maxHealth;
        lvl = 1;
        drop = null;
        speed = 1.123;
        minDmg = 1;
        maxDmg = 3;
    }
}
