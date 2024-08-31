package ev.projects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    private final Board board;
    private final Floor floor;
    private final List<Tile> tiles;
    private int score;
    private final String name;

    public Player(Board board, Floor floor) {
        this(board, floor, "Erwin");
    }

    public Player(Board board, Floor floor, String name) {
        this.board = board;
        this.floor = floor;
        this.tiles = new ArrayList<>();
        this.score = 0;
        this.name = name;
    }

    void takeTilesFromFactory(FactoryDisplay factoryDisplay, Tile tile) {
        tiles.addAll(factoryDisplay.giveTiles(tile));
    }

    void takeTilesFromCenter(Center center, Tile tile) {
        tiles.addAll(center.giveTiles(tile, floor));
    }

    void addToFloor(Floor floor, int count) {
        floor.add(count);
    }

    void addToPatternLine(PatternLine patternLine, int count) {
        patternLine.add(tiles.stream().limit(count).collect(Collectors.toList()), 0);
    }

    public void giveFloorPenalty() {
        score = Math.max(0, score + board.floorPenalty());
    }

    public void assignGameEndingScore() {
        score += board.gameEndingScore();
    }

    public void moveTileToWall() {
        moveTileToWall(0);
    }

    public void moveTileToWall(int y) {
        score += board.moveTileToWall(y);
    }

    void addScore(int score) {
        this.score += score;
    }

    int score() {
        return score;
    }

    String name() {
        return name;
    }

    Board board() {
        return board;
    }
}
