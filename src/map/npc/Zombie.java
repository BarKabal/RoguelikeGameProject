package map.npc;


public class Zombie extends Monster {

    public Zombie(int xPos, int yPos) {
        x = xPos;
        y = yPos;
        maxHealth = 40;
        currentHealth = maxHealth;
        lvl = 2;
        drop = null;
        speed = 1.5;
        minDmg = 4;
        maxDmg = 6;
    }
}
