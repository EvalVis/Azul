package ev.projects;

import java.util.List;
import java.util.stream.Collectors;

public class Game {
    private final List<Player> players;
    private final Center center;
    private final Bag bag;
    private final FactoryDisplay[] factoryDisplays;

    public Game(List<Player> players) {
        this(players, new Center());
    }

    public Game(List<Player> players, Center center) {
        this.players = players;
        this.center = center;
        this.bag = new Bag();
        factoryDisplays = new FactoryDisplay[7];
    }

    void start() {
        for (int i = 0; i < 7; i++) {
            List<Tile> tiles = bag.takeTiles(4);
            factoryDisplays[i] = new FactoryDisplay(center, tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(3));
        }
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

    FactoryDisplay[] factoryDisplays() {
        return factoryDisplays;
    }

    List<Tile> bagTiles() {
        return bag.tiles();
    }
}
