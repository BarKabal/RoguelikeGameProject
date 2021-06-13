package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow extends JFrame implements ActionListener {
    private Color background = new Color(15,15,40, 255);


    public MainWindow() {
        getContentPane().setBackground(background);
        setSize(1000,800);
        makeStartScreen();
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void makeStartScreen() {
        makeStartButton();
        makeTitleLabel();
    }

    private void makeTitleLabel() {
        JLabel jlTitle = new JLabel("Dungeon Crawling Game!", SwingConstants.CENTER);
        jlTitle.setBounds(200,100,600,100);
        jlTitle.setFont(new Font("Arial", Font.PLAIN,30));
        this.add(jlTitle);
    }

    private void makeStartButton() {
        JButton jbNewGame = new JButton("New Game");
        jbNewGame.setBounds(300,300,400,100);
        this.add(jbNewGame);
        JButton jbContinue = new JButton("Continue");
        jbContinue.setBounds(300,400,400,100);
        this.add(jbContinue);
        JButton jbSettings = new JButton("Settings");
        jbSettings.setBounds(300,500,400,100);
        this.add(jbSettings);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public static void main(String[] args) {
        MainWindow window = new MainWindow();
    }
}
