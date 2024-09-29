package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static ev.projects.PatternLineTest.patternLines;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {

    @Test
    void playerCanAddTilesToPatternLineAndFloor() {
        Floor floor = new Floor();
        PatternLine[] patternLines = patternLines();
        Player player = new Player(new Board(patternLines, new Wall(), floor));
        Game game = new Game(
                List.of(player, new Player(new Board(patternLines()))), new Center(), 0
        );
        game.start();
        game.changeFactoryDisplay(0, Tile.RED, Tile.RED, Tile.RED, Tile.BLUE);
        game.giveTilesFromFactory(0, Tile.RED);

        player.addTileToPatternLine(1, 2);
        player.addToFloor(2);

        assertEquals(1, patternLines[2].tileCount());
        assertEquals(-2, floor.score());
    }
}
