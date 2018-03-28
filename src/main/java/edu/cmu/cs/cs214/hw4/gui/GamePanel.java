package edu.cmu.cs.cs214.hw4.gui;

import edu.cmu.cs.cs214.hw4.core.BoardController;
import edu.cmu.cs.cs214.hw4.core.GameController;
import edu.cmu.cs.cs214.hw4.element.Player;
import edu.cmu.cs.cs214.hw4.element.Tile;
import edu.cmu.cs.cs214.hw4.parameter.Rules;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GamePanel is a class that represents a panel where the game is playing.
 */
public class GamePanel extends JPanel {
    // GUI related
    private List<SinglePlayerPanel> players;
    private int originX, originY; // the absolute coordinate of the first tile
    private JPanel boardPanel;
    private JScrollPane scrollPane;
    private JPanel playerPanel;
    private JButton nextPlayer;
    private JButton changeTile;
    private JButton endGame;
    private JButton rotateTile;
    private JTextArea currentPlayerPanel;
    private GridBagConstraints gbc;
    private JButton[][] currentSegmentChoices;
    private Color[] colorList = {Color.RED, Color.BLUE, Color.GRAY, Color.YELLOW, Color.GREEN};
    // logic related
    private GameController gameController;
    private BoardController boardController;
    private Tile currentTile;
    private boolean isPlacingTile;
    private boolean isPlacingFollower;
    // constants
    private static final int TILE_SIZE = 100;
    private static final int BUTTON_HEIGHT = 20;
    private int leftBorder;
    private int upBorder;
    private Map<String, JButton> buttonMap;
    private Map<JButton, String> coordinateMap;

    /**
     * Create a new game panel.
     *
     * @param names list of player's name
     */
    public GamePanel(List<String> names) {
        // first click listener
        startNewGame(names);
    }

