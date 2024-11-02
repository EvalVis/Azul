package ev.projects;

import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private final List<Player> players;
    private final Center center;
    private Bag bag;
    private final Lid lid;
    private final FactoryDisplay[] factoryDisplays;
    private int currentPlayer;
    private boolean isRunning;

    public Game(List<Player> players) {
        this(players, new Center());
    }

    public Game(List<Player> players, Center center) {
        this(players, center, new Random().nextInt(players.size()));
    }

    public Game(List<Player> players, Center center, int startingPlayer) {
        this(players, center, startingPlayer, new Lid());
    }

    public Game(List<Player> players, Center center, int startingPlayer, Lid lid) {
        this.players = players;
        this.center = center;
        this.currentPlayer = startingPlayer;
        this.bag = new Bag();
        this.lid = lid;
        this.factoryDisplays = new FactoryDisplay[1 + 2 * players.size()];
        for (int i = 0; i < 1 + 2 * players.size(); i++) {
            factoryDisplays[i] = new FactoryDisplay(center, bag.takeTiles(4, lid));
        }
        players.get(currentPlayer).giveStartingMarker();
        isRunning = true;
    }

    void changeFactoryDisplay(int index, List<Tile> tiles) {
        factoryDisplays[index] = new FactoryDisplay(center, tiles);
    }

    void setBag(Bag bag) {
        this.bag = bag;
    }

    void executeWallTilingPhase() {
        players.forEach(p -> {
            p.moveTilesToWall();
            p.giveFloorPenalty();
            p.board().clearFloor();
        });
        if (players.stream().noneMatch(p -> p.board().wall().completedHorizontalLines() > 0)) {
            prepareForNextRound();
        }
        else {
            executeGameEndingPhase();
        }
    }

    void prepareForNextRound() {
        for (int i = 0; i < 1 + 2 * players.size(); i++) {
            if (factoryDisplays[i].isEmpty()) {
                factoryDisplays[i] = new FactoryDisplay(center, bag.takeTiles(4, lid));
            }
        }
    }

    void executeGameEndingPhase() {
        players.forEach(Player::assignGameEndingScore);
        isRunning = false;
    }

    FactoryDisplay[] factoryDisplays() {
        return Arrays.copyOf(factoryDisplays, factoryDisplays.length);
    }

    public void clearFactoryDisplays() {
        for (FactoryDisplay factoryDisplay : factoryDisplays) {
            factoryDisplay.clear();
        }
    }

    List<Tile> peekCenter() {
        return center.tiles();
    }

    List<Tile> bagTiles() {
        return bag.tiles();
    }

    public void executeFactoryOfferPhaseWithFactory(
            int factoryIndex, Tile tileToTake, int amountToPlaceOnFloor, int patternLineIndex
    ) {
        players
                .get(currentPlayer)
                .takeTilesFromFactory(
                        factoryDisplays[factoryIndex], tileToTake, amountToPlaceOnFloor, patternLineIndex
                );
        if (Arrays.stream(factoryDisplays).allMatch(FactoryDisplay::isEmpty) && center.isEmpty()) {
            executeWallTilingPhase();
        } else {
            currentPlayer = (currentPlayer == (players.size() - 1)) ? 0 : (currentPlayer + 1);
        }
    }

    public void executeFactoryOfferPhaseWithCenter(Tile tileToTake, int amountToPlaceOnFloor, int patternLineIndex) {
        players
                .get(currentPlayer)
                .takeTilesFromCenter(
                        center, tileToTake, amountToPlaceOnFloor, patternLineIndex
                );
        if (Arrays.stream(factoryDisplays).allMatch(FactoryDisplay::isEmpty) && center.isEmpty()) {
            executeWallTilingPhase();
        } else {
            currentPlayer = (currentPlayer == (players.size() - 1)) ? 0 : (currentPlayer + 1);
        }
    }

    @Override
    public String toString() {
        StringBuilder result;
        if (isRunning) {
            result = new StringBuilder("Factories: ");
            for (int i = 0; i < factoryDisplays().length; i++) {
                result.append(i + 1).append(") ").append(factoryDisplays[i].toString());
                if (i < factoryDisplays().length - 1) {
                    result.append(" ");
                }
            }
            result.append("\n");
            result.append("Center: ").append(center.toString()).append("\n");
            for (Player player : players) {
                result.append("Player: ").append(player).append("\n");
            }
            result
                    .append("Bag: ").append(bag.toString()).append("\n")
                    .append("Lid: ").append(lid.toString());
        } else {
            result = new StringBuilder();
            for (Player player : players) {
                result.append(player.name()).append(" : ").append(player.score());
            }
        }
        return result.toString();
    }

    public Map<String, Object> jsonObject() {
        if (isRunning) {
            List<Map<String, Object>> playersJson = new ArrayList<>();
            for (Player player : players) {
                playersJson.add(player.jsonObject());
            }

            List<Map<String, Integer>> factoryDisplaysJson = new ArrayList<>();
            for (FactoryDisplay factoryDisplay : factoryDisplays) {
                factoryDisplaysJson.add(factoryDisplay.jsonObject());
            }
            return Map.of(
                    "isRunning", true, "Factory displays", factoryDisplaysJson, "Center", center.jsonObject(),
                    "Players", playersJson, "Bag", bag.jsonObject(), "Lid", lid.jsonObject()
            );
        } else {
            Map<String, Integer> playersScores = new HashMap<>();
            for (Player player : players) {
                playersScores.put(player.name(), player.score());
            }
            return Map.of(
                    "isRunning", false,
                    "Game results", Map.of("Winners", winners(), "Final scores", playersScores)
            );
        }
    }

    List<String> winners() {
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
                    .map(Player::name)
                    .collect(Collectors.toList());
        }

        return bestPlayers.stream().map(Player::name).collect(Collectors.toList());
    }
}
