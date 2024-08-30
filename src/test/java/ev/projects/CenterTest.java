package ev.projects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CenterTest {

    @Test
    void playerTakingFromCenterFirstWithEmptyFloorGetsPenalty() {
        Center center = new Center();
        FactoryDisplay display = new FactoryDisplay(center, Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW);
        Floor floor = new Floor();
        Player player = new Player(floor);
        player.takeTilesFromFactory(display, Tile.RED);

        player.takeTilesFromCenter(center, Tile.BLUE);

        assertEquals(-1, floor.score());
    }

    @Test
    void playerTakingFromCenterFirstWithNonEmptyFloorGetsPenalty() {
        Center center = new Center();
        FactoryDisplay display = new FactoryDisplay(center, Tile.RED, Tile.RED, Tile.RED, Tile.BLUE);
        Floor floor = new Floor();
        Player player = new Player(floor);
        player.takeTilesFromFactory(display, Tile.RED);
        player.addToFloor(floor, 3);

        player.takeTilesFromCenter(center, Tile.BLUE);

        assertEquals(-6, floor.score());
    }

    @Test
    void playerTakingFromCenterSecondDoesNotGetPenalty() {
        Center center = new Center();
        FactoryDisplay display = new FactoryDisplay(center, Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW);
        Player player1 = new Player(new Floor());
        player1.takeTilesFromFactory(display, Tile.RED);
        player1.takeTilesFromCenter(center, Tile.BLUE);
        Floor player2Floor = new Floor();

        new Player(player2Floor).takeTilesFromCenter(center, Tile.YELLOW);

        assertEquals(0, player2Floor.score());
    }
}
