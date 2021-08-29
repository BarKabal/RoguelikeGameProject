package logic.gameData;

import map.npc.Monster;

import java.util.HashMap;
import java.util.PriorityQueue;

public class MonsterQueue {
    private final PriorityQueue<Double> queue;
    private final HashMap<Double, Monster> monsters; //monster is null if players turn

    public MonsterQueue() {
        queue = new PriorityQueue<>();
        monsters = new HashMap<>();
    }

    public void addMonster(Monster monster, Double nextTurn) {
        queue.add(nextTurn);
        monsters.put(nextTurn, monster);
    }

    public Monster getNextMonsterTurn() {
        return monsters.remove(queue.poll());
    }

    public boolean checkIfMonsterBelongs(Monster monster) {
        return monsters.containsValue(monster);
    }

    public double peekNextTime() {
        return queue.element();
    }

    public void removeMonster(Monster monster) {
        queue.remove(monster.getNextMove());
        monsters.remove(monster.getNextMove(), monster);
    }

    public int getLength() {
        return queue.size();
    }

}
