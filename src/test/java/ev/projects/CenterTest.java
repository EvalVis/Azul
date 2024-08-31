package ev.projects;

import org.junit.jupiter.api.Test;

import static ev.projects.PatternLineTest.patternLines;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CenterTest {

    @Test
    void playerTakingFromCenterFirstWithEmptyFloorGetsPenalty() {
        Center center = new Center();
        FactoryDisplay display = new FactoryDisplay(center, Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW);
        Floor floor = new Floor();
        PatternLine[] patternLines = patternLines();
        patternLines[4].add(Tile.RED, 2);
        Player player = new Player(new Board(patternLines, new Wall(), floor));
        player.takeTilesFromFactory(display, Tile.RED);

        player.takeTilesFromCenter(center, Tile.BLUE);

        assertEquals(-1, floor.score());
    }

    @Test
    void playerTakingFromCenterFirstWithNonEmptyFloorGetsPenalty() {
        Center center = new Center();
        FactoryDisplay display = new FactoryDisplay(center, Tile.RED, Tile.RED, Tile.RED, Tile.BLUE);
        Floor floor = new Floor();
        Player player = new Player(new Board(new Wall(), floor));
        player.takeTilesFromFactory(display, Tile.RED);
        player.addToFloor(3);

        player.takeTilesFromCenter(center, Tile.BLUE);

        assertEquals(-6, floor.score());
    }

    @Test
    void playerTakingFromCenterSecondDoesNotGetPenalty() {
        Center center = new Center();
        FactoryDisplay display = new FactoryDisplay(center, Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW);
        Player player1 = new Player(new Board(new Wall(), new Floor()));
        player1.takeTilesFromFactory(display, Tile.RED);
        player1.takeTilesFromCenter(center, Tile.BLUE);
        Floor floor2 = new Floor();
        Player player2 = new Player(new Board(new Wall(), floor2));

        player2.takeTilesFromCenter(center, Tile.YELLOW);

        assertEquals(0, floor2.score());
    }
}
