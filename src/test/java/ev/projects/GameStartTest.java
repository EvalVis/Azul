package ev.projects;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameStartTest {

    @ParameterizedTest
    @MethodSource("games")
    void factoryDisplaysAreFilled() {
        Center center = new Center();
        Board player1Board = new Board(new Wall(), new Floor());
        Board player2Board = new Board(new Wall(), new Floor());
        Board player3Board = new Board(new Wall(), new Floor());
        Game game = new Game(
                List.of(new Player(player1Board), new Player(player2Board), new Player(player3Board)), center
        );

        game.start();

        long blue = Arrays.stream(game.factoryDisplays())
                .mapToLong(fd -> fd.giveTiles(Tile.BLUE).size())
                .sum();
        int yellow = center.giveTiles(Tile.YELLOW, player2Board).size();
        int red = center.giveTiles(Tile.RED, player3Board).size();
        int black = center.giveTiles(Tile.BLACK, player1Board).size();
        int white = center.giveTiles(Tile.WHITE, player2Board).size();
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

    private static Stream<Game> games() {
        Center center = new Center();
        Board player1Board = new Board(new Wall(), new Floor());
        Board player2Board = new Board(new Wall(), new Floor());
        Board player3Board = new Board(new Wall(), new Floor());
        Board player4Board = new Board(new Wall(), new Floor());
        return Stream.of(
                new Game(List.of(new Player(player1Board), new Player(player2Board)), center),
                new Game(List.of(new Player(player1Board), new Player(player2Board), new Player(player3Board)), center),
                new Game(
                        List.of(
                                new Player(player1Board), new Player(player2Board), new Player(player3Board),
                                new Player(player4Board)
                        ), center
                )
        );
    }
}
