package edu.cmu.cs.cs214.hw4.core;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import edu.cmu.cs.cs214.hw4.element.Player;
import edu.cmu.cs.cs214.hw4.element.Segment;
import edu.cmu.cs.cs214.hw4.element.Tile;
import edu.cmu.cs.cs214.hw4.parameter.SegmentType;
import edu.cmu.cs.cs214.hw4.parameter.Rules;

import static edu.cmu.cs.cs214.hw4.parameter.SegmentType.FIELD;

/**
 * The GameController program mainly interact with UI and deal with functions related to players' score.
 *
 * @author jianlanz
 */
public class GameController {
    private BoardController boardController;
    private List<Player> players; // Player list, ith player
    private int currentPlayerId;
    private Player currentPlayer;
    private List<Player> winner;
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // x, y axis, up, right, down, left

    /**
     * This method is used to create a new game controller.
     */
    public GameController() {
        this.boardController = new BoardController();
        this.players = new ArrayList<>();
        this.currentPlayerId = -1;
        this.currentPlayer = null;
        this.winner = new ArrayList<>();
    }

    /* ******************** main functionality ******************** */

    /**
     * This method is used to start a new game with current players.
     *
     * @return boolean return whether can start the game with current players
     */
    public boolean startGame() {
        // return false if start failure
        if (players == null || players.size() < 2) {
            System.out.println("There are not enough players!");
            return false;
        }
        // put the first tile at (0, 0)
        Map<String, Tile> board = boardController.getCoordinates();
        Tile firstTile = new Tile(Rules.FIRST_TILE_ID, boardController.getTileBeans().get(Rules.FIRST_TILE_ID));
        board.put(Rules.getCoordinate(0, 0), firstTile);
        firstTile.setCoordinate(0, 0);
        int remain = boardController.getTileStack().get(Rules.FIRST_TILE_ID);
        boardController.getTileStack().set(Rules.FIRST_TILE_ID, remain - 1);
        boardController.setRemainingTiles(Rules.TOTAL_TILES - 1);
        boardController.getNextCoordinates().add("0,1");
        boardController.getNextCoordinates().add("0,-1");
        boardController.getNextCoordinates().add("-1,0");
        boardController.getNextCoordinates().add("1,0");
        // update playerId
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setPlayerId(i);
        }
        return true;
    }

    /**
     * This method is used to add a new player with a name to the game.
     *
     * @param name Player's name
     * @return boolean return whether can add another player with a particular name
     */
    public boolean addPlayer(String name) {
        // if there have been 5 players
        if (players.size() >= Rules.MAX_PLAYER_NUMBER) {
            System.out.println("Cannot add more player!");
            return false;
        }
        Player newPlayer = new Player(name);
        players.add(newPlayer);
        return true;
    }

    /**
     * This method is used to remove a certain player out of the game.
     *
     * @param index Player's playerId
     * @return boolean return whether can remove the player out of the game
     */
    public boolean removePlayer(int index) {
        // if no players right now
        if (players.size() == 0) {
            System.out.println("There is no player can be removed right now.");
            return false;
        }
        players.remove(index);
        return true;
    }

    /**
     *
     * @return if the game is over
     */
    public boolean checkGameOver() {
        return boardController.getRemainingTiles() == 0;
    }

    /**
     * This method is used to switch to the next user and provide the next user a tile to place.
     *
     * @return Tile return a new tile for the next player to place
     */
    public Tile nextPlayer() {
        // update score first
        Tile current = boardController.getCurrentTile();
        // avoid duplicated calculate score
        if (current != null) {
            Set<Segment> visited = new HashSet<>();
            // traverse every segment to see if it is complete
            for (List<Segment> border : current.getBorders()) {
                for (Segment segment : border) {
                    if (!visited.contains(segment)) {
                        visited.add(segment);
                        // if road or city is completed
                        if (checkSegmentComplete(segment)) {
                            updateSegmentScore(segment);
                        }
                    }
                }
            }
            // check cloister complete, check surrounding tiles
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int nx = current.getX() + i;
                    int ny = current.getY() + j;
                    String coordinate = Rules.getCoordinate(nx, ny);
                    // if there is a tile here
                    Tile tile = boardController.getCoordinates().getOrDefault(coordinate, null);
                    if (checkCloisterComplete(tile)) {
                        Segment cloister = tile.getCloister();
                        updateSegmentScore(cloister);
                    }
                }
            }
        }
        // if has next player, return the tile given to him
        if (currentPlayerId > 0) {
            currentPlayer.setActive(false);
        }
        // if there is no tile to play
        if (checkGameOver()) {
            endGame();
            return null;
        }
        currentPlayerId = (currentPlayerId + 1) % players.size();
        currentPlayer = players.get(currentPlayerId);
        currentPlayer.setActive(true);
        return nextTile();
    }

    /**
     * This method is used to provide a new tile for player to place.
     *
     * @param tile The tile taken back
     * @return Tile return a new tile to place
     */
    public Tile changeTile(Tile tile) {
        return boardController.changeTile(tile);
    }

    /**
     * This method is used to provide a new tile to place.
     *
     * @return Tile return a new tile
     */
    public Tile nextTile() {
        return boardController.nextTile();
    }

    /**
     * This method is used to rotate the tile 90 degree clockwise.
     *
     */
    public void rotateTile() {
        boardController.rotateTile();
    }

    /**
     * This method is used to place tile on (x, y) on the board and judge the placement validity.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param tile The tile to place
     * @return boolean return whether user can place tile at coordinate (x, y)
     */
    public boolean placeTile(int x, int y, Tile tile) {
        if (boardController.placeTile(x, y, tile, players)) {
            // merge
            mergeSegment(x, y, tile);
            // update cloister
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method is used to merge segments in tile with its surrounding tiles' segments.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param tile The tile to be merged
     */
    public void mergeSegment(int x, int y, Tile tile) {
        // merge segment
        for (int i = 0; i < 4; i++) {
            int nx = x + DIRECTIONS[i][0];
            int ny = y + DIRECTIONS[i][1];
            String nextCoordinate = Rules.getCoordinate(nx, ny);
            List<List<Segment>> allCurrentBorders = tile.getBorders();
            // if connected with tile (nx, ny)
            if (boardController.getCoordinates().containsKey(nextCoordinate)) {
                Tile neighborTile = boardController.getCoordinates().get(nextCoordinate);
                List<Segment> currentBorder = allCurrentBorders.get(i);
                List<Segment> neighborBorder = neighborTile.getBorders().get((2 + i) % 4);
                int numberOfSegment = currentBorder.size();
                for (int k = 0; k < numberOfSegment; k++) {
                    // merge
                    Segment currentSegment = currentBorder.get(k);
                    // get all the positions that need to update
                    Segment neighborSegment = neighborBorder.get(numberOfSegment - k - 1);
                    if (currentSegment.getFeatures() != neighborSegment.getFeatures()) {
                        Segment.merge(neighborSegment, currentSegment);
                    } else {
                        for (Segment seg : currentSegment.getFeatures()) {
                            int open = seg.getOpen();
                            seg.setOpen(open - 2);
                        }
                    }
                    // change the reference of that segment
                    neighborBorder.set(numberOfSegment - k - 1, neighborSegment);
                }
            }
        }
    }

    /**
     * This method is used to update players score based on the number of followers in the segment.
     *
     * @param segment The segment which may contains followers
     */
    public void updateSegmentScore(Segment segment) {
        // update city and road score
        if (segment.getFollowers().isEmpty() || segment.getSegmentType() == FIELD) {
            return;
        }
        Map<Integer, Integer> numberOfFollowers = segment.getFollowers();
        Set<Integer> ownerIds = numberOfFollowers.keySet();
        int maxFollowerNumber = 0;
        // find max follower number
        for (Integer i : numberOfFollowers.values()) {
            if (i > maxFollowerNumber) {
                maxFollowerNumber = i;
            }
        }
        // take back followers and update scores
        for (Integer playerId : ownerIds) {
            Player player = players.get(playerId);
            // take back followers, farmers cannot be taken back
            // if the game is over, do not take back
            if (boardController.getRemainingTiles() != 0) {
                player.retrieveFollower(numberOfFollowers.get(playerId));
            }
            // update cloister
            if (segment.getSegmentType() == SegmentType.CLOISTER) {
                player.addScore(Rules.CLOISTER_COMPLETE_SCORE);
                break;
            }
            // update score
            if (numberOfFollowers.get(playerId) == maxFollowerNumber) {
                int tileNumber = segment.getAreas().size();
                SegmentType type = segment.getSegmentType();
                int updatedScore = segment.getOpen() == 0 ?
                        tileNumber * type.getCompletePoints() + 2 * segment.getPennants() :
                        tileNumber * type.getIncompletePoints() + segment.getPennants();
                System.out.println("Areas = " + tileNumber + ", pennants = " + segment.getPennants());
                player.addScore(updatedScore);
            }
        }
        // clear all followers
        segment.getFollowers().clear();
    }

    /**
     * This method is used to update player's score based on the current incomplete cloister's information.
     */
    public void updateCloisterScore() {
        for (Tile tile : boardController.getCoordinates().values()) {
            Segment cloister = tile.getCloister();
            if (cloister != null && !cloister.getFollowers().isEmpty()) {
                for (Integer playerId : cloister.getFollowers().keySet()) {
                    Player player = players.get(playerId);
                    player.addScore(1 + tile.getSurroundings());
                }
            }
        }
    }


    /**
     * This method is used to judge whether the segment is complete.
     *
     * @param segment The segment to be checked if complete.
     * @return boolean return whether the segment is complete
     */
    public boolean checkSegmentComplete(Segment segment) {
        return segment.getOpen() == 0 && segment.getSegmentType() != SegmentType.CLOISTER;
    }

    /**
     * This method is used to check if a cloister feature is complete.
     *
     * @param tile chosen tile
     * @return whether the cloister is complete
     */
    public boolean checkCloisterComplete(Tile tile) {
        return tile != null && tile.getSurroundings() == 8 && tile.getCloister() != null;
    }


    /**
     *
     * @param tile chosen tile
     * @param border chosen border
     * @param index target segment index
     * @return boolean whether can place follower here
     */
    public boolean placeFollower(Tile tile, int border, int index) {
        Segment segment = getSegment(tile, border, index);
        boolean placeResult = boardController.placeFollower(segment, this.currentPlayer);
        System.out.println("No." + currentPlayer.getPlayerId() + " remains follower: " + currentPlayer.getFollowers());
        return placeResult;
    }

    /**
     * This method is used to update all players' scores when the game is over.
     */
    private void updateFinalScore() {
        // (1) incomplete road, cloister, city
        // (2) farms connected to cities
        // first, update cloister score
        updateCloisterScore();
        // update field scores
        Map<String, Tile> board = boardController.getCoordinates();
        Set<Set<Segment>> visitedSegments = new HashSet<>();
        Set<Set<Segment>> visitedCities = new HashSet<>(); // incomplete city, road and field
        for (Tile tile : board.values()) {
            // if the current tile contains city
            if (!tile.getCities().isEmpty()) {
                for (Segment city : tile.getCities()) {
                    // the city has to be complete
                    if (city.getOpen() != 0) {
                        continue;
                    }
                    if (visitedCities.contains(city.getFeatures())) {
                        continue;
                    }
                    System.out.println("Find new complete city! Area = " + city.getAreas().size());
                    visitedCities.add(city.getFeatures());
                    Set<Set<Segment>> visitedFields = new HashSet<>();
                    for (Segment field : city.getSupportFields()) {
                        if (!visitedFields.contains(field.getFeatures())) {
                            System.out.println("Find new support field! Area = " + field.getAreas().size());
                            visitedFields.add(field.getFeatures());
                            updateFinalFieldScore(field);
                        }
                    }
                }
            }
            // update incomplete city and road
            List<List<Segment>> tileBorders = tile.getBorders();
            for (List<Segment> border : tileBorders) {
                for (Segment segment : border) {
                    if (!visitedSegments.contains(segment.getFeatures()) && segment.getOpen() != 0 && segment.getSegmentType() != FIELD) {
                        updateSegmentScore(segment);
                        // add it to the visited list
                        visitedSegments.add(segment.getFeatures());
                    }
                }
            }
        }
    }

    /**
     * This method is used to get a targeted segment.
     *
     * @param tile chosen tile
     * @param border chosen border
     * @param index index of target segment
     * @return target segment
     */
    public Segment getSegment(Tile tile, int border, int index) {
        Segment target = null;
        List<Segment> selectedBorder = (border < 0 ? null : tile.getBorders().get(border));
        if (selectedBorder == null) {
            target = tile.getCloister();
        } else if (selectedBorder.size() == 1) {
            target = selectedBorder.get(0);
        } else {
            target = selectedBorder.get(index);
        }
        return target;
    }

    /**
     * This method is used to update players' score at the end of the game
     * based on the farms they have which support a completed city
     *
     * @param farm The field segment that may support a completed city
     */
    public void updateFinalFieldScore(Segment farm) {
        if (farm.getFollowers().isEmpty()) {
            return;
        }
        Map<Integer, Integer> numberOfFollowers = farm.getFollowers();
        Set<Integer> ownerIds = numberOfFollowers.keySet();
        int maxFollowerNumber = 0;
        // find max follower number
        for (Integer i : numberOfFollowers.values()) {
            if (i > maxFollowerNumber) {
                maxFollowerNumber = i;
            }
        }
        for (Integer playerId : ownerIds) {
            Player player = players.get(playerId);
            // update score
            if (numberOfFollowers.get(playerId) == maxFollowerNumber) {
                player.addScore(Rules.SUPPORT_FIELD_SCORE);
            }
        }
    }

    /**
     * This method is used to get a winner list based on their scores.
     *
     * @return List return winner list
     */
    public List<Player> getWinner() {
        winner = new ArrayList<>();
        int highestScore = -1;
        for (Player player : players) {
            if (player.getScore() > highestScore) {
                highestScore = player.getScore();
            }
        }
        for (Player player : players) {
            if (player.getScore() == highestScore) {
                winner.add(player);
            }
        }
        return winner;
    }

    /**
     * This method is used to update players' score and return the winners list
     * at the end of the game.
     *
     * @return List return the winner list
     */
    public List<Player> endGame() {
        // calculate final score
        updateFinalScore();
        return getWinner();
    }

    /* ******************** getters and setters ******************** */
    public BoardController getBoardController() {
        return boardController;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public static int[][] getDIRECTIONS() {
        return DIRECTIONS;
    }
}
