package edu.cmu.cs.cs214.hw4.parameter;

import java.awt.Color;

/**
 * Carcassonne rules and common used methods.
 */
public class Rules {
    public static final int TYPE_OF_TILES = 24;
    public static final int TOTAL_TILES = 72;
    public static final String TILE_PATH = "src/main/resources/tileBeans.yml";
    public static final int FIRST_TILE_ID = 3; // 20 as test
    public static final int MAX_FOLLOWER_NUMBER = 8;
    public static final int CLOISTER_COMPLETE_SCORE = 9;
    public static final int SUPPORT_FIELD_SCORE = 3;
    public static final int MAX_PLAYER_NUMBER = 5;

    public static final int[][] SEGMENT_INDICES =
        {{2, 2}, {0, 1}, {0, 2}, {0, 3}, {1, 0}, {1, 4}, {2, 0}, {2, 4}, {3, 0}, {3, 4}, {4, 1}, {4, 2}, {4, 3}};

    public static final String getCoordinate(int x, int y) {
        return x + "," + y;
    }

    public static final int[] parseCoordinate(String str) {
        int[] coordinate = new int[2];
        String[] coor = str.split(",");
        coordinate[0] = Integer.parseInt(coor[0]);
        coordinate[1] = Integer.parseInt(coor[1]);
        return coordinate;
    }

    public static final String getPicturePath(int i) {
        return "src/main/resources/images/tile" + i + ".png";
    }
}
