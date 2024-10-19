package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FloorTest {

    @Test
    void playerCanAddTilesToFloorLine() {
        Floor floor = new Floor();
        Player player = new PlayerMother().newPlayer(floor);
        Game game = new Game(List.of(player, new PlayerMother().newPlayer()), new Center(), 0);
        game.changeFactoryDisplay(0, List.of(Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW));

        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), List.of(Tile.RED, Tile.RED, Tile.BLUE, Tile.BLUE)), Tile.RED, 2, 4
        );

        assertEquals(-2, floor.score());
    }

    @Test
    void floorPenaltyCantMakePlayerScoreNegative() {
        Floor floor = new Floor();
        floor.add(Tile.RED, 3);
        Player player = new PlayerMother().newPlayer(floor);
        player.addScore(3);
        Game game = new GameMother().new2PlayerGame(player);

        game.executeWallTilingPhase();

        assertEquals(0, player.score());
    }

    @Test
    void overflownFloorTilesGoToLid() {
        Lid lid = new Lid();
        Floor floor = new Floor(lid);
        floor.add(Tile.RED, 8);
        floor.add(Tile.BLUE, 1);

        assertArrayEquals(new Tile[]{Tile.RED, Tile.BLUE}, lid.tiles().toArray());
    }
}
