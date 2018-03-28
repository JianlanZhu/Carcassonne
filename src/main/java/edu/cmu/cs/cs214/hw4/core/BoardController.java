package edu.cmu.cs.cs214.hw4.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Set;

import edu.cmu.cs.cs214.hw4.element.Player;
import edu.cmu.cs.cs214.hw4.element.bean.TileBean;
import org.yaml.snakeyaml.Yaml;
import edu.cmu.cs.cs214.hw4.element.Segment;
import edu.cmu.cs.cs214.hw4.element.Tile;
import edu.cmu.cs.cs214.hw4.parameter.Rules;

/**
 * The BoardController program mainly handle tile operations.
 *
 * @author jianlanz
 *
 */
public class BoardController {
    private Map<String, Tile> coordinates; // <coordinate, Tile>
    private Set<String> nextCoordinates; // <coordinate>
    private List<Integer> tileStack; // <number of tiles left>
    private List<TileBean> tileBeans; // 24 tileBeans
    private Tile currentTile; // what if user wants to change tile
    private Random rand;
    private int remainingTiles;
    private static final Integer[] TILE_STACK = {2, 4, 1, 4, 5, 2, 1, 3, 2, 3, 3, 3, 2, 3, 2, 3, 1, 3, 2, 1, 8, 9, 4, 1};
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // x, y axis, up, right, down, left

    /**
     * This method is used to create a new board controller.
     */
    public BoardController() {
        super();
        this.coordinates = new HashMap<>();
        this.nextCoordinates = new HashSet<>();
        this.tileStack = new ArrayList<>();
        // init tileBeans
        for (int i = 0; i < Rules.TYPE_OF_TILES; i++) {
            this.tileStack.add(TILE_STACK[i]);
        }
        // init tileBean list
        this.tileBeans = new ArrayList<>();
        Yaml yaml = new Yaml();
        String fileName = Rules.TILE_PATH;
        try (InputStream is = new FileInputStream(fileName)) {
            this.tileBeans = (List<TileBean>) yaml.loadAs(is, ArrayList.class);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File " + fileName + " not found!");
        } catch (IOException e) {
            throw new IllegalArgumentException("Error when reading " + fileName + "!");
        }
        this.currentTile = null;
        this.rand = new Random();
        this.remainingTiles = Rules.TOTAL_TILES;
    }

    /* ******************** main functionality ******************** */

    /**
     * This method is used to prepare a tile for user to place on the board.
     *
     * @return Tile return a new tile
     */
    public Tile nextTile() {
        int nextTileId = rand.nextInt(Rules.TYPE_OF_TILES); // 24
        int tileRemain = tileStack.get(nextTileId);
        // if have not used out
        while (tileRemain <= 0) {
            nextTileId = (nextTileId + 1) % 24;
            tileRemain = tileStack.get(nextTileId);
        }
        // if there is tile type left
        TileBean tileBean = tileBeans.get(nextTileId);
        Tile nextTile = new Tile(nextTileId, tileBean);
        tileStack.set(nextTileId, tileRemain - 1);
        currentTile = nextTile;
        System.out.println("Sussessfully get next tile! New tile id = " + nextTile.getTileId());
        return nextTile;
    }

    /**
     * This method is used to provide user a new tile to place.
     *
     * @param tile The tile needs to be replaced
     * @return Tile return a new tile for user to place
     */
    public Tile changeTile(Tile tile) {
        int remain = tileStack.get(tile.getTileId());
        tileStack.set(tile.getTileId(), remain + 1);
        return nextTile();
    }

    /**
     * This method is used to rotate the tile 90 degree clockwise.
     *
     */
    public void rotateTile() {
        currentTile.rotate();
    }

