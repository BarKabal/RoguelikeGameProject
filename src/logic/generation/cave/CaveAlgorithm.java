package logic.generation.cave;

import java.util.Random;

public class CaveAlgorithm {
    Random rng;
    int xSize = 100;
    int ySize = 100;
    double chance = 0.45;
    static long seed = 1;
    int iterations = 5;
    boolean changes = true;

    public CaveAlgorithm(long seed){
        rng = new Random(seed);
    }

    public int[][] generateCave() {
        int[][] cave = new int[xSize][ySize];
        cave = generateInitial(cave);
        for (int i = 0; i < iterations; i++) {
            boolean lastIter = i == iterations - 1;
            cave = reiterate(cave, lastIter);
        }
        cave = findFill(cave, 0, 2);
        while (changes) {
            changes = false;
            cave = findCavern(cave);
            cave = findFill(cave, 2, 0);
            cave = findFill(cave, 0, 2);
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
                    if (check.nextDouble() > 0.40) {
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
                } else if (cave[i][j] == 2){
                    System.out.print(",");
                } else if (cave[i][j] == 0){
                    System.out.print(".");
                } else if (cave[i][j] == 3){
                    System.out.print("0");
                }
            }
            System.out.print("\n");
        }
    }

    public int[][] findFill(int[][] cave, int colorStart, int colorEnd) {
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                if (cave[i][j] == colorStart) {
                    floodFill(cave, i, j, colorEnd);
                    return cave;
                }
            }
        }
        return cave;
    }

    public int[][] findCavern(int[][] cave) {
        boolean foundStart = false;
        int minX = 0, maxX = 0, minY = 0, maxY = 0;
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                if (cave[i][j] == 0 && !foundStart) {
                    foundStart = true;
                    minY = i;
                    minX = i;
                    minX = j;
                    maxX = j;
                }
                if (cave[i][j] == 0 && foundStart) {
                    if (j < minX) {
                        minX = j;
                    }
                    if (j > maxX) {
                        maxX = j;
                    }
                    if (i > maxY) {
                        maxY = i;
                    }
                }
            }
        }
        cave = findTunnelSpot(cave, minX, minY, maxX, maxY);
        return cave;
    }

    public int[][] findTunnelSpot(int[][] cave, int minPosX, int minPosY, int maxPosX, int maxPosY) {
        int minTunnelLength = xSize + ySize;
        int[] tunnelCoordinates = new int[3];   //[rząd/kolumna,początek,koniec]
        boolean horizontalTunnel = false;
        for (int i = minPosX; i <= maxPosX; i++) {
            int[] res = scanForVerticalTunnel(getColumn(cave, i));
            if (Math.abs(res[0] - res[1]) < minTunnelLength && Math.abs(res[0] - res[1]) != 0) {
                tunnelCoordinates[0] = i;
                tunnelCoordinates[1] = res[0];
                tunnelCoordinates[2] = res[1];
                minTunnelLength = Math.abs(tunnelCoordinates[2] - tunnelCoordinates[1]);
            }
        }
        for (int i = minPosY; i <= maxPosY; i++) {
            int[] res = scanForHorizontalTunnel(cave[i]);
            if (Math.abs(res[0] - res[1]) < minTunnelLength && Math.abs(res[0] - res[1]) != 0) {
                tunnelCoordinates[0] = i;
                tunnelCoordinates[1] = res[0];
                tunnelCoordinates[2] = res[1];
                minTunnelLength = Math.abs(tunnelCoordinates[2] - tunnelCoordinates[1]);
                horizontalTunnel = true;
            }
        }
        if (horizontalTunnel) {
            cave = createHorizontalTunnel(cave, tunnelCoordinates);
        } else {
            cave = createVerticalTunnel(cave, tunnelCoordinates);
        }
        return cave;
    }

    public int[] scanForHorizontalTunnel(int[] cavePart) {
        boolean availableRightTunnel = false;
        boolean availableLeftTunnel = false;
        boolean leftTunnelDone = false;
        boolean rightTunnelDone = false;
        int leftTunnelStart = 0;
        int rightTunnelStart = 0;
        int leftTunnelEnd = 0;
        int rightTunnelEnd = 0;
        for (int i = 0; i < ySize; i++) {
            if (cavePart[i] == 2 && cavePart[i+1] == 1 && !leftTunnelDone) {
                availableLeftTunnel = true;
                leftTunnelStart = i;
            }
            if (cavePart[i] == 0 && cavePart[i+1] == 1 && !rightTunnelDone) {
                availableRightTunnel = true;
                rightTunnelStart = i;
            }
            if (availableLeftTunnel && cavePart[i] == 0 && !leftTunnelDone) {
                leftTunnelEnd = i;
                leftTunnelDone = true;
            }
            if (availableRightTunnel && cavePart[i] == 2 && !rightTunnelDone) {
                rightTunnelEnd = i;
                rightTunnelDone = true;
            }
        }
        if (leftTunnelStart == 0 && rightTunnelEnd == 0) {
            return new int[] {xSize+ySize, 0};
        } else if (leftTunnelStart == 0) {
            return new int[] {rightTunnelStart, rightTunnelEnd};
        } else if (rightTunnelEnd == 0) {
            return new int[] {leftTunnelStart, leftTunnelEnd};
        } else {
            if (leftTunnelStart - leftTunnelEnd > rightTunnelStart - rightTunnelEnd) {
                return new int[] {rightTunnelStart, rightTunnelEnd};
            } else {
                return new int[] {leftTunnelStart, leftTunnelEnd};
            }
        }
    }

    public int[] scanForVerticalTunnel(int[] cavePart) {
        boolean availableLowerTunnel = false;
        boolean availableUpperTunnel = false;
        boolean upperTunnelDone = false;
        boolean lowerTunnelDone = false;
        int upperTunnelStart = 0;
        int lowerTunnelStart = 0;
        int upperTunnelEnd = 0;
        int lowerTunnelEnd = 0;
        for (int i = 0; i < xSize; i++) {
            if (cavePart[i] == 2 && cavePart[i+1] == 1 && !upperTunnelDone) {
                availableUpperTunnel = true;
                upperTunnelStart = i;
            }
            if (cavePart[i] == 0 && cavePart[i+1] == 1 && !lowerTunnelDone) {
                availableLowerTunnel = true;
                lowerTunnelStart = i;
            }
            if (availableUpperTunnel && cavePart[i] == 0 && !upperTunnelDone) {
                upperTunnelEnd = i;
                upperTunnelDone = true;
            }
            if (availableLowerTunnel && cavePart[i] == 2 && !lowerTunnelDone) {
                lowerTunnelEnd = i;
                lowerTunnelDone = true;
            }
        }
        if (upperTunnelStart == 0 && lowerTunnelEnd == 0) {
            return new int[] {xSize+ySize, 0};
        } else if (upperTunnelStart == 0) {
            return new int[] {upperTunnelStart, upperTunnelEnd};
        } else if (lowerTunnelEnd == 0) {
            return new int[] {lowerTunnelStart, lowerTunnelEnd};
        } else {
            if (upperTunnelEnd - upperTunnelStart > lowerTunnelEnd - lowerTunnelStart) {
                return new int[] {lowerTunnelStart, lowerTunnelEnd};
            } else {
                return new int[] {upperTunnelStart, upperTunnelEnd};
            }
        }
    }

    public int[][] createHorizontalTunnel(int[][] cave, int[] tunnelCoordinates) {
        for (int i = tunnelCoordinates[1] + 1; i < tunnelCoordinates[2]; i++) {
            cave[tunnelCoordinates[0]][i] = 3;
            changes = true;
        }
        return cave;
    }

    public int[][] createVerticalTunnel(int[][] cave, int[] tunnelCoordinates) {
        for (int i = tunnelCoordinates[1] + 1; i < tunnelCoordinates[2]; i++) {
            cave[i][tunnelCoordinates[0]] = 3;
            changes = true;
        }
        return cave;
    }

    public int[][] floodFill(int[][] cave, int posX, int posY, int colorEnd) {
        if (cave[posX][posY] == 1 || cave[posX][posY] == colorEnd) {
            return cave;
        }
        cave[posX][posY] = colorEnd;

        cave = floodFill(cave,posX,posY-1, colorEnd); //N
        cave = floodFill(cave,posX+1,posY-1, colorEnd); //NE
        cave = floodFill(cave,posX+1,posY, colorEnd); //E
        cave = floodFill(cave,posX+1,posY+1, colorEnd); //SE
        cave = floodFill(cave,posX,posY+1, colorEnd); //S
        cave = floodFill(cave,posX-1,posY+1, colorEnd); //SW
        cave = floodFill(cave,posX-1,posY, colorEnd); //W
        cave = floodFill(cave,posX-1,posY-1, colorEnd); //NW

        return cave;
    }

    public int[] getColumn(int[][] array, int index) {
        int[] column = new int[array.length];
        for(int i=0; i<column.length; i++){
            column[i] = array[i][index];
        }
        return column;
    }
}