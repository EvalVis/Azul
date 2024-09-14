package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static ev.projects.PatternLineTest.patternLines;
import static org.junit.jupiter.api.Assertions.*;

public class WallTest {

    @Test
    void tileGetsPlacedOnTheWallIfPatternLineIsFilled() {
        Wall wall = new Wall();
        PatternLine[] patternLines = patternLines();
        patternLines[4].add(Tile.RED, 5);
        Game game = new Game(List.of(new Player(new Board(patternLines, wall, new Floor()))));

        game.executeWallTilingPhase();

        assertTrue(wall.alreadyHas(Tile.RED));
        assertEquals(0, patternLines[4].tileCount());
    }

    @Test
    void tileDoesNotGetPlacedOnTheWallIfPatternLineIsNotFilled() {
        Wall wall = new Wall();
        PatternLine[] patternLines = patternLines();
        patternLines[4].add(Tile.RED, 4);
        Game game = new Game(List.of(new Player(new Board(patternLines, wall, new Floor()))));

        game.executeWallTilingPhase();

        assertFalse(wall.alreadyHas(Tile.RED));
        assertEquals(4, patternLines[4].tileCount());
    }

    @Test
    void cantAddColourToPatternLineIfWallHasThatColour() {
        Wall wall = new Wall();
        Player player = new PlayerMother().newPlayer(wall);
        player.takeTiles(List.of(Tile.RED));
        wall.add(Tile.RED, 4);

        assertThrows(ActionNotAllowedException.class, () -> player.addTileToPatternLine(1, 4));
    }

    @Test
    void playerScoresAPointWhenPlacingATileOnEmptyWall() {
        Wall wall = new Wall();
        PatternLine[] patternLines = patternLines();
        patternLines[0].add(Tile.RED, 1);
        Player player = new Player(new Board(patternLines, wall, new Floor()));

        player.moveTileToWall();

        assertEquals(1, player.score());
    }

    @Test
    void playerScoresPointsWhenPlacingATileOnWall() {
        Wall wall = new Wall();
        PatternLine[] patternLines = patternLines();
        patternLines[2].add(Tile.YELLOW, 3);
        Player player = new Player(new Board(patternLines, wall, new Floor()));
        wall.add(Tile.BLACK, 2);
        wall.add(Tile.WHITE, 2);
        wall.add(Tile.BLUE, 2);
        wall.add(Tile.BLACK, 0);
        wall.add(Tile.RED, 1);
        int currentScore = player.score();

        player.moveTileToWall(2);

        assertEquals(7, player.score() - currentScore);
    }

    @Test
    void playerScoresAPointWhenPlacingATileOnWall() {
        Wall wall = new Wall();
        PatternLine[] patternLines = patternLines();
        patternLines[2].add(Tile.YELLOW, 3);
        Player player = new Player(new Board(patternLines, wall, new Floor()));
        wall.add(Tile.BLACK, 2);
        wall.add(Tile.WHITE, 2);
        wall.add(Tile.BLACK, 0);
        int currentScore = player.score();

        player.moveTileToWall(2);

        assertEquals(1, player.score() - currentScore);
    }
}
