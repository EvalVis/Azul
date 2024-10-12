package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CenterTest {

    @Test
    void playerTakingFromCenterFirstWithEmptyFloorGetsPenalty() {
        Center center = new Center();
        Floor floor = new Floor();
        Player player = new PlayerMother().newPlayer(floor);
        Game game = new Game(List.of(player, new PlayerMother().newPlayer()), center, 1);
        game.start();
        game.executeFactoryOfferPhaseWithFactory(0, game.factoryDisplays()[0].tiles()[0], 0, 4);

        player.takeTilesFromCenter(center, game.peekCenter().get(0), 0, 4);

        assertEquals(-1, floor.score());
    }

    @Test
    void playerTakingFromCenterFirstWithNonEmptyFloorGetsPenalty() {
        Center center = new Center();
        Floor floor = new Floor();
        Player player = new PlayerMother().newPlayer(floor);
        Game game = new Game(List.of(player, new PlayerMother().newPlayer()), center, 1);
        game.start();
        game.executeFactoryOfferPhaseWithFactory(0, game.factoryDisplays()[0].tiles()[0], 0, 4);

        player.takeTilesFromCenter(center, game.peekCenter().get(0), 1, 4);

        assertEquals(-2, floor.score());
    }

    @Test
    void playerTakingFromCenterSecondDoesNotGetPenalty() {
        Center center = new Center();
        Floor floor1 = new Floor();
        Player player1 = new PlayerMother().newPlayer(floor1);
        Player player2 = new PlayerMother().newPlayer();
        Game game = new Game(List.of(player1, player2), center, 0);
        game.start();
        game.changeFactoryDisplay(0, Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW);
        game.executeFactoryOfferPhaseWithFactory(0, Tile.RED, 0, 4);
        player2.takeTilesFromCenter(center, Tile.BLUE, 0, 4);

        player2.takeTilesFromCenter(center, Tile.YELLOW, 0, 3);

        assertEquals(0, floor1.score());
    }
}
