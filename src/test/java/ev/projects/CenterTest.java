package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class CenterTest {

    @Test
    void playerTakingFromCenterFirstWithEmptyFloorGetsPenalty() {
        Center center = new Center();
        Floor floor = new Floor();
        Player player = new PlayerMother().newPlayer(floor);
        Game game = new GameMother().new2PlayerGame(player, center);
        game.start();
        game.giveTilesFromFactory(0, game.factoryDisplays()[0].tiles()[0]);

        player.takeTilesFromCenter(center, game.peekCenter().get(0));

        assertEquals(-1, floor.score());
    }

    @Test
    void playerTakingFromCenterFirstWithNonEmptyFloorGetsPenalty() {
        Center center = new Center();
        Floor floor = new Floor();
        Player player = new PlayerMother().newPlayer(floor);
        Game game = new GameMother().new2PlayerGame(player, center);
        game.start();
        game.giveTilesFromFactory(0, game.factoryDisplays()[0].tiles()[0]);
        player.addToFloor(player.tileCount());

        player.takeTilesFromCenter(center, Tile.BLUE);

        assertNotEquals(0, floor.score());
    }

    @Test
    void playerTakingFromCenterSecondDoesNotGetPenalty() {
        Center center = new Center();
        Floor floor1 = new Floor();
        Player player1 = new PlayerMother().newPlayer(floor1);
        Player player2 = new PlayerMother().newPlayer();
        Game game = new Game(List.of(player1, player2), center, 0);
        game.changeFactoryDisplay(0, Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW);
        game.giveTilesFromFactory(0, Tile.RED);
        player2.takeTilesFromCenter(center, Tile.BLUE);

        player1.takeTilesFromCenter(center, Tile.YELLOW);

        assertEquals(0, floor1.score());
    }
}
