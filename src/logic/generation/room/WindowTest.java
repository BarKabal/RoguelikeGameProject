package logic.generation.room;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class WindowTest extends Canvas {

    ArrayList<Room> rooms;

    public WindowTest(RoomAlgorithm alg) {
        rooms = alg.getRooms();
    }

    public void paint(Graphics g) {
        setForeground(Color.RED);
        for (int i = 0; i < rooms.size(); i++) {
            g.drawRect((int) (rooms.get(i).xPos - rooms.get(i).width / 2), (int) (rooms.get(i).yPos - rooms.get(i).height / 2), (int) (rooms.get(i).width), (int) (rooms.get(i).height));
        }
    }

    public static void main(String[] args) {
        RoomAlgorithm alg = new RoomAlgorithm(0);
        WindowTest ts = new WindowTest(alg);
        alg.generateRoom();
        JFrame f = new JFrame();
        f.setSize(1000, 1000);
        f.add(ts);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
