package ev.projects;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameEndingTest {

    @Test
    void playerScoresForCompletedHorizontalLines() {
        Player player = new PlayerMother().newPlayer(new WallMother().withCompletedTwoHorizontalLines());
        Game game = new GameMother().new2PlayerGame(player);
        int currentScore = player.score();

        game.executeGameEndingPhase();

        assertEquals(4, player.score() - currentScore);
    }

    @Test
    void playerScoresForCompletedVerticalLines() {
        Player player = new PlayerMother().newPlayer(new WallMother().withThreeCompletedVerticalLines());
        Game game = new GameMother().new2PlayerGame(player);
        int currentScore = player.score();

        game.executeGameEndingPhase();

        assertEquals(21, player.score() - currentScore);
    }

    @Test
    void playerScoresForCompletedTiles() {
        Player player = new PlayerMother().newPlayer(new WallMother().withCompletedBlueAndYellowTiles());
        Game game = new GameMother().new2PlayerGame(player);
        int currentScore = player.score();

        game.executeGameEndingPhase();

        assertEquals(20, player.score() - currentScore);
    }

    @Test
    void winnerIsDeclared() {
        Player player1 = new PlayerMother().newPlayer("Joke");
        player1.addScore(10);
        Player player2 = new PlayerMother().newPlayer("Alfonso");
        player2.addScore(20);
        Player player3 = new PlayerMother().newPlayer("Ra");
        player3.addScore(15);
        Game game = new Game(List.of(player1, player2, player3));

        List<String> winner = game.winners();

        assertEquals(1, winner.size());
        assertEquals("Alfonso", winner.get(0));
    }

    @Test
    void resolvesDrawWithHorizontalLines() {
        Player player1 = new PlayerMother().newPlayer(new WallMother().withCompletedTwoHorizontalLines(), "Joke");
        player1.addScore(20);
        Player player2 = new PlayerMother().newPlayer(new WallMother().withCompletedHorizontalLine(), "Alfonso");
        player2.addScore(20);
        Game game = new Game(List.of(player1, player2));

        List<String> winner = game.winners();

        assertEquals(1, winner.size());
        assertEquals("Joke", winner.get(0));
    }

    @Test
    void bothPlayersWinIfDrawnAndSameAmountOfCompletedHorizontalLines() {
        Player player1 = new PlayerMother().newPlayer(new WallMother().withCompletedTwoHorizontalLines(), "Joke");
        player1.addScore(20);
        Player player2 = new PlayerMother().newPlayer(new WallMother().withCompletedTwoHorizontalLines(), "Alfonso");
        player2.addScore(20);
        Game game = new Game(List.of(player1, player2));

        List<String> winners = game.winners();

        assertEquals(2, winners.size());
    }
}
