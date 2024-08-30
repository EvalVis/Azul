package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameEndingTest {

    @Test
    void playerScoresForCompletedHorizontalLines() {
        Floor floor = new Floor();
        Player player = new Player(floor);
        Wall wall = new Wall(player);
        Game game = new Game(List.of(new Board(player, new PatternLine(5, floor, wall), wall)));
        completeFirstHorizontalLine(wall);
        completeSecondHorizontalLine(wall);
        int currentScore = player.score();

        game.executeGameEndingPhase();

        assertEquals(4, player.score() - currentScore);
    }

    @Test
    void playerScoresForCompletedVerticalLines() {
        Floor floor = new Floor();
        Player player = new Player(floor);
        Wall wall = new Wall(player);
        Game game = new Game(List.of(new Board(player, new PatternLine(5, floor, wall), wall)));
        wall.add(Tile.BLUE, 0);
        wall.add(Tile.WHITE, 1);
        wall.add(Tile.BLACK, 2);
        wall.add(Tile.RED, 3);
        wall.add(Tile.YELLOW, 4);
        wall.add(Tile.YELLOW, 0);
        wall.add(Tile.BLUE, 1);
        wall.add(Tile.WHITE, 2);
        wall.add(Tile.BLACK, 3);
        wall.add(Tile.RED, 4);
        wall.add(Tile.RED, 0);
        wall.add(Tile.YELLOW, 1);
        wall.add(Tile.BLUE, 2);
        wall.add(Tile.WHITE, 3);
        wall.add(Tile.BLACK, 4);
        int currentScore = player.score();

        game.executeGameEndingPhase();

        assertEquals(21, player.score() - currentScore);
    }

    @Test
    void playerScoresForCompletedTiles() {
        Floor floor = new Floor();
        Player player = new Player(floor);
        Wall wall = new Wall(player);
        Game game = new Game(List.of(new Board(player, new PatternLine(5, floor, wall), wall)));
        wall.add(Tile.BLUE, 0);
        wall.add(Tile.BLUE, 1);
        wall.add(Tile.BLUE, 2);
        wall.add(Tile.BLUE, 3);
        wall.add(Tile.BLUE, 4);
        wall.add(Tile.YELLOW, 0);
        wall.add(Tile.YELLOW, 1);
        wall.add(Tile.YELLOW, 2);
        wall.add(Tile.YELLOW, 3);
        wall.add(Tile.YELLOW, 4);
        int currentScore = player.score();

        game.executeGameEndingPhase();

        assertEquals(20, player.score() - currentScore);
    }

    @Test
    void winnerIsDeclared() {
        Wall wall1 = new Wall();
        Floor floor1 = new Floor();
        Player player1 = new Player(floor1, "Joke");
        player1.addScore(10);
        Wall wall2 = new Wall();
        Floor floor2 = new Floor();
        Player player2 = new Player(new Floor(), "Alfonso");
        player2.addScore(20);
        Wall wall3 = new Wall();
        Floor floor3 = new Floor();
        Player player3 = new Player(new Floor(), "Ra");
        player3.addScore(15);
        Game game = new Game(
                List.of(
                        new Board(player1, new PatternLine(5, floor1, wall1), wall1),
                        new Board(player2, new PatternLine(5, floor2, wall2), wall2),
                        new Board(player1, new PatternLine(5, floor3, wall3), wall3)
                )
        );

        List<Player> winner = game.winners();

        assertEquals(1, winner.size());
        assertEquals("Alfonso", winner.get(0).name());
    }

    @Test
    void resolvesDrawWithHorizontalLines() {
        Wall wall1 = new Wall();
        completeFirstHorizontalLine(wall1);
        completeSecondHorizontalLine(wall1);
        Floor floor1 = new Floor();
        Player player1 = new Player(floor1, "Joke");
        player1.addScore(20);
        Wall wall2 = new Wall();
        completeSecondHorizontalLine(wall2);
        Floor floor2 = new Floor();
        Player player2 = new Player(new Floor(), "Alfonso");
        player2.addScore(20);
        Game game = new Game(
                List.of(
                        new Board(player1, new PatternLine(5, floor1, wall1), wall1),
                        new Board(player2, new PatternLine(5, floor2, wall2), wall2)
                )
        );

        List<Player> winner = game.winners();

        assertEquals(1, winner.size());
        assertEquals("Joke", winner.get(0).name());
    }

    @Test
    void bothPlayersWinIfDrawnAndSameAmountOfCompletedHorizontalLines() {
        Wall wall1 = new Wall();
        completeFirstHorizontalLine(wall1);
        Floor floor1 = new Floor();
        Player player1 = new Player(floor1, "Joke");
        player1.addScore(20);
        Wall wall2 = new Wall();
        completeSecondHorizontalLine(wall2);
        Floor floor2 = new Floor();
        Player player2 = new Player(new Floor(), "Alfonso");
        player2.addScore(20);
        Game game = new Game(
                List.of(
                        new Board(player1, new PatternLine(5, floor1, wall1), wall1),
                        new Board(player2, new PatternLine(5, floor2, wall2), wall2)
                )
        );

        List<Player> winners = game.winners();

        assertEquals(2, winners.size());
    }

    private void completeFirstHorizontalLine(Wall wall) {
        wall.add(Tile.BLUE, 0);
        wall.add(Tile.YELLOW, 0);
        wall.add(Tile.RED, 0);
        wall.add(Tile.BLACK, 0);
        wall.add(Tile.WHITE, 0);
    }

    private void completeSecondHorizontalLine(Wall wall) {
        wall.add(Tile.WHITE, 1);
        wall.add(Tile.BLUE, 1);
        wall.add(Tile.YELLOW, 1);
        wall.add(Tile.RED, 1);
        wall.add(Tile.BLACK, 1);
    }
}
