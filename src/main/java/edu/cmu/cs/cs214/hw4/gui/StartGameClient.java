package edu.cmu.cs.cs214.hw4.gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * StartGameClient class represents a panel that is used to add players to start a new game.
 */
public class StartGameClient extends JPanel {
    private static final int PLAYER_DISPLAY_HEIGHT = 10;
    private static final int PLAYER_DISPLAY_WIDTH = 10;
    private static final int PLAYER_NAME_COLUMN = 20;

    private javax.swing.JFrame parentFrame;
    private JPanel gamePanel;
    private List<String> names;

    /**
     * Initialize a game client.
     *
     * @param parentFrame parent frame
     */
    public StartGameClient(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.names = new ArrayList<>();

        // panel for adding players
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BorderLayout());
        // input hints
        JLabel playerLabel = new JLabel("Player name: ");
        // input player name
        JTextField playerName = new JTextField(PLAYER_NAME_COLUMN);
        // add elements
        playerPanel.add(playerLabel, BorderLayout.WEST);
        playerPanel.add(playerName, BorderLayout.CENTER);

        // panel for buttons
        JPanel buttonPanel = new JPanel();
        // different buttons
        JButton playerAdd = new JButton("Add player");
        JButton playerRemove = new JButton("Remove player");
        JButton gameStart = new JButton("Start Carcassonne");
        // add elements
        buttonPanel.add(playerAdd);
        buttonPanel.add(playerRemove);
        buttonPanel.add(gameStart);

        // handle the add and remove of player
        JTextArea playerDisplay = new JTextArea(PLAYER_DISPLAY_HEIGHT, PLAYER_DISPLAY_WIDTH);

        // Observer to add a player
        ActionListener addPlayerListener = e -> {
            String newPlayerName = playerName.getText().trim();
            if (names.size() < 5) {
                playerDisplay.setText("");
                names.add(newPlayerName);
                for (int i = 0; i < names.size(); i++) {
                    playerDisplay.append("No." + i + ": " + names.get(i) + "\n");
                }
                playerName.setText("");
                playerName.requestFocus();
            }
        };
        playerAdd.addActionListener(addPlayerListener);

        // Observer to remove a player
        ActionListener removePlayerListener = e -> {
            String idx = playerName.getText().trim();
            if (idx != null && idx.length() != 0) {
                int removeIdx = Integer.parseInt(idx);
                if (names.size() > 0 && removeIdx < names.size()) {
                    playerDisplay.setText("");
                    names.remove(removeIdx);
                    for (int i = 0; i < names.size(); i++) {
                        playerDisplay.append("No." + i + ": " + names.get(i) + "\n");
                    }
                    playerName.setText("");
                    playerName.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid user index.");
                    playerName.setText("");
                }
            }
        };
        playerRemove.addActionListener(removePlayerListener);

        // observer to start game
        ActionListener gameStartListener = e -> {
            if (names.size() > 0 && names.size() < 6) {
                startGameSession();
            } else {
                JOptionPane.showMessageDialog(null, "Player number not valid! Please add or remove players.");
            }
        };
        gameStart.addActionListener(gameStartListener);

        // add the components
        setLayout(new BorderLayout());
        add(playerPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(playerDisplay, BorderLayout.SOUTH);
        setVisible(true);
    }

    /**
     * This method is used to start a new game session.
     */
    public void startGameSession() {
        parentFrame.dispose();
        parentFrame = null;

        JFrame gameFrame = new JFrame("Carcassonne");
        this.gamePanel = new GamePanel(names);
        gameFrame.add(gamePanel);
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // pack
        gameFrame.pack();
        gameFrame.setResizable(true);
        gameFrame.setVisible(true);
    }
}
