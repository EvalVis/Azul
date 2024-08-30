package ev.projects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {

    @Test
    void playerCanAddTilesToPatternLineAndFloor() {
        Player player = new Player(new Floor());
        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.BLUE), Tile.RED);
        PatternLine patternLine = new PatternLine(5, new Floor(), new Wall());
        Floor floor = new Floor();

        player.addToPatternLine(patternLine, 1);
        player.addToFloor(floor, 2);

        assertEquals(1, patternLine.tileCount());
        assertEquals(-2, floor.score());
    }
}
