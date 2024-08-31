package ev.projects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PatternLineTest {
    @Test
    void playerFillsPatternLine() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, wall);
        patternLine.add(Tile.RED, 2, 0);
        Player player = new Player(new Board(patternLine, wall, floor));
        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.YELLOW), Tile.RED
        );

        player.addToPatternLine(3);

        assertTrue(patternLine.isFilled());
    }

    @Test
    void playerAddsToPatternLine() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, wall);
        patternLine.add(Tile.RED, 2, 0);
        Player player = new Player(new Board(patternLine, wall, floor));
        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.YELLOW), Tile.RED
        );

        player.addToPatternLine(2);

        assertFalse(patternLine.isFilled());
    }

    @Test
    void playerOverfillsPatternLine() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(3, wall);
        Board board = new Board(patternLine, wall, floor);
        board.addTileToPatternLine(Tile.RED, 2, 0);
        Player player = new Player(board);
        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.RED), Tile.RED);

        player.addToPatternLine(4);

        assertTrue(patternLine.isFilled());
        assertEquals(-4, floor.score());
    }

    @Test
    void cantAddTileOfDifferentColour() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, wall);
        patternLine.add(Tile.RED, 2, 0);
        Player player = new Player(new Board(patternLine, wall, floor));

        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.BLUE, Tile.BLUE, Tile.RED, Tile.RED), Tile.BLUE);

        assertThrows(ActionNotAllowedException.class, () -> player.addToPatternLine(2));
    }
}
