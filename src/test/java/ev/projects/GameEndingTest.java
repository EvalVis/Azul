package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameEndingTest {

    @Test
    void playerScoresForCompletedHorizontalLines() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, floor, wall);
        patternLine.add(List.of(Tile.RED, Tile.RED));
        Player player = new Player(new Board(patternLine, wall), floor);
        Game game = new Game(List.of(player));
        completeFirstHorizontalLine(wall);
        completeSecondHorizontalLine(wall);
        int currentScore = player.score();

        game.executeGameEndingPhase();

        assertEquals(4, player.score() - currentScore);
    }

    @Test
    void playerScoresForCompletedVerticalLines() {
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, floor, wall);
        patternLine.add(List.of(Tile.RED, Tile.RED));
        Player player = new Player(new Board(patternLine, wall), floor);
        Game game = new Game(List.of(player));
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
        Wall wall = new Wall();
        Floor floor = new Floor();
        PatternLine patternLine = new PatternLine(5, floor, wall);
        patternLine.add(List.of(Tile.RED, Tile.RED));
        Player player = new Player(new Board(patternLine, wall), floor);
        Game game = new Game(List.of(player));
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
        Player player1 = new Player(new Board(new PatternLine(5, floor1, wall1), wall1), floor1, "Joke");
        player1.addScore(10);
        Wall wall2 = new Wall();
        Floor floor2 = new Floor();
        Player player2 = new Player(new Board(new PatternLine(5, floor2, wall2), wall2), floor2, "Alfonso");
        player2.addScore(20);
        Wall wall3 = new Wall();
        Floor floor3 = new Floor();
        Player player3 = new Player(new Board(new PatternLine(5, floor3, wall3), wall3), floor3, "Ra");
        player3.addScore(15);
        Game game = new Game(List.of(player1, player2, player3)
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
        Player player1 = new Player(new Board(new PatternLine(5, floor1, wall1), wall1), floor1, "Joke");
        player1.addScore(20);
        Wall wall2 = new Wall();
        completeSecondHorizontalLine(wall2);
        Floor floor2 = new Floor();
        Player player2 = new Player(new Board(new PatternLine(5, floor2, wall2), wall2), floor2, "Alfonso");
        player2.addScore(20);
        Game game = new Game(List.of(player1, player2)
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
        Player player1 = new Player(new Board(new PatternLine(5, floor1, wall1), wall1), floor1, "Joke");
        player1.addScore(20);
        Wall wall2 = new Wall();
        completeSecondHorizontalLine(wall2);
        Floor floor2 = new Floor();
        Player player2 = new Player(new Board(new PatternLine(5, floor2, wall2), wall2), floor2, "Alfonso");
        player2.addScore(20);
        Game game = new Game(List.of(player1, player2));

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
