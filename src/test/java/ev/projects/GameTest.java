package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static ev.projects.PatternLineTest.patternLines;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {

    @Test
    void playerTakesAndPlacesFactoryTiles() {
        PatternLine[] patternLines = patternLines();
        Floor floor = new Floor();
        Player player = new Player(new Board(patternLines, new Wall(), floor));
        Game game = new Game(List.of(player, new PlayerMother().newPlayer()), new Center(), 0);
        game.start();
        game.changeFactoryDisplay(0, Tile.RED, Tile.RED, Tile.RED, Tile.RED);
        PlayerController playerController = new PlayerController(game);

        playerController.takeTilesFromFactory(new FactoryTakingRequest(0, Tile.RED, 2, 1));

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
        game.start();
        game.changeFactoryDisplay(0, Tile.RED, Tile.RED, Tile.YELLOW, Tile.BLACK);
        game.giveTilesFromFactory(0, Tile.YELLOW);
        PlayerController playerController = new PlayerController(game);

        playerController.takeTilesFromCenter(new CenterTakingRequest(Tile.RED, 1, 0));

        assertEquals(-2, floor.score());
        assertEquals("M R", floor.toString());
        assertEquals(1, patternLines[0].tileCount());
        assertEquals(Tile.RED, patternLines[0].tile());
        assertEquals("K", center.toString());
    }

    @Test
    void playersTakeTilesInOrder() {
        Center center = new Center();
        Player player1 = new PlayerMother().newPlayer();
        Player player2 = new PlayerMother().newPlayer();
        Game game = new Game(List.of(player1, player2), center);
        game.start();

        game.giveTilesFromFactory(0, game.factoryDisplays()[0].tiles()[0]);
        int player1TileCount = player1.tileCount();
        game.giveTilesFromFactory(1, game.factoryDisplays()[1].tiles()[0]);
        int player2TileCount = player2.tileCount();
        game.giveTilesFromFactory(2, game.factoryDisplays()[2].tiles()[0]);
        assertTrue(player1TileCount < player1.tileCount());
        game.giveTilesFromFactory(3, game.factoryDisplays()[3].tiles()[0]);
        assertTrue(player2TileCount < player2.tileCount());
    }

    @Test
    void gameIsDisplayed() {
        Player player1 = new PlayerMother().newPlayer("Robert");
        Wall wall2 = new Wall();
        Floor floor2 = new Floor();
        Player player2 = new PlayerMother().newPlayer(wall2, floor2, "Roger");
        Center center = new Center();
        Game game = new Game(List.of(player1, player2), center, 0);
        game.start();
        game.changeFactoryDisplay(0, Tile.RED, Tile.RED, Tile.RED, Tile.BLUE);
        game.changeFactoryDisplay(1, Tile.RED, Tile.RED, Tile.YELLOW, Tile.BLUE);
        game.changeFactoryDisplay(2, Tile.YELLOW, Tile.YELLOW, Tile.YELLOW, Tile.YELLOW);
        game.changeFactoryDisplay(3, Tile.WHITE, Tile.WHITE, Tile.WHITE, Tile.WHITE);
        game.changeFactoryDisplay(4, Tile.BLACK, Tile.WHITE, Tile.WHITE, Tile.YELLOW);
        game.setBag(new Bag(initTilesInBag(new int[] {18, 14, 15, 19, 14})));
        game.giveTilesFromFactory(4, Tile.WHITE);
        player1.addTileToPatternLine(1, 2);
        floor2.add(List.of(Tile.RED, Tile.YELLOW, Tile.YELLOW));
        player2.takeTilesFromCenter(center, Tile.YELLOW);
        wall2.add(Tile.BLUE, 2);
        game.executeWallTilingPhase();

        System.out.println(new PlayerController(game).show());
        assertEquals("""
                        Factories: 1) B 3R 2) B Y 2R 3) 4Y 4) 4W 5) Empty
                        Center: K
                        Player Robert:
                        Hand-held tiles: W
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
                        Floor: Empty
                        Player Roger:
                        Hand-held tiles: Y
                        Score: 0
                        Board:
                        Pattern lines:
                        E\s
                        E E\s
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
                        Bag: 18B 14Y 15R 19K 14W""",
                new PlayerController(game).show()
        );
        // TODO: Implement showing of lid tiles.
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
