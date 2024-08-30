package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FloorTest {

    @Test
    void playerCanAddTilesToFloorLine() {
        Player player = new Player(new Floor());
        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW), Tile.RED
        );
        Floor floor = new Floor();

        player.addToFloor(floor, 2);

        assertEquals(-2, floor.score());
    }

    @Test
    void playerCantOverfillFloor() {
        Player player = new Player(new Floor());
        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.RED), Tile.RED);
        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.BLUE, Tile.RED, Tile.RED, Tile.RED), Tile.RED);
        Floor floor = new Floor();
        player.addToFloor(floor, 7);

        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.BLUE, Tile.RED, Tile.RED, Tile.RED), Tile.BLUE);
        player.addToFloor(floor, 1);

        assertEquals(-14, floor.score());
    }

    @Test
    void playerScoresPenaltyForFloorTiles() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        Player player = new Player(floor);
        player.addScore(5);
        floor.add(3);
        Game game = new Game(List.of(new Board(player, new PatternLine(5, floor, wall), wall)));

        game.executeWallTilingPhase();

        assertEquals(1, player.score());
    }

    @Test
    void floorPenaltyCantMakePlayerScoreNegative() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        Player player = new Player(floor);
        player.addScore(3);
        floor.add(3);
        Game game = new Game(List.of(new Board(player, new PatternLine(5, floor, wall), wall)));

        game.executeWallTilingPhase();

        assertEquals(0, player.score());
    }
}
