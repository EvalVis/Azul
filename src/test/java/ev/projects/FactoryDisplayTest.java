package ev.projects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FactoryDisplayTest {

    @Test
    void leftOverTilesArePushedToCenter() {
        Center center = new Center();
        FactoryDisplay display = new FactoryDisplay(center, Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW);
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, wall);
        Player player = new Player(new Board(patternLine, wall, floor));

        player.takeTilesFromFactory(display, Tile.RED);

        assertEquals(0L, center.count(Tile.RED));
        assertEquals(1L, center.count(Tile.BLUE));
        assertEquals(1L, center.count(Tile.YELLOW));
    }
}
