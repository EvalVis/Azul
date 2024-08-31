package ev.projects;

import org.junit.jupiter.api.Test;

import static ev.projects.PatternLineTest.patternLines;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {

    @Test
    void playerCanAddTilesToPatternLineAndFloor() {
        Floor floor = new Floor();
        PatternLine[] patternLines = patternLines();
        Player player = new Player(new Board(patternLines, new Wall(), floor));
        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.BLUE), Tile.RED);

        player.addTileToPatternLine(1, 2);
        player.addToFloor(2);

        assertEquals(1, patternLines[2].tileCount());
        assertEquals(-2, floor.score());
    }
}
