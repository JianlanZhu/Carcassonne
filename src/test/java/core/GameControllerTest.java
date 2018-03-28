package edu.cmu.cs.cs214.hw4.core;

import edu.cmu.cs.cs214.hw4.element.Player;
import edu.cmu.cs.cs214.hw4.element.Segment;
import edu.cmu.cs.cs214.hw4.element.Tile;
import edu.cmu.cs.cs214.hw4.element.bean.TileBean;
import org.junit.Before;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class GameControllerTest {
    GameController gameController;
    BoardController boardController;
    @Before
    public void setUp() {
        gameController = new GameController();
        boardController = gameController.getBoardController();
    }

    @Test
    public void testGameController() {
        // cannot start game: not enough players
        boardController.toString();
        assertEquals(gameController.startGame(), false);

        // add two players
        assertEquals(false, gameController.removePlayer(0));
        gameController.addPlayer("Jay");
        gameController.addPlayer("Micheal");
        gameController.addPlayer("Ao");
        gameController.addPlayer("Sharada");
        gameController.addPlayer("Sky");
        assertEquals(false, gameController.addPlayer("cannotAdd"));
        gameController.removePlayer(2);
        gameController.removePlayer(2);
        gameController.removePlayer(2);
        // now can start game
        assertEquals(gameController.startGame(), true);
        assertEquals(gameController.getPlayers().size(), 2); // have passed
        List<TileBean> tileBeans = gameController.getBoardController().getTileBeans();
        // test place tile

        // tile1 test
        Tile tile1 = gameController.nextPlayer();
        System.out.println("Current player is " + gameController.getCurrentPlayer().getName());
        while (tile1.getTileId() != 4) {
            tile1 = gameController.changeTile(tile1);
        }
        assertEquals(false, gameController.placeTile(0, 0, tile1)); // not related to any tiles
        assertEquals(false, gameController.placeTile(-2, 0, tile1)); // not related to any tiles
        assertEquals(false, gameController.placeTile(1, 0, tile1)); // not match
        gameController.rotateTile();
        gameController.rotateTile();
        gameController.rotateTile();
        assertEquals(true, gameController.placeTile(1, 0, tile1)); // match
        System.out.println("CurrentPlayerId = " + gameController.getCurrentPlayer().getPlayerId());
        assertEquals(true, gameController.placeFollower(tile1, 3, 0));
        System.out.println("City Follower = " + tile1.getBorders().get(1).get(0).getFollowers().size());
        assertEquals(70, boardController.getRemainingTiles()); // now 70 tiles left
        assertEquals(1, boardController.getCoordinates().get("0,0").getSurroundings());
        assertEquals(1, boardController.getCoordinates().get("1,0").getSurroundings());

        //test city merge and farm support
        Tile tile2 = gameController.nextPlayer();
        System.out.println("current player is " + gameController.getCurrentPlayer().getName());
        while (tile2.getTileId() != 0) {
            tile2 = gameController.changeTile(tile1);
        }
        System.out.println("TileId = " + tile2.getTileId());
        assertEquals(false, gameController.placeTile(1, 1, tile2)); // not match
        assertEquals(true, gameController.placeTile(0, 1, tile2)); // match
        assertEquals(69, boardController.getRemainingTiles());
        assertEquals(false, gameController.checkGameOver());
        assertEquals(true, gameController.placeFollower(tile2, -1, -1)); // can place
        assertEquals(2, tile2.getSurroundings());

        //test field support cities
        Tile tile3 = gameController.nextPlayer();
        while (tile3.getTileId() != 1) {
            tile3 = gameController.changeTile(tile1);
        }
        assertEquals(true, gameController.placeTile(1, 1, tile3)); // match
        assertEquals(true, gameController.placeFollower(tile3, 0, 0));

        List<Player> winners = gameController.endGame();
        for (Player player : winners) {
            System.out.println("Winner is " + player.getName());
        }
        for (Player player : gameController.getPlayers()) {
            if (player.getPlayerId() == 0) {
                assertEquals(7, player.getScore());
            } else {
                assertEquals(4, player.getScore());
            }
            System.out.println(player.getName() + " got " + player.getScore() + " points.");
        }
    }
}
