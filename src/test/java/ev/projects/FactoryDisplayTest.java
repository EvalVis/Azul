package ev.projects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FactoryDisplayTest {

    @Test
    void leftOverTilesArePushedToCenter() {
        Center center = new Center();
        Game game = new GameMother().new2PlayerGame(center);
        game.start();
        game.changeFactoryDisplay(0, Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW);

        game.executeFactoryOfferPhaseWithFactory(0, Tile.RED, 0, 0);

        assertEquals(0L, center.count(Tile.RED));
        assertEquals(1L, center.count(Tile.BLUE));
        assertEquals(1L, center.count(Tile.YELLOW));
    }
}
