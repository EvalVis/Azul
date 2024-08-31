package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PatternLineTest {
    @Test
    void playerFillsPatternLine() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, floor, wall);
        patternLine.add(Tile.RED, 2);
        Player player = new Player(new Board(patternLine, wall), floor);
        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.YELLOW), Tile.RED
        );

        player.addToPatternLine(patternLine, 3);

        assertTrue(patternLine.isFilled());
    }

    @Test
    void playerAddsToPatternLine() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, floor, wall);
        patternLine.add(Tile.RED, 2);
        Player player = new Player(new Board(patternLine, wall), floor);
        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.YELLOW), Tile.RED
        );

        player.addToPatternLine(patternLine, 2);

        assertFalse(patternLine.isFilled());
    }

    @Test
    void playerOverfillsPatternLine() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(3, floor, wall);
        patternLine.add(Tile.RED, 2);
        Player player = new Player(new Board(patternLine, wall), floor);
        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.RED), Tile.RED);

        player.addToPatternLine(patternLine, 4);

        assertTrue(patternLine.isFilled());
        assertEquals(-4, floor.score());
    }

    @Test
    void cantAddTileOfDifferentColour() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, floor, wall);
        patternLine.add(Tile.RED, 2);
        Player player = new Player(new Board(patternLine, wall), floor);

        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.BLUE, Tile.BLUE, Tile.RED, Tile.RED), Tile.BLUE);

        assertThrows(ActionNotAllowedException.class, () -> player.addToPatternLine(patternLine, 2));
    }
}
