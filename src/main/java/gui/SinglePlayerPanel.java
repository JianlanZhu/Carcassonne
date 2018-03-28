package edu.cmu.cs.cs214.hw4.gui;

import edu.cmu.cs.cs214.hw4.element.Player;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * SinglePlayerPanel class represent a panel showing a player's game information.
 */
public class SinglePlayerPanel extends JPanel {
    private String playerName;
    private int playerId;
    private Player player;
    private JPanel whole;
    private JLabel nameLabel;
    private JLabel scoreLabel;
    private JLabel followerLabel;

    /**
     * This method is used to create a new player panel using player ID and player name.
     * @param playerId player ID
     * @param playerName player name
     */
    public SinglePlayerPanel(int playerId, String playerName) {
        this.whole = new JPanel();
        this.whole.setSize(100, 100);
        whole.setLayout(new BorderLayout());
        this.playerName = playerName;
        this.playerId = playerId;
        this.player = new Player(playerName);

        setLayout(new BorderLayout());
        nameLabel = new JLabel("No." + playerId + ": " + playerName);
        scoreLabel = new JLabel("Score: " + this.player.getScore());
        followerLabel = new JLabel("Remaining followers: " + this.player.getFollowers());
        whole.add(nameLabel, BorderLayout.NORTH);
        whole.add(scoreLabel, BorderLayout.CENTER);
        whole.add(followerLabel, BorderLayout.SOUTH);
        add(whole);
        setVisible(true);
    }

    /**
     * This method is used to update information in the panel and display the updated information.
     */
    public void update() {
        System.out.println("update No." + this.player.getPlayerId() + "'s info, current followers = " + this.player.getFollowers());
        scoreLabel.setText("Score: " + this.player.getScore());
        followerLabel.setText("Remaining followers: " + this.player.getFollowers());
    }

    /**
     * This method is used to get the player represent in this panel.
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

}