    // listeners
    private ActionListener placeFollowerListener = e -> {
        if (isPlacingFollower) {
            JButton segment = (JButton) e.getSource();
            for (int[] position : Rules.SEGMENT_INDICES) {
                if (currentSegmentChoices[position[0]][position[1]] == segment) {
                    Tile currentTile = boardController.getCurrentTile();
                    int[] coordinate = getSegmentCoordinate(position);
                    if (gameController.placeFollower(currentTile, coordinate[0], coordinate[1])) {
                        segment.setText(String.valueOf(gameController.getCurrentPlayer().getPlayerId()));
                        segment.setBorderPainted(true);
                        segment.setOpaque(true);
                        segment.setForeground(colorList[gameController.getCurrentPlayer().getPlayerId()]);
                        // change mode and update score
                        System.out.println("Update player score over.");
                        isPlacingFollower = false;
                        isPlacingTile = false;
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "You cannot place follower here!");
                    }
                }
            }
        }
    };

    private ActionListener placeTileListener = e -> {
        if (isPlacingTile) {
            // set new image
            JButton tile = (JButton) e.getSource();
            Point clickedPosition = tile.getLocation();
            System.out.println(clickedPosition.toString());
            int[] coordinate = Rules.parseCoordinate(coordinateMap.get(tile));
            Tile current = boardController.getCurrentTile();
            if (gameController.placeTile(coordinate[0], coordinate[1], current)) {
                // if can place
                System.out.println("Successfully place tile on (" + coordinate[0] + ", " + coordinate[1] + ")");
                ImageIcon icon = (ImageIcon) rotateTile.getIcon();
                tile.setIcon(icon);
                rotateTile.setIcon(null);
                // change mode
                isPlacingTile = false;
                isPlacingFollower = true;
                // expand
                int rightMove = coordinate[0] == leftBorder ? 1 : 0;
                int downMove = coordinate[1] == upBorder ? 1 : 0;
                if (rightMove == 1 || downMove == 1) {
                    updateGlobalPosition(rightMove, downMove);
                    // update border
                    leftBorder -= rightMove;
                    upBorder += downMove;
                }
                int[][] directions = gameController.getDIRECTIONS();
                for (int i = 0; i < 4; i++) {
                    int nx = coordinate[0] + directions[i][0];
                    int ny = coordinate[1] + directions[i][1];
                    String coor = Rules.getCoordinate(nx, ny);
                    if (!buttonMap.containsKey(coor)) {
                        JButton newButton = getNewButton();
                        buttonMap.put(coor, newButton);
                        coordinateMap.put(newButton, coor);
                        gbc.gridx = originX + nx;
                        gbc.gridy = originY - ny;
                        boardPanel.add(newButton, gbc);
                    }
                }
                tile.setLayout(new GridLayout(5, 5));
                currentSegmentChoices = new JButton[5][5];
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        currentSegmentChoices[i][j] = new JButton();
                        currentSegmentChoices[i][j].setOpaque(false);
                        currentSegmentChoices[i][j].setBorderPainted(false);
                        currentSegmentChoices[i][j].setSize(new Dimension(20, 20));
                        currentSegmentChoices[i][j].addActionListener(placeFollowerListener);
                        tile.add(currentSegmentChoices[i][j]);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "You cannot place at (" + coordinate[0] + ", " + coordinate[1] + ")!");
            }
        }
    };

    private ActionListener nextPlayerListener = e -> {
        if (isPlacingTile == false) {
            isPlacingTile = true;
            isPlacingFollower = false;
            Tile nextTile = gameController.nextPlayer();
            // update score
            updatePlayerScore();
            try {
                BufferedImage img = ImageIO.read(new File(Rules.getPicturePath(nextTile.getTileId())));
                rotateTile.setIcon(new ImageIcon(img));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("************************************************************************");
            currentPlayerPanel.setText("Current playing:" + gameController.getCurrentPlayer().getPlayerId());
        } else {
            JOptionPane.showMessageDialog(null, "You have to place tile!");
        }
    };

    private ActionListener changeTileListener = e -> {
        System.out.println("Tile required to change: " + currentTile.getTileId());
        Tile nextTile = gameController.changeTile(currentTile);
        System.out.println("Successfully change tile! New tile id = " +
                gameController.getBoardController().getCurrentTile().getTileId());
        try {
            BufferedImage img = ImageIO.read(new File(Rules.getPicturePath(nextTile.getTileId())));
            rotateTile.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        currentTile = nextTile;
    };

    private ActionListener endGameListener = e -> {
        // if not over, force over
        if (!gameController.checkGameOver()) {
            gameController.endGame();
        }
        updatePlayerScore();
        List<Player> winners = gameController.getWinner();
        StringBuffer winnerList = new StringBuffer();
        for (Player winner : winners) {
            winnerList.append(winner.getName() + ", ");
        }
        String answer = winnerList.toString();
        JOptionPane.showMessageDialog(null, "Winners: " + answer.substring(0, answer.length() - 2));
    };

    private ActionListener rotateTileListener = e -> {
        ImageIcon imgIcon = (ImageIcon) rotateTile.getIcon();
        BufferedImage img = (BufferedImage) imgIcon.getImage();
        img = getRotatedImage(img);
        rotateTile.setIcon(new ImageIcon(img));
        gameController.rotateTile();
    };

    /**
     * This method is used to start a new game using player's name list.
     *
     * @param names players' name
     */
    public void startNewGame(List<String> names) {
        this.isPlacingTile = true;
        this.isPlacingFollower = false;
        this.gameController = new GameController();
        this.boardController = gameController.getBoardController();
        this.buttonMap = new HashMap<>();
        this.coordinateMap = new HashMap<>();
        this.players = new ArrayList<>();
        this.leftBorder = -1;
        this.upBorder = 1;
        // board layout
        boardPanel = new JPanel();
        boardPanel.setSize(14300, 14300);
        boardPanel.setLayout(new GridBagLayout());
        this.gbc = new GridBagConstraints();

        // init beginning tile
        originX = 1;
        originY = 1;
        gbc.gridx = 1;
        gbc.gridy = 1;
        JButton first = new JButton();
        first.setSize(new Dimension(TILE_SIZE, TILE_SIZE));
        try {
            Image img = ImageIO.read(new File(Rules.getPicturePath(3)));
            first.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        boardPanel.add(first, gbc);
        buttonMap.put("0,0", first);
        coordinateMap.put(first, "0,0");

        gbc.gridx = 1;
        gbc.gridy = 0;
        JButton next0 = getNewButton();
        boardPanel.add(next0, gbc);
        buttonMap.put("0,1", next0);
        coordinateMap.put(next0, "0,1");

        gbc.gridx = 2;
        gbc.gridy = 1;
        JButton next1 = getNewButton();
        boardPanel.add(next1, gbc);
        buttonMap.put("1,0", next1);
        coordinateMap.put(next1, "1,0");

        gbc.gridx = 1;
        gbc.gridy = 2;
        JButton next2 = getNewButton();
        boardPanel.add(next2, gbc);
        buttonMap.put("0,-1", next2);
        coordinateMap.put(next2, "0,-1");

        gbc.gridx = 0;
        gbc.gridy = 1;
        JButton next3 = getNewButton();
        boardPanel.add(next3, gbc);
        buttonMap.put("-1,0", next3);
        coordinateMap.put(next3, "-1,0");

        // use scroll pane
        scrollPane = new JScrollPane(boardPanel);
        scrollPane.setPreferredSize(new Dimension(800, 800));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        // add the board with scrollPane
        add(scrollPane);

        // init player panel
        List<Player> playerList = new ArrayList<>();
        this.playerPanel = new JPanel();
        this.playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));
        for (int i = 0; i < names.size(); i++) {
            SinglePlayerPanel newPlayPanel = new SinglePlayerPanel(i, names.get(i));
            playerPanel.add(newPlayPanel);
            players.add(newPlayPanel);
            playerList.add(newPlayPanel.getPlayer());
        }
        // set the player list
        this.gameController.setPlayers(playerList);

        // next player panel
        JPanel nextPlayerPanel = new JPanel();
        nextPlayerPanel.setSize(TILE_SIZE, TILE_SIZE);
        nextPlayer = new JButton("Next Player");
        nextPlayer.setSize(new Dimension(TILE_SIZE, BUTTON_HEIGHT));
        nextPlayerPanel.add(nextPlayer);
        nextPlayer.addActionListener(nextPlayerListener);

        // change tile panel
        JPanel changeTilePanel = new JPanel();
        changeTilePanel.setSize(TILE_SIZE, TILE_SIZE);
        changeTile = new JButton("Change Tile");
        changeTile.setSize(new Dimension(TILE_SIZE, BUTTON_HEIGHT));
        changeTilePanel.add(changeTile);
        changeTile.addActionListener(changeTileListener);

        // end game panel
        JPanel endGamePanel = new JPanel();
        endGamePanel.setSize(TILE_SIZE, TILE_SIZE);
        endGame = new JButton("End game");
        endGame.setSize(new Dimension(TILE_SIZE, BUTTON_HEIGHT));
        endGamePanel.add(endGame);
        endGame.addActionListener(endGameListener);

        // rotate tile panel
        JPanel rotateTilePanel = new JPanel();
        rotateTile = new JButton();
        rotateTilePanel.add(rotateTile);
        rotateTile.addActionListener(rotateTileListener);

        // add all component
        this.playerPanel.add(nextPlayerPanel);
        this.playerPanel.add(changeTilePanel);
        this.playerPanel.add(endGamePanel);
        this.playerPanel.add(rotateTilePanel);

        add(playerPanel);
        // start the game in the core logic
        gameController.startGame();
        currentTile = gameController.nextPlayer();

        currentPlayerPanel = new JTextArea("Current player: No." + gameController.getCurrentPlayer().getPlayerId());
        this.playerPanel.add(currentPlayerPanel);

        try {
            BufferedImage img = ImageIO.read(new File(Rules.getPicturePath(currentTile.getTileId())));
            rotateTile.setIcon(new ImageIcon(img));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // make it visible
        setVisible(true);
    }

    /**
     * Get an empty tile that can be place on next time.
     *
     * @return a new empty tile
     */
    public JButton getNewButton() {
        JButton next = new JButton();
        next.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));//Size(new Dimension(90, 90));
        next.addActionListener(placeTileListener);
        return next;
    }

    /**
     * Get a rotated version of a given image.
     *
     * @param image image to be rotated
     * @return image rotated
     */
    public BufferedImage getRotatedImage(BufferedImage image) {
        AffineTransform at = new AffineTransform();
        at.rotate(Math.PI / 2, image.getWidth() / 2, image.getHeight() / 2);//(radian,arbit_X,arbit_Y)
        AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        BufferedImage newImage = ato.filter(image, null);//(sourse,destination)
        return newImage;
    }

    /**
     * This method is used to expand the board.
     *
     * @param rightMove steps moving to the right
     * @param downMove  steps moving to the left
     */
    public void updateGlobalPosition(int rightMove, int downMove) {
        // update origin coordinate
        originX += rightMove;
        originY += downMove;
        JPanel newPanel = new JPanel();
        newPanel.setLayout(new GridBagLayout());
        for (String position : buttonMap.keySet()) {
            int[] coordinate = Rules.parseCoordinate(position);
            JButton button = buttonMap.get(position);
            gbc.gridx = originX + coordinate[0];
            gbc.gridy = originY - coordinate[1];
            newPanel.add(button, gbc);
        }
        this.scrollPane.setViewportView(newPanel);
        boardPanel = newPanel;
    }

    /**
     * This method is used to get the border index and index in the border of the target segment.
     *
     * @param position position of target segment in a 5 * 5 grid
     * @return border index and index in the border
     */
    public int[] getSegmentCoordinate(int[] position) {
        int[] coordinate = new int[2];
        if (position[0] == 2 && position[1] == 2) {
            coordinate[0] = -1;
            coordinate[1] = -1;
        } else if (position[0] == 0) {
            coordinate[0] = 0;
            coordinate[1] = position[1] - 1;
        } else if (position[1] % 5 == 4) {
            coordinate[0] = 1;
            coordinate[1] = position[0] - 1;
        } else if (position[0] == 4) {
            coordinate[0] = 2;
            coordinate[1] = 3 - position[1];
        } else if (position[1] == 0) {
            coordinate[0] = 3;
            coordinate[1] = 3 - position[0];
        }
        return coordinate;
    }

    /**
     * This method is used to update player's scores.
     */
    public void updatePlayerScore() {
        for (SinglePlayerPanel currentPanel : players) {
            System.out.println("Current player: No." + gameController.getCurrentPlayer().getPlayerId());
            currentPanel.update();
        }
    }
}
