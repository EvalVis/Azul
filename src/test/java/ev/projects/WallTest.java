package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WallTest {

    @Test
    void tileGetsPlacedOnTheWallIfPatternLineIsFilled() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, wall);
        patternLine.add(Tile.RED, 5, 0);
        Game game = new Game(List.of(new Player(new Board(patternLine, wall, floor))));

        game.executeWallTilingPhase();

        assertTrue(wall.alreadyHas(Tile.RED));
        assertEquals(0, patternLine.tileCount());
    }

    @Test
    void tileDoesNotGetPlacedOnTheWallIfPatternLineIsNotFilled() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, wall);
        patternLine.add(Tile.RED, 4, 0);
        Game game = new Game(List.of(new Player(new Board(patternLine, wall, floor))));

        game.executeWallTilingPhase();

        assertFalse(wall.alreadyHas(Tile.RED));
        assertEquals(4, patternLine.tileCount());
    }

    @Test
    void cantAddColourToPatternLineIfWallHasThatColour() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, wall);
        Player player = new Player(new Board(patternLine, wall, floor));
        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), Tile.RED, Tile.BLUE, Tile.BLUE, Tile.BLUE), Tile.RED
        );
        wall.add(Tile.RED, 0);

        assertThrows(ActionNotAllowedException.class, () -> player.addToPatternLine(1));
    }

    @Test
    void playerScoresAPointWhenPlacingATileOnEmptyWall() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(1, wall);
        patternLine.add(Tile.RED, 1, 0);
        Player player = new Player(new Board(patternLine, wall, floor));

        player.moveTileToWall();

        assertEquals(1, player.score());
    }

    @Test
    void playerScoresPointsWhenPlacingATileOnWall() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(3, wall);
        patternLine.add(Tile.YELLOW, 3, 0);
        Player player = new Player(new Board(patternLine, wall, floor));
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
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(3, wall);
        patternLine.add(Tile.YELLOW, 3, 0);
        Player player = new Player(new Board(patternLine, wall, floor));
        wall.add(Tile.BLACK, 2);
        wall.add(Tile.WHITE, 2);
        wall.add(Tile.BLACK, 0);
        int currentScore = player.score();

        player.moveTileToWall(2);

        assertEquals(1, player.score() - currentScore);
    }
}
