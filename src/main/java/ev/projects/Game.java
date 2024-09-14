package ev.projects;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Game {
    private final List<Player> players;
    private final Center center;
    private final Bag bag;
    private final FactoryDisplay[] factoryDisplays;
    private int currentPlayer;

    public Game(List<Player> players) {
        this(players, new Center());
    }

    public Game(List<Player> players, Center center) {
        this(players, center, new Random().nextInt(players.size()));
    }

    public Game(List<Player> players, Center center, int startingPlayer) {
        this.players = players;
        this.center = center;
        this.currentPlayer = startingPlayer;
        this.bag = new Bag();
        this.factoryDisplays = new FactoryDisplay[7];
    }

    void start() {
        for (int i = 0; i < 7; i++) {
            List<Tile> tiles = bag.takeTiles(4);
            factoryDisplays[i] = new FactoryDisplay(center, tiles.get(0), tiles.get(1), tiles.get(2), tiles.get(3));
        }
        players.get(currentPlayer).giveStartingMarker();
    }

    void changeFactoryDisplay(int index, Tile tile1, Tile tile2, Tile tile3, Tile tile4) {
        factoryDisplays[index] = new FactoryDisplay(center, tile1, tile2, tile3, tile4);
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
        return Arrays.copyOf(factoryDisplays, factoryDisplays.length);
    }

    List<Tile> peekCenter() {
        return center.tiles();
    }

    List<Tile> bagTiles() {
        return bag.tiles();
    }

    public void giveTilesFromFactory(int index, Tile tile) {
        players.get(currentPlayer).takeTiles(factoryDisplays[index].giveTiles(tile));
        currentPlayer = (currentPlayer == (players.size() - 1)) ? 0 : (currentPlayer + 1);
    }
}
