package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FloorTest {

    @Test
    void playerCanAddTilesToFloorLine() {
        Floor floor = new Floor();
        Player player = new PlayerMother().newPlayer(floor);
        Game game = new GameMother().new2PlayerGame(player);
        game.start();
        game.changeFactoryDisplay(0, Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW);

        game.giveTilesFromFactory(0, Tile.RED);

        player.addToFloor(List.of(Tile.RED, Tile.RED));

        assertEquals(-2, floor.score());
    }

    @Test
    void playerScoresPenaltyForFloorTiles() {
        Floor floor = new Floor();
        floor.add(List.of(Tile.RED, Tile.RED, Tile.RED));
        Player player = new PlayerMother().newPlayer(floor);
        player.addScore(5);
        Game game = new Game(List.of(player));

        game.executeWallTilingPhase();

        assertEquals(1, player.score());
    }

    @Test
    void floorPenaltyCantMakePlayerScoreNegative() {
        Floor floor = new Floor();
        floor.add(List.of(Tile.RED, Tile.RED, Tile.RED));
        Player player = new PlayerMother().newPlayer(floor);
        player.addScore(3);
        Game game = new GameMother().new2PlayerGame(player);

        game.executeWallTilingPhase();

        assertEquals(0, player.score());
    }
}
