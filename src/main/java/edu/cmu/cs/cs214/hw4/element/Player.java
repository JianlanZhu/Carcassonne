package edu.cmu.cs.cs214.hw4.element;

import edu.cmu.cs.cs214.hw4.parameter.Rules;

/**
 * This Player program represent a player in the game.
 */
public class Player {
    private String name; // playerId
    private int playerId;
    private int score;
    private int followers;
    private boolean active;

    /**
     * The method is used to create a new player using a name.
     * @param name player's name
     */
    public Player(String name) {
        super();
        this.name = name;
        this.score = 0;
        this.followers = Rules.MAX_FOLLOWER_NUMBER;
        this.active = false;
    }

    /* ******************** main functionality ******************** */

    /**
     * The method is used to judge whether the player can place follower.
     *
     * @return return whether the player can place follower onto the board
     */
    public boolean placeFollower() {
        // not enough follower to place
        if (followers <= 0) {
            System.out.println("Not enough followers.");
            return false;
        }
        followers--;
        return true;
    }

    /**
     * The method is used to take back followers.
     *
     * @param number number of followers the player takes back
     */
    public void retrieveFollower(int number) {
        this.followers += number;
    }

    /**
     * This method is used to add a player's score.
     *
     * @param addition score needs to be added
     */
    public void addScore(int addition) {
        score += addition;
    }

    /* ******************** getters and setters ******************** */
    public String getName() {
        return name;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getScore() {
        return score;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
