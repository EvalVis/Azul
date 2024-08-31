package ev.projects;

import java.util.List;
import java.util.stream.Collectors;

public class Game {
    private final List<Player> players;

    public Game(List<Player> players) {
        this.players = players;
    }

    void executeWallTilingPhase() {
        players.forEach(p -> {
            p.moveTileToWall();
            p.giveFloorPenalty();
        });
    }

    void executeGameEndingPhase() {
        players.forEach(Player::assignGameEndingScore);
    }

    List<Player> winners() {
        int maxScore = players.stream()
                .map(Player::score)
                .max(Integer::compareTo).get();
        List<Player> bestPlayers = players.stream().filter(p -> p.score() == maxScore).toList();

        if(bestPlayers.size() > 1) {
            int maxCompletedHorizontalLines = players.stream()
                    .map(p -> p.board().wall().completedHorizontalLines())
                    .max(Integer::compareTo).get();
            return players
                    .stream()
                    .filter(p -> p.board().wall().completedHorizontalLines() == maxCompletedHorizontalLines)
                    .collect(Collectors.toList());
        }

        return bestPlayers;
    }
}
