package ev.projects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PatternLineTest {
    @Test
    void playerFillsPatternLine() {
        PatternLine patternLine = new PatternLine(5);
        patternLine.add(Tile.RED, 2);
        Player player = new Player(new Board(patternLine, new Wall(), new Floor()));
        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.YELLOW), Tile.RED
        );

        player.addToPatternLine(3);

        assertTrue(patternLine.isFilled());
    }

    @Test
    void playerAddsToPatternLine() {
        PatternLine patternLine = new PatternLine(5);
        patternLine.add(Tile.RED, 2);
        Player player = new Player(new Board(patternLine, new Wall(), new Floor()));
        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.YELLOW), Tile.RED
        );

        player.addToPatternLine(2);

        assertFalse(patternLine.isFilled());
    }

    @Test
    void onFilledPatternLineTileIsPlacedInCorrectWallPosition() {
        Wall wall = new Wall();
        PatternLine patternLine = new PatternLine(3);
        Board board = new Board(patternLine, wall, new Floor());

        patternLine.add(Tile.WHITE, 3);
        board.moveTileToWall(2);

        assertTrue(wall.alreadyHas(Tile.WHITE, 2));
        assertFalse(wall.alreadyHas(Tile.WHITE, 0));
        assertFalse(wall.alreadyHas(Tile.WHITE, 1));
        assertFalse(wall.alreadyHas(Tile.WHITE, 3));
        assertFalse(wall.alreadyHas(Tile.WHITE, 4));
        assertFalse(wall.alreadyHas(Tile.BLUE, 0));
        assertFalse(wall.alreadyHas(Tile.BLUE, 1));
        assertFalse(wall.alreadyHas(Tile.BLUE, 2));
        assertFalse(wall.alreadyHas(Tile.BLUE, 3));
        assertFalse(wall.alreadyHas(Tile.BLUE, 4));
        assertFalse(wall.alreadyHas(Tile.YELLOW, 0));
        assertFalse(wall.alreadyHas(Tile.YELLOW, 1));
        assertFalse(wall.alreadyHas(Tile.YELLOW, 2));
        assertFalse(wall.alreadyHas(Tile.YELLOW, 3));
        assertFalse(wall.alreadyHas(Tile.YELLOW, 4));
        assertFalse(wall.alreadyHas(Tile.RED, 0));
        assertFalse(wall.alreadyHas(Tile.RED, 1));
        assertFalse(wall.alreadyHas(Tile.RED, 2));
        assertFalse(wall.alreadyHas(Tile.RED, 3));
        assertFalse(wall.alreadyHas(Tile.RED, 4));
        assertFalse(wall.alreadyHas(Tile.BLACK, 0));
        assertFalse(wall.alreadyHas(Tile.BLACK, 1));
        assertFalse(wall.alreadyHas(Tile.BLACK, 2));
        assertFalse(wall.alreadyHas(Tile.BLACK, 3));
        assertFalse(wall.alreadyHas(Tile.BLACK, 4));
    }

    @Test
    void playerOverfillsPatternLine() {
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(3);
        Board board = new Board(patternLine, new Wall(), floor);
        board.addTileToPatternLine(Tile.RED, 2);
        Player player = new Player(board);
        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.RED), Tile.RED);

        player.addToPatternLine(4);

        assertTrue(patternLine.isFilled());
        assertEquals(-4, floor.score());
    }

    @Test
    void cantAddTileOfDifferentColour() {
        Wall wall = new Wall();
        PatternLine patternLine = new PatternLine(5);
        patternLine.add(Tile.RED, 2);
        Player player = new Player(new Board(patternLine, wall, new Floor()));

        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.BLUE, Tile.BLUE, Tile.RED, Tile.RED), Tile.BLUE);

        assertThrows(ActionNotAllowedException.class, () -> player.addToPatternLine(2));
    }
}