    /**
     * This method is used to place tile on (x, y) on the board and judge the placement validity.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @param tile The tile to place
     * @param players list of players
     * @return boolean return whether user can place tile at coordinate (x, y)
     */
    public boolean placeTile(int x, int y, Tile tile, List<Player> players) {
        // judge whether can put here and update surroundings
        String thisCoordinate = Rules.getCoordinate(x, y);
        // cannot put on other tiles
        if (!nextCoordinates.contains(thisCoordinate)) {
            System.out.println("Can't place here.");
            return false;
        }

        // should place next to at least another tile and border should match
        // try all 4 directions
        for (int i = 0; i < 4; i++) {
            int nx = x + DIRECTIONS[i][0];
            int ny = y + DIRECTIONS[i][1];
            String nextCoordinate = Rules.getCoordinate(nx, ny);
            // if connected
            if (coordinates.containsKey(nextCoordinate)) {
                Tile neighborTile = coordinates.get(nextCoordinate);
                List<Segment> currentBorder = tile.getBorders().get(i);
                List<Segment> neighborBorder = neighborTile.getBorders().get((2 + i) % 4);
                // judge whether border match
                if (currentBorder.size() == neighborBorder.size()) {
                    int numberOfSegment = currentBorder.size();
                    // compare each segment
                    for (int k = 0; k < numberOfSegment; k++) {
                        // if not match
                        if (currentBorder.get(k).getSegmentType() != neighborBorder.get(numberOfSegment - k - 1)
                                .getSegmentType()) {
                            System.out.println("Border not match!");
                            return false;
                        }
                    }
                } else {
                    // border segment numbers not equal
                    System.out.println("Border not match!");
                    return false;
                }
            }
        }
        // if can put here, expand the border
        for (int i = 0; i < 4; i++) {
            int nx = x + DIRECTIONS[i][0];
            int ny = y + DIRECTIONS[i][1];
            String nextCoordinate = Rules.getCoordinate(nx, ny);
            if (!coordinates.containsKey(nextCoordinate) && !nextCoordinates.contains(nextCoordinate)) {
                nextCoordinates.add(nextCoordinate);
            }
        }
        nextCoordinates.remove(thisCoordinate);
        // add it to the board
        tile.setCoordinate(x, y);
        coordinates.put(thisCoordinate, tile);
        this.remainingTiles--;
        // update surrounding cloister and the current coordinate
        int currentSurroundings = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                String coordinate = Rules.getCoordinate(x + i, y + j);
                if (coordinates.containsKey(coordinate)) {
                    currentSurroundings++;
                    Tile neighborTile = coordinates.get(coordinate);
                    int originalSurroundings = neighborTile.getSurroundings();
                    neighborTile.setSurroundings(originalSurroundings + 1);
                }
            }
        }
        // update the surrounding tile number
        tile.setSurroundings(currentSurroundings);
        return true;
    }

    /**
     * This method is used to place follower on the segment and judge the placement validity.
     *
     * @param segment The segment to be placed follower on
     * @param currentPlayer The current player who is placing follower
     * @return boolean return whether user can place follower at the segment
     */
    public boolean placeFollower(Segment segment, Player currentPlayer) {
        if (segment == null) {
            System.out.println("No cloister here!");
            return false;
        }
        Map<Integer, Integer> followers = segment.getFollowers();
        if (!followers.isEmpty()) {
            System.out.println("Already has follower. You cannot place here.");
            return false;
        }
        if (!currentPlayer.placeFollower()) {
            return false;
        }
        // update segment's follower list
        followers.put(currentPlayer.getPlayerId(), 1);
        System.out.println("Successfully place follower on a " + segment.getSegmentType().toString());
        segment.setFollowers(followers);
        return true;
    }

    @Override
    public String toString() {
        return "BroadController{" +
                "\ntiles=" + coordinates +
                ",\n tileStack=" + tileStack +
                ",\n tileBeans=" + tileBeans +
                ",\n currentTile=" + currentTile +
                ",\n rand=" + rand +
                ",\n remainingTiles=" + remainingTiles +
                "}\n";
    }

    /* ******************** getters and setters ******************** */

    public Map<String, Tile> getCoordinates() {
        return coordinates;
    }

    public List<Integer> getTileStack() {
        return tileStack;
    }

    public List<TileBean> getTileBeans() {
        return tileBeans;
    }

    public int getRemainingTiles() {
        return remainingTiles;
    }

    public void setRemainingTiles(int remainingTiles) {
        this.remainingTiles = remainingTiles;
    }

    public Set<String> getNextCoordinates() {
        return nextCoordinates;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }
}
