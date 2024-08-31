package ev.projects;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final Board board;
    private final List<Tile> tiles;
    private int score;
    private final String name;

    public Player(Board board) {
        this(board, "Erwin");
    }

    public Player(Board board, String name) {
        this.board = board;
        this.tiles = new ArrayList<>();
        this.score = 0;
        this.name = name;
    }

    void takeTilesFromFactory(FactoryDisplay factoryDisplay, Tile tile) {
        tiles.addAll(factoryDisplay.giveTiles(tile));
    }

    void takeTilesFromCenter(Center center, Tile tile) {
        tiles.addAll(center.giveTiles(tile, board));
    }

    void addToFloor(int amount) {
        board.addTilesToFloorLine(amount);
    }

    void addToPatternLine(int count) {
        if (count > tiles.size()) {
            throw new ActionNotAllowedException("Can't add more tiles than player " + name + " has.");
        }
        board.addTileToPatternLine(tiles.get(0), count);
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
