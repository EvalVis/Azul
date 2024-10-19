package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static ev.projects.PatternLineTest.patternLines;
import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    void gameEndingExecutesOnFilledHorizontalLine() {
        Center center = new Center();
        center.addTile(Tile.BLUE);
        Player player = new PlayerMother().newPlayer(new WallMother().withAlmostCompletedHorizontalLine());
        Game game = new Game(List.of(player, new PlayerMother().newPlayer()), center, 0);
        game.clearFactoryDisplays();

        game.executeFactoryOfferPhaseWithCenter(Tile.BLUE, 0, 0);

        assertEquals(6, player.score());
    }

    @Test
    void wallTilingExecutesOnEmptyFactoryDisplaysAndCenter() {
        Center center = new Center();
        center.addTile(Tile.BLUE);
        PatternLine[] patternLines = patternLines();
        Player player = new Player(new Board(patternLines));
        Game game = new Game(List.of(player, new PlayerMother().newPlayer()), center, 0);
        game.clearFactoryDisplays();

        game.executeFactoryOfferPhaseWithCenter(Tile.BLUE, 0, 0);

        assertFalse(patternLines[0].isFilled());
    }

    @Test
    void playerTakesAndPlacesFactoryTiles() {
        PatternLine[] patternLines = patternLines();
        Floor floor = new Floor();
        Player player = new Player(new Board(patternLines, new Wall(), floor));
        Game game = new Game(List.of(player, new PlayerMother().newPlayer()), new Center(), 0);
        game.changeFactoryDisplay(0, Tile.RED, Tile.RED, Tile.RED, Tile.RED);
        GameController gameController = new GameController(game);

        gameController.takeTilesFromFactory(new FactoryTakingRequest(0, Tile.RED, 2, 1));

        assertEquals(-2, floor.score());
        assertEquals("R R", floor.toString());
        assertEquals(2, patternLines[1].tileCount());
        assertEquals(Tile.RED, patternLines[1].tile());
    }

    @Test
    void playerTakesAndPlacesCenterTiles() {
        PatternLine[] patternLines = patternLines();
        Floor floor = new Floor();
        Player player = new Player(new Board(patternLines, new Wall(), floor));
        Center center = new Center();
        Game game = new Game(List.of(player, new PlayerMother().newPlayer()), center, 1);
        game.changeFactoryDisplay(0, Tile.RED, Tile.RED, Tile.YELLOW, Tile.BLACK);
        game.executeFactoryOfferPhaseWithFactory(0, Tile.YELLOW, 0, 3);
        GameController gameController = new GameController(game);

        gameController.takeTilesFromCenter(new CenterTakingRequest(Tile.RED, 1, 0));

        assertEquals(-2, floor.score());
        assertEquals("M R", floor.toString());
        assertEquals(1, patternLines[0].tileCount());
        assertEquals(Tile.RED, patternLines[0].tile());
        assertEquals("K", center.toString());
    }

    @Test
    void playersTakeTilesInOrder() {
        Center center = new Center();
        PatternLine[] patternLines1 = patternLines();
        Player player1 = new Player(new Board(patternLines1));
        PatternLine[] patternLines2 = patternLines();
        Player player2 = new Player(new Board(patternLines2));
        Game game = new Game(List.of(player1, player2), center, 0);

        game.executeFactoryOfferPhaseWithFactory(0, game.factoryDisplays()[0].tiles()[0], 0, 0);
        assertTrue(patternLines1[0].tileCount() > 0);
        game.executeFactoryOfferPhaseWithFactory(1, game.factoryDisplays()[1].tiles()[0], 0, 0);
        assertTrue(patternLines2[0].tileCount() > 0);
        game.executeFactoryOfferPhaseWithFactory(2, game.factoryDisplays()[2].tiles()[0], 0, 1);
        assertTrue(patternLines1[1].tileCount() > 0);
        game.executeFactoryOfferPhaseWithFactory(3, game.factoryDisplays()[3].tiles()[0], 0, 1);
        assertTrue(patternLines2[1].tileCount() > 0);
    }

    @Test
    void gameIsDisplayed() {
        Lid lid = new Lid();
        Player player1 = new PlayerMother().newPlayer("Robert");
        Wall wall2 = new Wall();
        Floor floor2 = new Floor(lid);
        Player player2 = new PlayerMother().newPlayer(wall2, floor2, "Roger");
        Center center = new Center();
        Game game = new Game(List.of(player1, player2), center, 0, lid);
        game.changeFactoryDisplay(0, Tile.RED, Tile.RED, Tile.RED, Tile.BLUE);
        game.changeFactoryDisplay(1, Tile.RED, Tile.RED, Tile.YELLOW, Tile.BLUE);
        game.changeFactoryDisplay(2, Tile.YELLOW, Tile.YELLOW, Tile.YELLOW, Tile.YELLOW);
        game.changeFactoryDisplay(3, Tile.WHITE, Tile.WHITE, Tile.WHITE, Tile.WHITE);
        game.changeFactoryDisplay(4, Tile.BLACK, Tile.WHITE, Tile.WHITE, Tile.YELLOW);
        game.setBag(new Bag(initTilesInBag(new int[] {18, 14, 15, 19, 14})));
        game.executeFactoryOfferPhaseWithFactory(4, Tile.WHITE, 1, 2);
        floor2.add(Tile.RED, 1);
        floor2.add(Tile.YELLOW, 2);
        player2.takeTilesFromCenter(center, Tile.YELLOW, 0, 1);
        wall2.add(Tile.BLUE, 2);
        game.executeWallTilingPhase();

        System.out.println(new GameController(game).show());
        assertEquals("""
                        Factories: 1) B 3R 2) B Y 2R 3) 4Y 4) 4W 5) Empty
                        Center: K
                        Player Robert:
                        Score: 0
                        Has the starting player token.
                        Board:
                        Pattern lines:
                        E\s
                        E E\s
                        W E E\s
                        E E E E\s
                        E E E E E\s
                        Wall:
                        b y r k w\s
                        w b y r k\s
                        k w b y r\s
                        r k w b y\s
                        y r k w b\s
                        Floor: W
                        Player Roger:
                        Score: 0
                        Board:
                        Pattern lines:
                        E\s
                        Y E\s
                        E E E\s
                        E E E E\s
                        E E E E E\s
                        Wall:
                        b y r k w\s
                        w b y r k\s
                        k w B y r\s
                        r k w b y\s
                        y r k w b\s
                        Floor: R Y Y M
                        Bag: 18B 14Y 15R 19K 14W
                        Lid: Empty""",
                new GameController(game).show()
        );
    }

    private List<Tile> initTilesInBag(int[] amount) {
        List<Tile> tiles = new ArrayList<>();
        for (int i = 0; i < amount.length; i++) {
            for(int j = 0; j < amount[i]; j++) {
                tiles.add(Tile.values()[i]);
            }
        }
        return tiles;
    }
}
