package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {

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
}
