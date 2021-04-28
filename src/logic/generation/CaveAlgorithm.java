package logic.generation;

import java.util.Random;

public class CaveAlgorithm {
    Random rng;
    int xSize = 30;
    int ySize = 60;
    double chance = 0.45;
    static long seed = 7;
    int iterations = 5;

    public CaveAlgorithm(long seed){
        rng = new Random(seed);
    }

    public int[][] generateCave() {
        int[][] cave = new int[xSize][ySize];
        cave = generateInitial(cave);
        for (int i = 0; i < iterations; i++) {
            boolean lastIter = false;
            if (i == iterations-1) {
                lastIter = true;
            }
            cave = reiterate(cave, lastIter);
        }
        return cave;
    }

    public int[][] generateInitial(int[][] cave){
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                if (i == 0 || j == 0 || i == xSize - 1 || j == ySize - 1) {
                    cave[i][j] = 1;
                } else {
                    if (rng.nextDouble() < chance) {
                        cave[i][j] = 1;
                    } else {
                        cave[i][j] = 0;
                    }
                }
            }
        }
        return cave;
    }

    public int[][] reiterate(int[][] cave, boolean lastIter) {
        Random check = new Random(seed);
        int[][] neighbour = new int[xSize][ySize];
        for (int i = 1; i < xSize-1; i++) {
            for (int j = 1; j < ySize-1; j++) {
                for (int n = -1; n <= 1; n++) {
                    for (int m = -1; m <= 1; m++) {
                        neighbour[i][j] += cave[i+n][j+m];
                    }
                }
            }
        }
        for (int i = 1; i < xSize-1; i++) {
            for (int j = 1; j < ySize-1; j++) {
                if (neighbour[i][j] >= 5) {
                    cave[i][j] = 1;
                } else if (neighbour[i][j] == 0 && !lastIter) {
                    if (check.nextDouble() > 0.5) {
                        cave[i][j] = 1;
                    }
                } else {
                    cave[i][j] = 0;
                }
            }
        }
        return cave;
    }

    public static void main(String[] args) {
        CaveAlgorithm ca = new CaveAlgorithm(seed);
        int[][] cave = ca.generateCave();
        for (int i = 0; i < ca.xSize; i++) {
            for (int j = 0; j < ca.ySize; j++) {
                if (cave[i][j] == 1) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.print("\n");
        }
    }
}


