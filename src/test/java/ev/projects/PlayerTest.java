package ev.projects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {

    @Test
    void playerCanAddTilesToPatternLineAndFloor() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, floor, wall);
        Player player = new Player(new Board(patternLine, wall), floor);
        player.takeTilesFromFactory(new FactoryDisplay(new Center(), Tile.RED, Tile.RED, Tile.RED, Tile.BLUE), Tile.RED);

        player.addToPatternLine(patternLine, 1);
        player.addToFloor(floor, 2);

        assertEquals(1, patternLine.tileCount());
        assertEquals(-2, floor.score());
    }
}
