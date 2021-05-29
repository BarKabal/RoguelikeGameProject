package logic.generation.room;

import java.util.ArrayList;
import java.util.Random;

public class RoomAlgorithm {
    public double minHeight = 20;
    public double maxHeight = 100;
    public double minWidth = 20;
    public double maxWidth = 100;
    public double clusterCircleRadius = 50;
    public ArrayList<Room> rooms;
    public ArrayList<Vector> vectors;
    public int roomNumber = 200;
    public Random rng;
    public long seed;

    public RoomAlgorithm(long seed) {
        rng = new Random(seed);
        rooms = new ArrayList<>();
        vectors = new ArrayList<>();
    }

    public void generateRoom() {
        generateRoomCluster();
        spitRooms();
    }

    public void generateRoomCluster() {
        for (int i = 0; i < roomNumber; i++) {
            double[] tmp = generatePointInCircle();
            rooms.add(constructRoomAroundPoint(tmp[0], tmp[1]));
        }
    }

    public double[] generatePointInCircle() {          //x,y
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
                for (int j = i + 1; j < roomNumber; j++) {
                    if (rooms.get(i).checkIfRoomOverlaps(rooms.get(j))) {
                        generateVector(rooms.get(i), rooms.get(j));
                        changes++;
                    }
                }
            }
            for (int i = 0; i < roomNumber; i++) {
                rooms.get(i).moveByVector();
            }
            System.out.print(changes + "\n");
        }
    }

    public void generateVector(Room r1, Room r2) {
        double x1 = r1.getxPos();
        double y1 = r1.getyPos();
        double x2 = r2.getxPos();
        double y2 = r2.getyPos();
        double h1 = r1.getHeight();
        double w1 = r1.getWidth();
        double h2 = r2.getHeight();
        double w2 = r2.getWidth();
        double z = Math.sqrt(Math.abs(x1-x2)*(Math.abs(x1-x2))+(Math.abs(y1-y2)*Math.abs(y1-y2)));
        boolean verR1Cut, verR2Cut, higherR1, rightR1;
        double sinZ = Math.abs(x1 - x2) / z;
        double cosZ = Math.abs(y1 - y2) / z;
        double tanZ = Math.abs(x1 - x2) / Math.abs(y1 - y2);
        double PQ1, PQ2, QQ, tmp1, tmp2, vec, vecX, vecY;

        verR1Cut = h1 / w1 < tanZ;
        verR2Cut = h2 / w2 < tanZ;
        higherR1 = y1 > y2;
        rightR1 = x1 > x2;

        if (verR1Cut) {
            tmp1 = w1 / (2 * tanZ);
            PQ1 = Math.sqrt((w1 / 2) * (w1 / 2) + tmp1 * tmp1);
        } else {
            tmp1 = (h1 / 2) * tanZ;
            PQ1 = Math.sqrt((h1 / 2) * (h1 / 2) + tmp1 * tmp1);
        }
        if (verR2Cut) {
            tmp2 = w2 / (2 * tanZ);
            PQ2 = Math.sqrt((w2 / 2) * (w2 / 2) + tmp2 * tmp2);
        } else {
            tmp2 = h2 / (2 * tanZ);
            PQ2 = Math.sqrt((h2 / 2) * (h2 / 2) + tmp2 * tmp2);
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
        vecX = vec*sinZ;
        vecY = vec*cosZ;

        if (higherR1) {
            if (rightR1) {
                r1.addVector(vecX/2,vecY/2);
                r2.addVector(-vecX/2,-vecY/2);
            } else {
                r1.addVector(-vecX/2,vecY/2);
                r2.addVector(vecX/2,-vecY/2);
            }
        } else {
            if  (rightR1){
                r1.addVector(vecX/2,-vecY/2);
                r2.addVector(-vecX/2,vecY/2);
            } else {
                r1.addVector(-vecX/2,-vecY/2);
                r2.addVector(vecX/2,vecY/2);
            }
        }
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

}
