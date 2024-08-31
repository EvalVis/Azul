package ev.projects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CenterTest {

    @Test
    void playerTakingFromCenterFirstWithEmptyFloorGetsPenalty() {
        Center center = new Center();
        FactoryDisplay display = new FactoryDisplay(center, Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW);
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, wall);
        patternLine.add(Tile.RED, 2, 0);
        Player player = new Player(new Board(patternLine, wall, floor));
        player.takeTilesFromFactory(display, Tile.RED);

        player.takeTilesFromCenter(center, Tile.BLUE);

        assertEquals(-1, floor.score());
    }

    @Test
    void playerTakingFromCenterFirstWithNonEmptyFloorGetsPenalty() {
        Center center = new Center();
        FactoryDisplay display = new FactoryDisplay(center, Tile.RED, Tile.RED, Tile.RED, Tile.BLUE);
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, wall);
        patternLine.add(Tile.RED, 2, 0);
        Player player = new Player(new Board(patternLine, wall, floor));
        player.takeTilesFromFactory(display, Tile.RED);
        player.addToFloor(3);

        player.takeTilesFromCenter(center, Tile.BLUE);

        assertEquals(-6, floor.score());
    }

    @Test
    void playerTakingFromCenterSecondDoesNotGetPenalty() {
        Center center = new Center();
        FactoryDisplay display = new FactoryDisplay(center, Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW);
        Wall wall1 = new Wall();
        Floor floor1 = new Floor();
        PatternLine patternLine1 = new PatternLine(5, wall1);
        Player player1 = new Player(new Board(patternLine1, wall1, floor1));
        player1.takeTilesFromFactory(display, Tile.RED);
        player1.takeTilesFromCenter(center, Tile.BLUE);
        Wall wall2 = new Wall();
        Floor floor2 = new Floor();
        PatternLine patternLine2 = new PatternLine(5, wall2);
        Player player2 = new Player(new Board(patternLine2, wall2, floor2));

        player2.takeTilesFromCenter(center, Tile.YELLOW);

        assertEquals(0, floor2.score());
    }
}
