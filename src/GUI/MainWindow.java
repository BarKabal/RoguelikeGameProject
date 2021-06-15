package GUI;

import logic.generation.EntryGenerator;
import map.tiles.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainWindow extends JFrame implements ActionListener, Runnable, KeyListener {

    JPanel menuPanel;
    JPanel gamePanel;
    Color background = new Color(15, 15, 40, 255);
    long seed;
    int type;
    EntryGenerator entryGenerator;
    boolean gameActive = false;
    Tile[][] map;
    int currentX, currentY;
    JButton[][] jGrid;

    @Override
    public void run() {
        setSize(1000, 800);
        setLayout(null);
        addKeyListener(this);
        setVisible(true);
        makeStartScreen();
        menuPanel.setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void makeStartScreen() {
        menuPanel = new JPanel();
        menuPanel.setBackground(background);
        menuPanel.setPreferredSize(new Dimension(1000, 830));
        menuPanel.setBounds(0, 0, 1000, 830);
        menuPanel.setLayout(null);
        makeStartButton();
        makeStartTitleLabel();
        this.add(menuPanel);
    }

    private void makeStartTitleLabel() {
        JLabel jlTitle = new JLabel("Dungeon Crawling Game!", SwingConstants.CENTER);
        jlTitle.setBounds(200, 100, 600, 100);
        jlTitle.setFont(new Font("Arial", Font.PLAIN, 30));
        menuPanel.add(jlTitle);
    }

    private void makeStartButton() {
        JButton jbNewGame = new JButton("New Game");
        jbNewGame.setBounds(300, 300, 400, 100);
        jbNewGame.addActionListener(this);
        jbNewGame.setActionCommand("New Game");
        menuPanel.add(jbNewGame);
        JButton jbContinue = new JButton("Continue (WIP)");
        jbContinue.setBounds(300, 400, 400, 100);
        jbContinue.addActionListener(this);
        jbContinue.setActionCommand("Continue");
        menuPanel.add(jbContinue);
        JButton jbSettings = new JButton("Settings (WIP)");
        jbSettings.setBounds(300, 500, 400, 100);
        jbSettings.addActionListener(this);
        jbSettings.setActionCommand("Settings");
        menuPanel.add(jbSettings);
    }

    private void newGameChoice() {
        JDialog gameType = new JDialog(this, "Choose the game type", true);
        gameType.setLayout(null);
        gameType.setSize(300, 300);
        JLabel jlGameType = new JLabel("Please choose the game type and the seed", SwingConstants.CENTER);
        jlGameType.setBounds(15, 20, 250, 30);
        gameType.add(jlGameType);

        JRadioButton dungeon = new JRadioButton("Dungeon", true);
        JRadioButton cave = new JRadioButton("Cave");
        dungeon.setBounds(85, 60, 100, 30);
        cave.setBounds(85, 100, 100, 30);
        ButtonGroup bg = new ButtonGroup();
        bg.add(dungeon);
        bg.add(cave);
        gameType.add(dungeon);
        gameType.add(cave);

        JTextField jtSeed = new JTextField();
        jtSeed.setBounds(40, 140, 200, 30);
        gameType.add(jtSeed);

        JButton jbGameType = new JButton("Done");
        jbGameType.setBounds(100, 190, 80, 40);
        jbGameType.addActionListener(e -> {
            try {
                seed = Long.parseLong(jtSeed.getText());
            } catch (NumberFormatException nfe) {
                return;
            }
            if (dungeon.isSelected())
                type = 1;
            else
                type = 2;
            gameType.setVisible(false);
            makeGameBoard();
        });
        jbGameType.setActionCommand("Done");
        gameType.add(jbGameType);

        gameType.setVisible(true);
        this.setSize(1015,867);
        this.revalidate();
        this.repaint();
    }

    private void makeGameBoard() {
        gameActive = true;
        entryGenerator = new EntryGenerator(type, seed);
        map = entryGenerator.tileMap();
        GridLayout gridLayout = new GridLayout(15,15);
        gamePanel = new JPanel();
        gamePanel.setBackground(background);
        gamePanel.setPreferredSize(new Dimension(1000, 830));
        gamePanel.setBounds(0, 0, 1000, 830);
        gamePanel.setLayout(gridLayout);
        addGameButtons();
        this.remove(menuPanel);
        this.add(gamePanel);
        gamePanel.setVisible(true);
    }

    private void addGameButtons() {
        jGrid = new JButton[15][15]; //-7|0|7
        int[] entry = entryGenerator.getEntry();
        currentX = entry[0];
        currentY = entry[1];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                jGrid[i][j] = new JButton();
                jGrid[i][j].setBounds(47*(i+1),47*(j+1),47,47);
                jGrid[i][j].setBackground(background);
                jGrid[i][j].setForeground(Color.WHITE);
                jGrid[i][j].setEnabled(false);
                gamePanel.add(jGrid[i][j]);
            }
        }
        repositionBoard();
    }

    private void repositionBoard() {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (currentX-i+7 < 0 || currentY-j+7 < 0 || currentX-i+7 >= map[0].length || currentY-j+7 >= map.length) {
                    jGrid[i][j].setText("#");
                    jGrid[i][j].setBackground(background);
                } else if (i == 7 && j == 7) {
                    jGrid[i][j].setText("@");
                    jGrid[i][j].setBackground(new Color(0, 89, 128));
                } else {
                    jGrid[i][j].setText(map[currentX-i+7][currentY-j+7].getAsci());
                    if (!map[currentX-i+7][currentY-j+7].isWall())
                        jGrid[i][j].setBackground(new Color(0, 58, 120));
                    else
                        jGrid[i][j].setBackground(background);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (!gameActive) {
            switch (command) {
                case "New Game" -> newGameChoice();
                case "Continue" -> System.out.print("WIP");
                case "Settings" -> System.out.print("WIP2");
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD && gameActive) {
            switch (e.getKeyChar()) {
                case '1':
                    if (!map[currentX - 1][currentY + 1].isWall()) {
                        currentX--;
                        currentY++;
                        repositionBoard();
                        System.out.println("moved bottom left");
                    }
                    break;
                case '4':
                    if (!map[currentX][currentY + 1].isWall()) {
                        currentY++;
                        repositionBoard();
                        System.out.println("moved left");
                    }
                    break;
                case '7':
                    if (!map[currentX + 1][currentY + 1].isWall()) {
                        currentX++;
                        currentY++;
                        repositionBoard();
                        System.out.println("moved top left");
                    }
                    break;
                case '2':
                    if (!map[currentX - 1][currentY].isWall()) {
                        currentX--;
                        repositionBoard();
                        System.out.println("moved down");
                    }
                    break;
                case '8':
                    if (!map[currentX + 1][currentY].isWall()) {
                        currentX++;
                        repositionBoard();
                        System.out.println("moved up");
                    }
                    break;
                case '3':
                    if (!map[currentX - 1][currentY - 1].isWall()) {
                        currentX--;
                        currentY--;
                        repositionBoard();
                        System.out.println("moved bottom right");
                    }
                    break;
                case '6':
                    if (!map[currentX][currentY - 1].isWall()) {
                        currentY--;
                        repositionBoard();
                        System.out.println("moved right");
                    }
                    break;
                case '9':
                    if (!map[currentX + 1][currentY - 1].isWall()) {
                        currentX++;
                        currentY--;
                        repositionBoard();
                        System.out.println("moved top right");
                    }
                    break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
