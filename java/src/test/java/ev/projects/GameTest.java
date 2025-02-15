package ev.projects;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        game.changeFactoryDisplay(0, List.of(Tile.RED, Tile.RED, Tile.RED, Tile.RED));
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
        game.changeFactoryDisplay(0, List.of(Tile.RED, Tile.RED, Tile.YELLOW, Tile.BLACK));
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

        game.executeFactoryOfferPhaseWithFactory(0, game.factoryDisplays()[0].tiles().get(0), 0, 0);
        assertTrue(patternLines1[0].tileCount() > 0);
        game.executeFactoryOfferPhaseWithFactory(1, game.factoryDisplays()[1].tiles().get(0), 0, 0);
        assertTrue(patternLines2[0].tileCount() > 0);
        game.executeFactoryOfferPhaseWithFactory(2, game.factoryDisplays()[2].tiles().get(0), 0, 1);
        assertTrue(patternLines1[1].tileCount() > 0);
        game.executeFactoryOfferPhaseWithFactory(3, game.factoryDisplays()[3].tiles().get(0), 0, 1);
        assertTrue(patternLines2[1].tileCount() > 0);
    }

    @Test
    void gameIsDisplayed() {
        Lid lid = new Lid();
        Player player1 = new PlayerMother().newPlayer(new Wall(), new Floor(lid), "Robert");
        Wall wall2 = new Wall();
        Floor floor2 = new Floor(lid);
        Player player2 = new PlayerMother().newPlayer(wall2, floor2, "Roger");
        Center center = new Center();
        Game game = new Game(List.of(player1, player2), center, 0, lid);
        game.changeFactoryDisplay(0, List.of(Tile.RED, Tile.RED, Tile.RED, Tile.BLUE));
        game.changeFactoryDisplay(1, List.of(Tile.RED, Tile.RED, Tile.YELLOW, Tile.BLUE));
        game.changeFactoryDisplay(2, List.of(Tile.YELLOW, Tile.YELLOW, Tile.YELLOW, Tile.YELLOW));
        game.changeFactoryDisplay(3, List.of(Tile.WHITE, Tile.WHITE, Tile.WHITE, Tile.WHITE));
        game.changeFactoryDisplay(4, List.of(Tile.BLACK, Tile.WHITE, Tile.WHITE, Tile.YELLOW));
        game.setBag(new Bag(initTilesInBag(new int[] {18, 14, 15, 19, 14})));
        game.executeFactoryOfferPhaseWithFactory(4, Tile.WHITE, 1, 2);
        floor2.add(Tile.RED, 1);
        floor2.add(Tile.YELLOW, 2);
        player2.takeTilesFromCenter(center, Tile.YELLOW, 0, 1);
        wall2.add(Tile.BLUE, 2);
        game.executeWallTilingPhase();

        System.out.println(new GameController(game).showJson());

        Map<String, Object> jsonObject = new GameController(game).showJson();
        assertEquals("{K=1}", jsonObject.get("Center").toString());
        assertEquals("{R=1, W=1, Y=2}", jsonObject.get("Lid").toString());
        assertTrue(jsonObject.get("Factory displays").toString().contains("{B=1, R=3}"));
        assertTrue(jsonObject.get("Factory displays").toString().contains("{B=1, R=2, Y=1}"));
        assertTrue(jsonObject.get("Factory displays").toString().contains("{Y=4}"));
        assertTrue(jsonObject.get("Factory displays").toString().contains("{W=4}"));
        assertTrue(jsonObject.get("Players").toString().contains("Pattern lines=[[], [], [W], [], []]"));
        assertTrue(
                jsonObject
                        .get("Players")
                        .toString()
                        .contains("Wall=[[b, y, r, k, w], [w, b, y, r, k], [k, w, b, y, r], [r, k, w, b, y], [y, r, k, w, b]]")
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
