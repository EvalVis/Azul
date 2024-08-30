package ev.projects;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Game {
    private final List<Board> boards;

    public Game(List<Board> boards) {
        this.boards = boards;
    }

    void executeWallTilingPhase() {
        boards.forEach(b -> {
            b.moveTileToWall();
            b.givePlayerFloorPenalty();
        });
    }

    void executeGameEndingPhase() {
        boards.forEach(Board::assignGameEndingScore);
    }

    List<Player> winners() {
        int maxScore = boards.stream()
                .map(Board::playerScore)
                .max(Integer::compareTo).get();
        List<Board> bestBoards = boards.stream().filter(b -> b.playerScore() == maxScore).toList();

        if(bestBoards.size() > 1) {
            int maxCompletedHorizontalLines = boards.stream()
                    .map(b -> b.wall().completedHorizontalLines())
                    .max(Integer::compareTo).get();
            return bestBoards
                    .stream()
                    .filter(b -> b.wall().completedHorizontalLines() == maxCompletedHorizontalLines)
                    .map(Board::player)
                    .collect(Collectors.toList());
        }

        return bestBoards.stream().map(Board::player).collect(Collectors.toList());
    }
}
