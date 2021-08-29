package logic.generation.room;

import logic.gameData.GameData;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class RoomAlgorithm {
    public double minHeight = 4;
    public double maxHeight = 10;
    public double minWidth = 4;
    public double maxWidth = 10;
    public double clusterCircleRadius = 20;
    public ArrayList<Room> rooms;
    public ArrayList<Room> mainRooms;
    public ArrayList<Point2D.Double> mainRoomCenters;
    public ArrayList<Integer[]> graphConnections;
    public ArrayList<Room> finalRooms;
    public double extraConnectChance = 0.005;
    public int roomNumber = 250; //1000 max inaczej bardzo długa kompilacja
    public GameData gameData;
    public boolean changes = true;
    public int xSize;
    public int ySize;
    int[][] map;

    public RoomAlgorithm(GameData gameData) {
        Room.resetNumber();
        this.gameData = gameData;
        rooms = new ArrayList<>();
        mainRooms = new ArrayList<>();
        mainRoomCenters = new ArrayList<>();
        graphConnections = new ArrayList<>();
        finalRooms = new ArrayList<>();
    }

    public void generateRoom() {
        generateRoomCluster();
        spitRooms();
        decideMainRooms();
        minSpanTree();
        addExtraConnects();
        reduceNotUsedRooms();
        moveRooms();
        convertToMap();
    }

    private void generateRoomCluster() {
        for (int i = 0; i < roomNumber; i++) {
            double[] tmp = generatePointInCircle();
            rooms.add(constructRoomAroundPoint(tmp[0], tmp[1]));
        }
    }

    private double[] generatePointInCircle() {
        double t = 2 * Math.PI * gameData.getRng().nextDouble();
        double u = gameData.getRng().nextDouble() + gameData.getRng().nextDouble();
        double r;
        if (u > 1) {
            r = 2 - u;
        } else {
            r = u;
        }
        double[] coordinates = new double[2];
        coordinates[0] = clusterCircleRadius * r * Math.cos(t);
        coordinates[1] = clusterCircleRadius * r * Math.sin(t);

        return coordinates;
    }

    private Room constructRoomAroundPoint(double x, double y) {
        double width, height;
        width = minWidth + (maxWidth - minWidth) * gameData.getRng().nextDouble();
        height = minHeight + (maxHeight - minHeight) * gameData.getRng().nextDouble();
        return new Room(height, width, x + width / 2, y + height / 2);
    }

    private void spitRooms() {
        int slitChanges = 1;
        while (slitChanges > 0) {
            slitChanges = 0;
            for (int i = 0; i < roomNumber; i++) {
                for (int j = 0; j < roomNumber; j++) {
                    if (j != i) {
                        if (rooms.get(i).checkIfRoomOverlaps(rooms.get(j))) {
                            if (generateVector(rooms.get(i), rooms.get(j))) {
                                slitChanges++;
                                rooms.get(i).moveByVector();
                                rooms.get(j).moveByVector();
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean generateVector(Room r1, Room r2) {
        int divider = 2;
        double x1 = r1.getxPos();
        double y1 = r1.getyPos();
        double x2 = r2.getxPos();
        double y2 = r2.getyPos();
        double h1 = r1.getHeight();
        double w1 = r1.getWidth();
        double h2 = r2.getHeight();
        double w2 = r2.getWidth();
        double z = Math.sqrt((Math.abs(x1 - x2) * Math.abs(x1 - x2)) + (Math.abs(y1 - y2) * Math.abs(y1 - y2)));
        boolean verR1Cut, verR2Cut, higherR1, rightR1;
        double sinZ = Math.abs(x1 - x2) / z;
        double cosZ = Math.abs(y1 - y2) / z;
        double tanZ = Math.abs(x1 - x2) / Math.abs(y1 - y2);
        double PQ1, PQ2, QQ, tmp1, tmp2, vec, vecX, vecY;

        verR1Cut = w1 / h1 < tanZ;
        verR2Cut = w2 / h2 < tanZ;
        higherR1 = y1 > y2;
        rightR1 = x1 > x2;

        if (verR1Cut) {
            tmp1 = w1 / (2 * tanZ);
            PQ1 = Math.sqrt(((w1 / 2) * (w1 / 2)) + (tmp1 * tmp1));
        } else {
            tmp1 = (h1 / 2) * tanZ;
            PQ1 = Math.sqrt(((h1 / 2) * (h1 / 2)) + (tmp1 * tmp1));
        }

        if (verR2Cut) {
            tmp2 = w2 / (2 * tanZ);
            PQ2 = Math.sqrt(((w2 / 2) * (w2 / 2)) + (tmp2 * tmp2));
        } else {
            tmp2 = (h2 / 2) * tanZ;
            PQ2 = Math.sqrt(((h2 / 2) * (h2 / 2)) + (tmp2 * tmp2));
        }

        if (verR1Cut != verR2Cut) {
            if (verR1Cut)
                QQ = Math.sqrt(((h1 / 2) - tmp1) * ((h1 / 2) - tmp1) + ((w2 / 2) - tmp2) * ((w2 / 2) - tmp2));
            else
                QQ = Math.sqrt(((w1 / 2) - tmp1) * ((w1 / 2) - tmp1) + ((h2 / 2) - tmp2) * ((h2 / 2) - tmp2));
        } else {
            QQ = 0;
        }
        vec = PQ1 + PQ2 + QQ - z;

        if (vec < 0.01) {
            return false;
        }

        vecX = (vec * sinZ) / divider;
        vecY = (vec * cosZ) / divider;

        if (higherR1) {
            if (rightR1) {
                r1.addVector(vecX, vecY);
                r2.addVector(-vecX, -vecY);
            } else {
                r1.addVector(-vecX, vecY);
                r2.addVector(vecX, -vecY);
            }
        } else {
            if (rightR1) {
                r1.addVector(vecX, -vecY);
                r2.addVector(-vecX, vecY);
            } else {
                r1.addVector(-vecX, -vecY);
                r2.addVector(vecX, vecY);
            }
        }
        return true;
    }

    private void moveRooms() {
        double tmpX = 10000;
        double tmpY = 10000;
        for (int i = 0; i < finalRooms.size(); i++) {
            if (finalRooms.get(i).getxPos() - finalRooms.get(i).getWidth() / 2 < tmpX)
                tmpX = finalRooms.get(i).getxPos() - finalRooms.get(i).getWidth() / 2;
            if (finalRooms.get(i).getyPos() - finalRooms.get(i).getHeight() / 2 < tmpY)
                tmpY = finalRooms.get(i).getyPos() - finalRooms.get(i).getHeight() / 2;
        }
        for (int i = 0; i < finalRooms.size(); i++) {
            finalRooms.get(i).changeXPos(finalRooms.get(i).getxPos() - tmpX + 1);
            finalRooms.get(i).changeYPos(finalRooms.get(i).getyPos() - tmpY + 1);
        }
    }

    private void decideMainRooms() {
        double meanHeight = 0;
        double meanWidth = 0;
        for (int i = 0; i < roomNumber; i++) {
            meanHeight += rooms.get(i).getHeight();
            meanWidth += rooms.get(i).getWidth();

        }
        meanHeight = meanHeight / roomNumber;
        meanWidth = meanWidth / roomNumber;
        for (int i = 0; i < roomNumber; i++) {
            if (rooms.get(i).getWidth() >= meanWidth * 1.25 && rooms.get(i).getHeight() >= meanHeight * 1.25) {
                mainRooms.add(rooms.get(i));
            }
        }
    }

    private void extractCentres() {
        for (int i = 0; i < mainRooms.size(); i++) {
            mainRoomCenters.add(new Point2D.Double(mainRooms.get(i).getxPos(), mainRooms.get(i).getyPos()));
        }
    }

    private void minSpanTree() {
        extractCentres();
        int[] ids = new int[mainRoomCenters.size()];
        for (int i = 0; i < mainRoomCenters.size(); i++) {
            ids[i] = mainRooms.get(i).getId();
        }
        ArrayList<Room> roomsInTree = new ArrayList<>();
        ArrayList<Room> roomsNotInTree = new ArrayList<>(mainRooms);
        roomsInTree.add(roomsNotInTree.remove(0));
        while (roomsNotInTree.size() != 0 && roomsInTree.size() != mainRooms.size()) {
            double minDist = 100000;
            Room roomIn = null;
            Room roomOut = null;
            for (int i = 0; i < ids.length; i++) {
                if (checkIfRoomIsInList(ids[i], roomsInTree)) {
                    for (int j = 0; j < ids.length; j++) {
                        if (checkIfRoomIsInList(ids[j], roomsNotInTree)) {
                            if (distanceFromPoints(mainRoomCenters.get(i).getX(), mainRoomCenters.get(j).getX(), mainRoomCenters.get(i).getY(), mainRoomCenters.get(j).getY()) < minDist) {
                                roomIn = mainRooms.get(i);
                                roomOut = mainRooms.get(j);
                                minDist = distanceFromPoints(mainRoomCenters.get(i).getX(), mainRoomCenters.get(j).getX(), mainRoomCenters.get(i).getY(), mainRoomCenters.get(j).getY());
                            }
                        }
                    }
                }
            }
            Integer[] connection = new Integer[2];
            connection[0] = roomIn.getId();
            connection[1] = roomOut.getId();
            roomsInTree.add(roomOut);
            roomsNotInTree.remove(roomOut);
            graphConnections.add(connection);
        }
    }

    private boolean checkIfRoomIsInList(int id, ArrayList<Room> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                return true;
            }
        }
        return false;
    }

    private double distanceFromPoints(double ax, double bx, double ay, double by) {
        double xd = Math.abs(ax - bx);
        double yd = Math.abs(ay - by);
        return Math.sqrt(xd * xd + yd * yd);
    }

    private void addExtraConnects() {
        for (int i = 0; i < mainRooms.size(); i++) {
            for (int j = 0; j < mainRooms.size(); j++) {
                for (int k = 0; k < graphConnections.size(); k++) {
                    if (graphConnections.get(k)[0] != i || graphConnections.get(k)[1] != j && graphConnections.get(k)[0] != j || graphConnections.get(k)[1] != i) {
                        if (gameData.getRng().nextDouble() < extraConnectChance) {
                            Integer[] connection = new Integer[2];
                            connection[0] = mainRooms.get(i).getId();
                            connection[1] = mainRooms.get(j).getId();
                            graphConnections.add(connection);
                        }
                    }
                }
            }
        }
    }

    private void reduceNotUsedRooms() {
        finalRooms.addAll(mainRooms);
        for (int i = 0; i < graphConnections.size(); i++) {
            for (int j = 0; j < rooms.size(); j++) {
                if (!finalRooms.contains(rooms.get(j)) && rooms.get(j).checkIfLineOverlaps(rooms.get(graphConnections.get(i)[0]).getxPos(), rooms.get(graphConnections.get(i)[0]).getyPos(), rooms.get(graphConnections.get(i)[1]).getxPos(), rooms.get(graphConnections.get(i)[1]).getyPos())) {
                    finalRooms.add(rooms.get(j));
                }
            }
        }
    }

    private void convertToMap() {
        double[] maxXY = findMaximalXAndY();
        map = new int[(int) Math.round(maxXY[1]) + 1][(int) Math.round(maxXY[0]) + 1];
        xSize = map.length;
        ySize = map[0].length;
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                map[i][j] = 1;
            }
        }
        for (int i = 0; i < finalRooms.size(); i++) {
            addRoomToMap(map, finalRooms.get(i));
        }
        createDoorsAndHallways(map);
    }

    private void addRoomToMap(int[][] map, Room room) {
        int up, down, right, left;
        up = (int) Math.round(room.getyPos() - room.getHeight() / 2);
        down = (int) Math.round(room.getyPos() + room.getHeight() / 2);
        right = (int) Math.round(room.getxPos() + room.getWidth() / 2);
        left = (int) Math.round(room.getxPos() - room.getWidth() / 2);
        for (int i = up + 1; i < down; i++) {
            for (int j = left + 1; j < right; j++) {
                map[i][j] = 0;
            }
        }
    }

    private void printMapToTerminal(int[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 1) {
                    System.out.print("#");
                } else /*if (map[i][j] == 0)*/ {
                    System.out.print(".");
                }
            }
            System.out.print("\n");
        }
    }

    private double[] findMaximalXAndY() {
        double[] result = new double[]{0, 0};
        for (int i = 0; i < finalRooms.size(); i++) {
            if (finalRooms.get(i).getxPos() + finalRooms.get(i).getWidth() / 2 > result[0]) {
                result[0] = finalRooms.get(i).getxPos() + finalRooms.get(i).getWidth() / 2;
            }
            if (finalRooms.get(i).getyPos() + finalRooms.get(i).getHeight() / 2 > result[1]) {
                result[1] = finalRooms.get(i).getyPos() + finalRooms.get(i).getHeight() / 2;
            }
        }
        return result;
    }

    private void createDoorsAndHallways(int[][] map) {
        for (int i = 0; i < finalRooms.size(); i++) {
            carveTunnel(scanRight(finalRooms.get(i), map), map);
            carveTunnel(scanDown(finalRooms.get(i), map), map);
        }
        int colorEnd = 10;
        while (true) {
            int[] res = findRoom(map);
            if (res == null) {
                break;
            }
            floodFill(map, res[0], res[1], colorEnd);
            colorEnd++;
        }
        ArrayList<Integer> counter = new ArrayList<>();

        int maxNum = 0, maxInd = 0;
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                if (map[i][j] >= 10) {
                    if (counter.size() <= map[i][j]-10)
                        counter.add(map[i][j] - 10, 1);
                    else
                        counter.add(map[i][j] - 10, counter.remove(map[i][j] - 10)+1);
                }
            }
        }
        for (int i = 0; i < counter.size(); i++) {
            if (counter.get(i) > maxNum) {
                maxInd = i;
                maxNum = counter.get(i);
            }
        }
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                if (map[i][j] != maxInd+10 && map[i][j] != 1) {
                    map[i][j] = 1;
                }
            }
        }
        printMapToTerminal(map);
    }

    private Integer[] scanRight(Room room, int[][] map) {
        int up = (int) Math.round(room.getyPos() - room.getHeight() / 2);
        int down = (int) Math.round(room.getyPos() + room.getHeight() / 2);
        int right = (int) Math.round(room.getxPos() + room.getWidth() / 2);
        int minLen = 10000;
        boolean zeroFound = false;
        ArrayList<Integer[]> rows = new ArrayList<>(); //x,y,długość,orientacja (0 - horizontal, 1 - vertical)
        for (int i = up + 1; i < down; i++) {
            int currLen = 0;
            for (int j = right; j < map[0].length; j++) {
                if (map[i][j] == 0) {
                    if (j - right + 1 < minLen) {
                        minLen = j - right + 1;
                        rows.clear();
                    }
                    zeroFound = true;
                }
                currLen++;
                if (zeroFound) {
                    break;
                }
            }
            zeroFound = false;
            if (currLen == minLen) {
                rows.add(new Integer[]{i, right, minLen, 0});
            }
        }
        if (rows.size() == 0 || minLen > 15) {
            return null;
        } else {
            return rows.get(gameData.getRng().nextInt(rows.size()));
        }
    }

    private Integer[] scanDown(Room room, int[][] map) {
        int down = (int) Math.round(room.getyPos() + room.getHeight() / 2);
        int right = (int) Math.round(room.getxPos() + room.getWidth() / 2);
        int left = (int) Math.round(room.getxPos() - room.getWidth() / 2);
        int minLen = 10000;
        boolean zeroFound = false;
        ArrayList<Integer[]> cols = new ArrayList<>(); //x,y,długość,orientacja (0 - horizontal, 1 - vertical)
        for (int i = left + 1; i < right; i++) {
            int currLen = 0;
            for (int j = down; j < map.length; j++) {
                if (map[j][i] == 0) {
                    if (j - down + 1 < minLen) {
                        minLen = j - down + 1;
                        cols.clear();
                    }
                    zeroFound = true;
                }
                currLen++;
                if (zeroFound) {
                    break;
                }
            }
            zeroFound = false;
            if (currLen == minLen) {
                cols.add(new Integer[]{down, i, minLen, 1});
            }
        }
        if (cols.size() == 0 || minLen > 12) {
            return null;
        } else {
            return cols.get(gameData.getRng().nextInt(cols.size()));
        }
    }

    private void carveTunnel(Integer[] tunnelData, int[][] map) {
        if (tunnelData == null) {
            return;
        }
        if (tunnelData[3] == 0) {
            for (int i = 0; i < tunnelData[2] - 1; i++) {
                map[tunnelData[0]][tunnelData[1] + i] = 0;
            }
        } else {
            for (int i = 0; i < tunnelData[2] - 1; i++) {
                map[tunnelData[0] + i][tunnelData[1]] = 0;
            }
        }
    }

    private int[] findRoom(int[][] map) { //returns upper left corner
        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                if (map[i][j] < 10 && map[i][j] != 1 && map[i][j] != 4) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private int[][] floodFill(int[][] cave, int posX, int posY, int colorEnd) {
        if (cave[posX][posY] == 1 || cave[posX][posY] == colorEnd || cave[posX][posY] == 4) {
            return cave;
        }
        if (cave[posX][posY] != 3) {
            cave[posX][posY] = colorEnd;
        } else {
            cave[posX][posY] = 4;
        }

        cave = floodFill(cave, posX, posY - 1, colorEnd); //N
        cave = floodFill(cave, posX + 1, posY - 1, colorEnd); //NE
        cave = floodFill(cave, posX + 1, posY, colorEnd); //E
        cave = floodFill(cave, posX + 1, posY + 1, colorEnd); //SE
        cave = floodFill(cave, posX, posY + 1, colorEnd); //S
        cave = floodFill(cave, posX - 1, posY + 1, colorEnd); //SW
        cave = floodFill(cave, posX - 1, posY, colorEnd); //W
        cave = floodFill(cave, posX - 1, posY - 1, colorEnd); //NW

        return cave;
    }

    public int[] returnSize() {
        return new int[]{map.length, map[0].length};
    }

    public int[][] returnMap() {
        return map;
    }
}
