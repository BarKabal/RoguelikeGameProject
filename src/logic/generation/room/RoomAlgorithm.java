package logic.generation.room;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class RoomAlgorithm {
    public double minHeight = 10;
    public double maxHeight = 50;
    public double minWidth = 10;
    public double maxWidth = 50;
    public double clusterCircleRadius = 50;
    public ArrayList<Room> rooms;
    public ArrayList<Room> mainRooms;
    public ArrayList<Point2D.Double> mainRoomCenters;
    public ArrayList<Integer[]> graphConnections; //contains connections of rooms in graph [id1,id2]
    public int roomNumber = 150;
    public Random rng;

    public RoomAlgorithm(long seed) {
        rng = new Random(seed);
        rooms = new ArrayList<>();
        mainRooms = new ArrayList<>();
        mainRoomCenters = new ArrayList<>();
        graphConnections = new ArrayList<>();
    }

    public void generateRoom() {
        generateRoomCluster();
        spitRooms();
        moveRooms();
        decideMainRooms();
        minSpanTree();
        System.out.println(mainRooms.size());
    }

    public void generateRoomCluster() {
        for (int i = 0; i < roomNumber; i++) {
            double[] tmp = generatePointInCircle();
            rooms.add(constructRoomAroundPoint(tmp[0], tmp[1]));
        }
    }

    public double[] generatePointInCircle() {
        double t = 2 * Math.PI * rng.nextDouble();
        double u = rng.nextDouble() + rng.nextDouble();
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

    public Room constructRoomAroundPoint(double x, double y) {
        double width, height;
        width = minWidth + (maxWidth - minWidth) * rng.nextDouble();
        height = minHeight + (maxHeight - minHeight) * rng.nextDouble();
        return new Room(height, width, x + width / 2, y + height / 2);
    }

    public void spitRooms() {
        int changes = 1;
        while (changes > 0) {
            changes = 0;
            for (int i = 0; i < roomNumber; i++) {
                for (int j = 0; j < roomNumber; j++) {
                    if (j != i) {
                        if (rooms.get(i).checkIfRoomOverlaps(rooms.get(j))) {
                            if (generateVector(rooms.get(i), rooms.get(j))) {
                                changes++;
                                rooms.get(i).moveByVector();
                                rooms.get(j).moveByVector();
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean generateVector(Room r1, Room r2) {
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

    public void moveRooms() {
        double tmpX = 0;
        double tmpY = 0;
        for (int i = 0; i < roomNumber; i++) {
            if (rooms.get(i).getxPos() > tmpX)
                tmpX = rooms.get(i).getxPos();
            if (rooms.get(i).getyPos() > tmpY)
                tmpY = rooms.get(i).getyPos();
        }
        tmpX = Math.abs(tmpX);
        tmpY = Math.abs(tmpY);
        for (int i = 0; i < roomNumber; i++) {
            rooms.get(i).changeXPos(rooms.get(i).getxPos() + tmpX);
            rooms.get(i).changeYPos(rooms.get(i).getyPos() + tmpY);
        }
    }

    public void decideMainRooms() {
        double meanHeight = 0;
        double meanWidth = 0;
        for (int i = 0; i < roomNumber; i++) {
            meanHeight += rooms.get(i).getHeight();
            meanWidth += rooms.get(i).getWidth();

        }
        meanHeight = meanHeight/roomNumber;
        meanWidth = meanWidth/roomNumber;
        for (int i = 0; i < roomNumber; i++) {
            if (rooms.get(i).getWidth() >= meanWidth * 1.25 && rooms.get(i).getHeight() >= meanHeight * 1.25) {
                mainRooms.add(rooms.get(i));
            }
        }
    }

    public void extractCentres() {
        for (int i = 0; i < mainRooms.size(); i++) {
            mainRoomCenters.add(new Point2D.Double(mainRooms.get(i).getxPos(),mainRooms.get(i).getyPos()));
        }
    }

    public void minSpanTree() {
        extractCentres();
        double[][] distanceMatrix = new double[mainRoomCenters.size()][mainRoomCenters.size()];
        int[] ids = new int[mainRoomCenters.size()];
        for (int i = 0 ; i < mainRoomCenters.size(); i++) {
            ids[i] = mainRooms.get(i).getId();
            for (int j = 0; j < mainRoomCenters.size(); j++) {
                if (i != j) {
                    distanceMatrix[i][j] = distanceFromPoints(mainRoomCenters.get(i).getX(), mainRoomCenters.get(j).getX(), mainRoomCenters.get(i).getY(), mainRoomCenters.get(j).getY());
                } else {
                    distanceMatrix[i][j] = 0;
                }
            }
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

    public boolean checkIfRoomIsInList(int id, ArrayList<Room> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId() == id) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public ArrayList<Room> getMainRooms() {
        return mainRooms;
    }

    public ArrayList<Integer[]> getGraphConnections() {
        return graphConnections;
    }

    public double distanceFromPoints(double ax, double bx, double ay, double by) {
        double xd = Math.abs(ax-bx);
        double yd = Math.abs(ay-by);
        return Math.sqrt(xd*xd+yd*yd);
    }

}
