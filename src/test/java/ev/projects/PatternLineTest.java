package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PatternLineTest {
    @Test
    void playerFillsPatternLine() {
        PatternLine[] patternLines = patternLines();
        patternLines[4].add(Tile.RED, 2);
        Player player = new Player(new Board(patternLines));

        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), List.of(Tile.RED, Tile.RED, Tile.RED, Tile.BLUE)), Tile.RED, 0, 4
        );

        assertTrue(patternLines[4].isFilled());
    }

    @Test
    void playerAddsToPatternLine() {
        PatternLine[] patternLines = patternLines();
        patternLines[4].add(Tile.RED, 2);
        Player player = new Player(new Board(patternLines));

        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), List.of(Tile.RED, Tile.RED, Tile.BLUE, Tile.BLUE)), Tile.RED, 0, 4
        );

        assertFalse(patternLines[4].isFilled());
    }

    @Test
    void onFilledPatternLineTileIsPlacedInCorrectWallPosition() {
        Wall wall = new Wall();
        PatternLine[] patternLines = patternLines();
        Board board = new Board(patternLines, wall, new Floor());

        patternLines[2].add(Tile.WHITE, 3);
        board.moveTilesFromPatternLinesToWall();

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
        PatternLine[] patternLines = patternLines();
        Board board = new Board(patternLines, new Wall(), floor);
        board.addTileToPatternLine(Tile.RED, 2, 2);
        Player player = new Player(board);

        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), List.of(Tile.RED, Tile.RED, Tile.RED, Tile.RED)), Tile.RED, 0, 2
        );

        assertTrue(patternLines[2].isFilled());
        assertEquals(-4, floor.score());
    }

    @Test
    void cantAddTileOfDifferentColour() {
        Wall wall = new Wall();
        PatternLine[] patternLines = patternLines();
        patternLines[4].add(Tile.RED, 2);
        Player player = new Player(new Board(patternLines, wall, new Floor()));

        assertThrows(
                ActionNotAllowedException.class,
                () -> player.takeTilesFromFactory(
                        new FactoryDisplay(new Center(), List.of(Tile.RED, Tile.RED, Tile.BLUE, Tile.BLUE)), Tile.BLUE, 0, 4
                )
        );
    }

    public static PatternLine[] patternLines() {
        return new PatternLine[] {
                new PatternLine(1), new PatternLine(2), new PatternLine(3), new PatternLine(4),
                new PatternLine(5)
        };
    }
}
