package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameStartTest {

    @Test
    void factoryDisplaysAreFilled() {
        Center center = new Center();
        Board player1Board = new Board();
        Board player2Board = new Board();
        Board player3Board = new Board();
        Game game = new Game(List.of(new Player(player1Board), new Player(player2Board)), center);
        game.start();

        long blue = Arrays.stream(game.factoryDisplays())
                .mapToLong(fd -> fd.giveTiles(Tile.BLUE))
                .sum();
        int yellow = center.giveTiles(Tile.YELLOW, player2Board);
        int red = center.giveTiles(Tile.RED, player3Board);
        int black = center.giveTiles(Tile.BLACK, player1Board);
        int white = center.giveTiles(Tile.WHITE, player2Board);
        assertEquals(100, blue + yellow + red + black + white + game.bagTiles().size());
        long blueFromBag = game.bagTiles().stream().filter(t -> t == Tile.BLUE).count();
        long yellowFromBag = game.bagTiles().stream().filter(t -> t == Tile.YELLOW).count();
        long redFromBag = game.bagTiles().stream().filter(t -> t == Tile.RED).count();
        long blackFromBag = game.bagTiles().stream().filter(t -> t == Tile.BLACK).count();
        long whiteFromBag = game.bagTiles().stream().filter(t -> t == Tile.WHITE).count();
        assertEquals(20, blue + blueFromBag);
        assertEquals(20, yellow + yellowFromBag);
        assertEquals(20, red + redFromBag);
        assertEquals(20, black + blackFromBag);
        assertEquals(20, white + whiteFromBag);
    }

    @Test
    void onePlayerHasStartingMarker() {
        Player player1 = new PlayerMother().newPlayer();
        Player player2 = new PlayerMother().newPlayer();
        Game game = new Game(List.of(player1, player2), new Center(), 0);

        game.start();

        assertTrue(player1.startsRound() && !player2.startsRound());
    }

    @Test
    void playersStartWith0Points() {
        Player player1 = new PlayerMother().newPlayer();
        Player player2 = new PlayerMother().newPlayer();
        Game game = new Game(List.of(player1, player2));

        game.start();

        assertEquals(0, player1.score());
        assertEquals(0, player2.score());
    }
}
