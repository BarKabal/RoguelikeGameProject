package GUI;

import logic.gameData.DungeonFloor;
import logic.gameData.FOVCalculator;
import logic.gameData.GameData;
import map.items.item;
import map.npc.Monster;
import map.tiles.Tile;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class MainWindow extends JFrame implements ActionListener, Runnable, KeyListener, MouseListener {

    private JPanel menuPanel, gamePanel, equipmentPanel, gameOver;
    private JLabel gameOverLabel;
    private final Color backgroundColor = new Color(15, 15, 40, 255);
    private long seed;
    private int type;
    private boolean gameActive = false;
    private JLabel[][] jGrid;
    private GameData gameData;
    private JLabel[][] inventoryGrid;
    private JLabel helmet, armor, mainHand, offHand, boots;
    private FOVCalculator fovCalculator;
    private JLabel HP, STR, DEX, INT, CON, WIS, LUC, LVL, XP, Time;


    @Override
    public void run() {
        setSize(1000, 800);
        setLayout(null);
        addKeyListener(this);
        makeStartScreen();
        menuPanel.setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void makeStartScreen() {
        menuPanel = new JPanel();
        menuPanel.setBackground(backgroundColor);
        menuPanel.setPreferredSize(new Dimension(1008, 830));
        menuPanel.setBounds(0, 0, 1008, 830);
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

        JRadioButton mixed = new JRadioButton("Mixed", true);
        JRadioButton dungeon = new JRadioButton("Dungeon");
        JRadioButton cave = new JRadioButton("Cave");
        mixed.setBounds(85, 60, 100, 20);
        dungeon.setBounds(85, 80, 100, 20);
        cave.setBounds(85, 100, 100, 20);
        ButtonGroup bg = new ButtonGroup();
        bg.add(mixed);
        bg.add(dungeon);
        bg.add(cave);
        gameType.add(mixed);
        gameType.add(dungeon);
        gameType.add(cave);

        JTextField jtSeed = new JTextField();
        jtSeed.setBounds(40, 140, 200, 30);
        gameType.add(jtSeed);

        JButton jbGameType = new JButton("Done");
        jbGameType.setBounds(100, 190, 80, 40);
        Random rng = new Random();
        jbGameType.addActionListener(e -> {
            try {
                if (Objects.equals(jtSeed.getText(), "")) {
                    seed = rng.nextLong();
                }
                seed = Long.parseLong(jtSeed.getText());
            } catch (NumberFormatException nfe) {
                seed = rng.nextLong();
            }
            if (dungeon.isSelected())
                type = 1;
            else if (cave.isSelected())
                type = 2;
            else if (mixed.isSelected())
                type = 0;
            gameType.setVisible(false);
            makeGameBoard();
        });
        jbGameType.setActionCommand("Done");
        gameType.add(jbGameType);

        gameType.setVisible(true);
        this.setSize(1475, 765);
        this.revalidate();
        this.repaint();
    }

    private void makeGameBoard() {
        gameActive = true;
        gameData = new GameData(seed);
        gameData.setGameType(type);
        gameData.generateMap();
        fovCalculator = new FOVCalculator(gameData);

        GridLayout gridLayout = new GridLayout(17, 21);
        gamePanel = new JPanel();
        gamePanel.setBackground(backgroundColor);
        gamePanel.setPreferredSize(new Dimension(1008, 765));
        gamePanel.setBounds(0, 0, 1008, 765);
        gamePanel.setLayout(gridLayout);

        addGameLabels();

        equipmentPanel = new JPanel();
        equipmentPanel.setBackground(backgroundColor);
        equipmentPanel.setPreferredSize(new Dimension(460, 765));
        equipmentPanel.setBounds(1008, 0, 460, 765);
        equipmentPanel.setLayout(null);

        addEquipmentParts();

        this.remove(menuPanel);
        this.add(gamePanel);
        this.add(equipmentPanel);
        gamePanel.setVisible(true);
        equipmentPanel.setVisible(true);
    }

    private void gameOver() {
        gameActive = false;
        gameOver = new JPanel();
        gameOver.setBackground(Color.BLACK);
        gameOver.setPreferredSize(new Dimension(1475, 765));
        gameOver.setBounds(0, 0, 1475, 765);
        gameOver.setLayout(null);

        gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setBounds(600,300,275,165);
        gameOverLabel.setBackground(Color.BLACK);
        gameOverLabel.setForeground(Color.WHITE);
        gameOverLabel.setEnabled(false);
        gameOver.add(gameOverLabel);
        gameOver.setVisible(true);

        this.remove(gamePanel);
        this.remove(equipmentPanel);
        this.add(gameOver);
        gameOver.setVisible(true);
        repaint();
    }

    private void addGameLabels() {
        jGrid = new JLabel[17][21];
        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 21; j++) {
                jGrid[i][j] = new JLabel();
                int tileWidth = 48;
                int tileHeight = 48;
                jGrid[i][j].setBounds(tileWidth * (i + 1), tileHeight * (j + 1), tileWidth, tileHeight);
                jGrid[i][j].setEnabled(false);
                gamePanel.add(jGrid[i][j]);
            }
        }
        repositionBoard();
    }

    private void addEquipmentParts() {
        addInventory();
        addEquipment();
        addStatistics();
    }

    private void addEquipment() {
        helmet = new JLabel();
        armor = new JLabel();
        mainHand = new JLabel();
        offHand = new JLabel();
        boots = new JLabel();
        addComponent(helmet, 65, 5, 48, 48, backgroundColor, Color.WHITE, false, equipmentPanel);
        addComponent(armor, 65, 55, 48, 48, backgroundColor, Color.WHITE, false, equipmentPanel);
        addComponent(mainHand, 15, 55, 48, 48, backgroundColor, Color.WHITE, false, equipmentPanel);
        addComponent(offHand, 115, 55, 48, 48, backgroundColor, Color.WHITE, false, equipmentPanel);
        addComponent(boots, 65, 105, 48, 48, backgroundColor, Color.WHITE, false, equipmentPanel);
        helmet.setBorder(new BevelBorder(BevelBorder.LOWERED));
        armor.setBorder(new BevelBorder(BevelBorder.LOWERED));
        mainHand.setBorder(new BevelBorder(BevelBorder.LOWERED));
        offHand.setBorder(new BevelBorder(BevelBorder.LOWERED));
        boots.setBorder(new BevelBorder(BevelBorder.LOWERED));
    }

    private void addInventory() {
        inventoryGrid = new JLabel[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                inventoryGrid[i][j] = new JLabel();
                addComponent(inventoryGrid[i][j], 55 * i + 8, 195 + (50 * j), 48, 48, backgroundColor, Color.WHITE, false, equipmentPanel);
                inventoryGrid[i][j].setBorder(new BevelBorder(BevelBorder.LOWERED));
                inventoryGrid[i][j].addMouseListener(this);
            }
        }
    }

    private void addStatistics() {
        HP = new JLabel();
        STR = new JLabel();
        DEX = new JLabel();
        INT = new JLabel();
        CON = new JLabel();
        WIS = new JLabel();
        LUC = new JLabel();
        LVL = new JLabel();
        XP = new JLabel();
        Time = new JLabel();
        addComponent(HP, 200, 5, 100, 10, backgroundColor, Color.WHITE, false, equipmentPanel);
        addComponent(STR, 200, 25, 100, 10, backgroundColor, Color.WHITE, false, equipmentPanel);
        addComponent(DEX, 300, 25, 100, 10, backgroundColor, Color.WHITE, false, equipmentPanel);
        addComponent(INT, 200, 45, 100, 10, backgroundColor, Color.WHITE, false, equipmentPanel);
        addComponent(CON, 300, 45, 100, 10, backgroundColor, Color.WHITE, false, equipmentPanel);
        addComponent(WIS, 200, 65, 100, 10, backgroundColor, Color.WHITE, false, equipmentPanel);
        addComponent(LUC, 300, 65, 100, 10, backgroundColor, Color.WHITE, false, equipmentPanel);
        addComponent(LVL, 200, 85, 100, 10, backgroundColor, Color.WHITE, false, equipmentPanel);
        addComponent(XP, 300, 85, 100, 10, backgroundColor, Color.WHITE, false, equipmentPanel);
        addComponent(Time, 200, 105, 100, 10, backgroundColor, Color.WHITE, false, equipmentPanel);
        updateStatistics();
    }

    public void updateStatistics() {
        HP.setText("HP " + gameData.getCharacterData().getHP() + "/" + gameData.getCharacterData().getMaxHP());
        STR.setText("STR: " + (int) gameData.getCharacterData().getSTR());
        DEX.setText("DEX: " + (int) gameData.getCharacterData().getDEX());
        INT.setText("INT: " + (int) gameData.getCharacterData().getINT());
        CON.setText("CON: " + (int) gameData.getCharacterData().getCON());
        WIS.setText("WIS: " + (int) gameData.getCharacterData().getWIS());
        LUC.setText("LUC: " + (int) gameData.getCharacterData().getLUC());
        LVL.setText("LVL " + gameData.getCharacterData().getLvl());
        XP.setText("XP: " + gameData.getCharacterData().getXp() / 10 + "%");
        Time.setText("Time " + gameData.getTime());
    }

    private void addComponent(JComponent component, int x, int y, int width, int height, Color background, Color foreground, boolean setEnabled, JPanel panel) {
        component.setBounds(x, y, width, height);
        component.setBackground(background);
        component.setForeground(foreground);
        component.setEnabled(setEnabled);
        panel.add(component);
        component.setVisible(true);
    }

    private void repositionBoard() {
        gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).resetVisibility();
        fovCalculator.CalculateFOV();
        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 21; j++) {
                jGrid[i][j].setBorder(null);
                if (i == 8 && j == 10) {
                    try {
                        jGrid[i][j].setDisabledIcon(new ImageIcon(ImageIO.read(new File("src/graphics/player/Player.png"))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (gameData.getCharacterData().getPositionX() + i - 8 < 0 ||
                        gameData.getCharacterData().getPositionY() + j - 10 < 0 ||
                        gameData.getCharacterData().getPositionX() + i - 8 >= gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).getDimensions()[0] ||
                        gameData.getCharacterData().getPositionY() + j - 10 >= gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).getDimensions()[1] ||
                        !gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).getTileFromMap(gameData.getCharacterData().getPositionX() + i - 8, gameData.getCharacterData().getPositionY() + j - 10).getSeen()) {
                    try {
                        jGrid[i][j].setDisabledIcon(new ImageIcon(ImageIO.read(new File("src/graphics/tiles/Void.png"))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Monster monster = gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).checkForMonster(gameData.getCharacterData().getPositionX() + i - 8, gameData.getCharacterData().getPositionY() + j - 10);
                    item item = gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).checkForItem(gameData.getCharacterData().getPositionX() + i - 8, gameData.getCharacterData().getPositionY() + j - 10);
                    gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).addMonsterIfNotPresentInQueue(monster);

                    if (monster != null && gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).getTileFromMap(gameData.getCharacterData().getPositionX() + i - 8, gameData.getCharacterData().getPositionY() + j - 10).getVisible()) {
                        jGrid[i][j].setDisabledIcon(new ImageIcon(monster.getPng()));

                    } else if (item != null && gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).getTileFromMap(gameData.getCharacterData().getPositionX() + i - 8, gameData.getCharacterData().getPositionY() + j - 10).getVisible()) {
                        jGrid[i][j].setDisabledIcon(new ImageIcon(item.getPng()));

                    } else if (!gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).getTileFromMap(gameData.getCharacterData().getPositionX() + i - 8, gameData.getCharacterData().getPositionY() + j - 10).getVisible() && gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).getTileFromMap(gameData.getCharacterData().getPositionX() + i - 8, gameData.getCharacterData().getPositionY() + j - 10).getSeen()) {
                        jGrid[i][j].setDisabledIcon(new ImageIcon(gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).getTileFromMap(gameData.getCharacterData().getPositionX() + i - 8, gameData.getCharacterData().getPositionY() + j - 10).getPng()));
                        jGrid[i][j].setBorder(new LineBorder(Color.BLACK, 2));
                    } else {
                        jGrid[i][j].setDisabledIcon(new ImageIcon(gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).getTileFromMap(gameData.getCharacterData().getPositionX() + i - 8, gameData.getCharacterData().getPositionY() + j - 10).getPng()));
                    }
                }
            }
        }
    }

    private void passTime() {
        gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor()).passTimeToNextPLayerRound();
    }

    private void updateInventory() {
        ArrayList<item> inventoryContent = gameData.getCharacterData().getInventoryContent();
        for (int i = 0; i < inventoryContent.size(); i++) {
            item item = inventoryContent.get(i);
            if (item != null)
                inventoryGrid[i % 8][i / 8].setDisabledIcon(new ImageIcon(item.getPng()));
            else
                inventoryGrid[i % 8][i / 8].setDisabledIcon(null);
        }
    }

    private void updateEquipment() {
        item helmetItem = gameData.getCharacterData().getHelmet();
        item armorItem = gameData.getCharacterData().getMainArmor();
        item mainHandItem = gameData.getCharacterData().getEquippedWeapon();
        item offHandItem = gameData.getCharacterData().getShield();
        item bootsItem = gameData.getCharacterData().getBoots();
        if (helmetItem != null)
            helmet.setDisabledIcon(new ImageIcon(helmetItem.getPng()));
        else
            helmet.setDisabledIcon(null);
        if (armorItem != null)
            armor.setDisabledIcon(new ImageIcon(armorItem.getPng()));
        else
            armor.setDisabledIcon(null);
        if (mainHandItem != null)
            mainHand.setDisabledIcon(new ImageIcon(mainHandItem.getPng()));
        else
            mainHand.setDisabledIcon(null);
        if (offHandItem != null)
            offHand.setDisabledIcon(new ImageIcon(offHandItem.getPng()));
        else
            offHand.setDisabledIcon(null);
        if (bootsItem != null)
            boots.setDisabledIcon(new ImageIcon(bootsItem.getPng()));
        else
            boots.setDisabledIcon(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (!gameActive) {
            switch (command) {
                case "New Game": newGameChoice(); break;
                case "Continue":
                case "Settings":
                    break;
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD && gameActive) {
            if (gameData.getCharacterData().getDead()) {
                gameOver();
                return;
            }
            DungeonFloor floor = gameData.getMapData().getDungeonFloor(gameData.getCharacterData().getCurrentFloor());
            Tile tile = floor.getTileFromMap(gameData.getCharacterData().getPositionX(), gameData.getCharacterData().getPositionY());
            switch (e.getKeyChar()) {
                case '9':
                    if (!floor.getTileFromMap(gameData.getCharacterData().getPositionX() - 1, gameData.getCharacterData().getPositionY() + 1).isWall()) {
                        gameData.getCharacterData().moveCharacter(-1, 1, gameData);
                    }
                    break;
                case '6':
                    if (!floor.getTileFromMap(gameData.getCharacterData().getPositionX(), gameData.getCharacterData().getPositionY() + 1).isWall()) {
                        gameData.getCharacterData().moveCharacter(0, 1, gameData);
                    }
                    break;
                case '3':
                    if (!floor.getTileFromMap(gameData.getCharacterData().getPositionX() + 1, gameData.getCharacterData().getPositionY() + 1).isWall()) {
                        gameData.getCharacterData().moveCharacter(1, 1, gameData);
                    }
                    break;
                case '8':
                    if (!floor.getTileFromMap(gameData.getCharacterData().getPositionX() - 1, gameData.getCharacterData().getPositionY()).isWall()) {
                        gameData.getCharacterData().moveCharacter(-1, 0, gameData);
                    }
                    break;
                case '2':
                    if (!floor.getTileFromMap(gameData.getCharacterData().getPositionX() + 1, gameData.getCharacterData().getPositionY()).isWall()) {
                        gameData.getCharacterData().moveCharacter(1, 0, gameData);
                    }
                    break;
                case '7':
                    if (!floor.getTileFromMap(gameData.getCharacterData().getPositionX() - 1, gameData.getCharacterData().getPositionY() - 1).isWall()) {
                        gameData.getCharacterData().moveCharacter(-1, -1, gameData);
                    }
                    break;
                case '4':
                    if (!floor.getTileFromMap(gameData.getCharacterData().getPositionX(), gameData.getCharacterData().getPositionY() - 1).isWall()) {
                        gameData.getCharacterData().moveCharacter(0, -1, gameData);
                    }
                    break;
                case '1':
                    if (!floor.getTileFromMap(gameData.getCharacterData().getPositionX() + 1, gameData.getCharacterData().getPositionY() - 1).isWall()) {
                        gameData.getCharacterData().moveCharacter(1, -1, gameData);
                    }
                    break;
                case '5':
                    item item = floor.checkForItem(gameData.getCharacterData().getPositionX(), gameData.getCharacterData().getPositionY());
                    if (item != null && gameData.getCharacterData().checkForInventorySpace()) {
                        floor.pickUpItem(gameData.getCharacterData().getPositionX(), gameData.getCharacterData().getPositionY());
                        gameData.getCharacterData().addItemToInventory(item);
                    }
                    updateInventory();
                    if (item == null && tile.isStairs() == 1) {
                        gameData.getCharacterData().moveCharacterToAnotherFloor(tile.isStairs(),
                                gameData.getMapData().getFloorUp(gameData.getCharacterData().getCurrentFloor()).getStairsDowns().get(floor.getStairsUps().indexOf(tile)).getX(),
                                gameData.getMapData().getFloorUp(gameData.getCharacterData().getCurrentFloor()).getStairsDowns().get(floor.getStairsUps().indexOf(tile)).getY());
                    } else if (item == null && tile.isStairs() == -1) {
                        gameData.getCharacterData().moveCharacterToAnotherFloor(tile.isStairs(),
                                gameData.getMapData().getFloorDown(gameData.getCharacterData().getCurrentFloor()).getStairsUps().get(floor.getStairsDowns().indexOf(tile)).getX(),
                                gameData.getMapData().getFloorDown(gameData.getCharacterData().getCurrentFloor()).getStairsUps().get(floor.getStairsDowns().indexOf(tile)).getY());
                    }
                    break;
            }
            passTime();
            repositionBoard();
            updateStatistics();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        JLabel label = (JLabel) e.getSource();
        for (int i = 0; i < gameData.getCharacterData().getInventoryContent().size(); i++) {
            if (label == inventoryGrid[i % 8][i / 8]) {
                gameData.getCharacterData().equipItem(i);
                break;
            }
        }
        updateEquipment();
        updateInventory();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
