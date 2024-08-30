package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WallTest {

    @Test
    void tileGetsPlacedOnTheWallIfPatternLineIsFilled() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, floor, wall);
        patternLine.add(List.of(Tile.RED, Tile.RED, Tile.RED, Tile.RED, Tile.RED));
        Game game = new Game(List.of(new Board(new Player(floor), patternLine, wall)));

        game.executeWallTilingPhase();

        assertTrue(wall.alreadyHas(Tile.RED));
        assertEquals(0, patternLine.tileCount());
    }

    @Test
    void tileDoesNotGetPlacedOnTheWallIfPatternLineIsNotFilled() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, floor, wall);
        patternLine.add(List.of(Tile.RED, Tile.RED, Tile.RED, Tile.RED));
        Game game = new Game(List.of(new Board(new Player(floor), patternLine, wall)));

        game.executeWallTilingPhase();

        assertFalse(wall.alreadyHas(Tile.RED));
        assertEquals(4, patternLine.tileCount());
    }

    @Test
    void cantAddColourToPatternLineIfWallHasThatColour() {
        Wall wall = new Wall();
        PatternLine patternLine = new PatternLine(5, new Floor(), wall);
        Player player = new Player(new Floor());
        player.takeTilesFromFactory(
                new FactoryDisplay(new Center(), Tile.RED, Tile.BLUE, Tile.BLUE, Tile.BLUE), Tile.RED
        );
        wall.add(Tile.RED, 0);

        assertThrows(ActionNotAllowedException.class, () -> player.addToPatternLine(patternLine, 1));
    }

    @Test
    void playerScoresAPointWhenPlacingATileOnEmptyWall() {
        Player player = new Player(new Floor());
        Wall wall = new Wall(player);

        wall.add(Tile.RED, 0);

        assertEquals(1, player.score());
    }

    @Test
    void playerScoresPointsWhenPlacingATileOnWall() {
        Player player = new Player(new Floor());
        Wall wall = new Wall(player);
        wall.add(Tile.BLACK, 2);
        wall.add(Tile.WHITE, 2);
        wall.add(Tile.BLUE, 2);
        wall.add(Tile.BLACK, 0);
        wall.add(Tile.RED, 1);
        int currentScore = player.score();

        wall.add(Tile.YELLOW, 2);

        assertEquals(7, player.score() - currentScore);
    }

    @Test
    void playerScoresAPointWhenPlacingATileOnWall() {
        Player player = new Player(new Floor());
        Wall wall = new Wall(player);
        wall.add(Tile.BLACK, 2);
        wall.add(Tile.WHITE, 2);
        wall.add(Tile.BLACK, 0);
        int currentScore = player.score();

        wall.add(Tile.YELLOW, 2);

        assertEquals(1, player.score() - currentScore);
    }
}
