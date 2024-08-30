package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PatternLineTest {

    @Test
    void patternLinesAreCreated() {
        Board board = new Board();

        assertEquals(5, board.patternLines.length);
        assertEquals(1, board.patternLines[0].size);
        assertEquals(2, board.patternLines[1].size);
        assertEquals(3, board.patternLines[3].size);
        assertEquals(4, board.patternLines[4].size);
        assertEquals(5, board.patternLines[5].size);
    }

    @Test
    void playerFillsPatternLine() {
        PatternLine patternLine = new PatternLine(5, new Floor(), new Wall());
        patternLine.add(List.of(Tile.RED, Tile.RED));
        Player player = new Player(new Floor());
        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.YELLOW), Tile.RED
        );

        player.addToPatternLine(patternLine, 3);

        assertTrue(patternLine.isFilled());
    }

    @Test
    void playerAddsToPatternLine() {
        PatternLine patternLine = new PatternLine(5, new Floor(), new Wall());
        patternLine.add(List.of(Tile.RED, Tile.RED));
        Player player = new Player(new Floor());
        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.YELLOW), Tile.RED
        );

        player.addToPatternLine(patternLine, 2);

        assertFalse(patternLine.isFilled());
    }

    @Test
    void playerOverfillsPatternLine() {
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(3, floor, new Wall());
        patternLine.add(List.of(Tile.RED, Tile.RED));
        Player player = new Player(floor);
        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.RED), Tile.RED);

        player.addToPatternLine(patternLine, 4);

        assertTrue(patternLine.isFilled());
        assertEquals(-4, floor.score());
    }

    @Test
    void cantAddTileOfDifferentColour() {
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, floor, new Wall());
        patternLine.add(List.of(Tile.RED, Tile.RED));

        Player player = new Player(floor);
        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.BLUE, Tile.BLUE, Tile.RED, Tile.RED), Tile.BLUE);

        assertThrows(ActionNotAllowedException.class, () -> player.addToPatternLine(patternLine, 2));
    }
}
