package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CenterTest {
    @Test
    void playerTakingFromCenterFirstWithEmptyFloorGetsPenalty() {
        Floor floor = new Floor();
        Game game = new Game(List.of(new PlayerMother().newPlayer(floor), new PlayerMother().newPlayer()), new Center(), 1);
        GameController controller = new GameController(game);
        controller.takeTilesFromFactory(new FactoryTakingRequest(0, game.factoryDisplays()[0].tiles().get(0), 0, 4));

        controller.takeTilesFromCenter(new CenterTakingRequest(game.peekCenter().get(0), 0, 4));

        assertEquals(-1, floor.score());
    }

    @Test
    void playerTakingFromCenterFirstWithNonEmptyFloorGetsPenalty() {
        Floor floor = new Floor();
        Game game = new Game(List.of(new PlayerMother().newPlayer(floor), new PlayerMother().newPlayer()), new Center(), 1);
        GameController controller = new GameController(game);
        controller.takeTilesFromFactory(new FactoryTakingRequest(0, game.factoryDisplays()[0].tiles().get(0), 0, 4));

        controller.takeTilesFromCenter(new CenterTakingRequest(game.peekCenter().get(0), 1, 4));

        assertEquals(-2, floor.score());
    }

    @Test
    void playerTakingFromCenterSecondDoesNotGetPenalty() {
        Center center = new Center();
        Floor floor1 = new Floor();
        Player player1 = new PlayerMother().newPlayer(floor1);
        Player player2 = new PlayerMother().newPlayer();
        Game game = new Game(List.of(player1, player2), center, 0);
        game.changeFactoryDisplay(0, List.of(Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW));
        game.executeFactoryOfferPhaseWithFactory(0, Tile.RED, 0, 4);
        player2.takeTilesFromCenter(center, Tile.BLUE, 0, 4);

        player2.takeTilesFromCenter(center, Tile.YELLOW, 0, 3);

        assertEquals(0, floor1.score());
    }
}
