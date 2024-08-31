package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FloorTest {

    @Test
    void playerCanAddTilesToFloorLine() {
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5);
        patternLine.add(Tile.RED, 2);
        Player player = new Player(new Board(patternLine, new Wall(), floor));
        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW), Tile.RED
        );

        player.addToFloor(2);

        assertEquals(-2, floor.score());
    }

    @Test
    void playerCantOverfillFloor() {
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5);
        patternLine.add(Tile.RED, 2);
        Player player = new Player(new Board(patternLine, new Wall(), floor));
        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.RED), Tile.RED);
        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.BLUE, Tile.RED, Tile.RED, Tile.RED), Tile.RED);
        player.addToFloor(7);

        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.BLUE, Tile.RED, Tile.RED, Tile.RED), Tile.BLUE);
        player.addToFloor(1);

        assertEquals(-14, floor.score());
    }

    @Test
    void playerScoresPenaltyForFloorTiles() {
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5);
        patternLine.add(Tile.RED, 2);
        Player player = new Player(new Board(patternLine, new Wall(), floor));
        player.addScore(5);
        floor.add(3);
        Game game = new Game(List.of(player));

        game.executeWallTilingPhase();

        assertEquals(1, player.score());
    }

    @Test
    void floorPenaltyCantMakePlayerScoreNegative() {
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5);
        patternLine.add(Tile.RED, 2);
        Player player = new Player(new Board(patternLine, new Wall(), floor));
        player.addScore(3);
        floor.add(3);
        Game game = new Game(List.of(player));

        game.executeWallTilingPhase();

        assertEquals(0, player.score());
    }
}
