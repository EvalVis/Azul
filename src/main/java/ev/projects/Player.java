package ev.projects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Player {
    private final Board board;
    private final List<Tile> tiles;
    private int score;
    private final String name;
    private boolean startsRound;

    public Player(Board board) {
        this(board, "Erwin");
    }

    public Player(Board board, String name) {
        this.board = board;
        this.tiles = new ArrayList<>();
        this.score = 0;
        this.name = name;
        this.startsRound = false;
    }

    void takeTiles(List<Tile> tiles) {
        this.tiles.addAll(tiles);
    }

    void takeTilesFromCenter(Center center, Tile tile) {
        tiles.addAll(center.giveTiles(tile, board));
    }

    void addToFloor(int amount) {
        if (amount > tileCount()) {
            throw new ActionNotAllowedException("You can't place more tiles on floor than you have.");
        }
        board.addTilesToFloorLine(tiles.stream().limit(amount).collect(Collectors.toList()));
        tiles.subList(0, amount).clear();
    }

    void addTileToPatternLine(int position) {
        addTileToPatternLine(tileCount(), position);
    }

    void addTileToPatternLine(int count, int position) {
        if (count > tiles.size()) {
            throw new ActionNotAllowedException("Can't add more tiles than player " + name + " has.");
        }
        board.addTileToPatternLine(tiles.get(0), count, position);
        tiles.subList(0, count).clear();
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
        score += board.moveTilesFromPatternLinesToWall(y);
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

    public void giveStartingMarker() {
        startsRound = true;
    }

    public boolean startsRound() {
        return startsRound;
    }

    public int tileCount() {
        return tiles.size();
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    @Override
    public String toString() {
        String result = "Player " + name() + ":\nHand-held tiles: " + Tile.count(tiles);
        result += "\nScore: " + score();
        if (startsRound()) {
            result += "\nHas the starting player token.";
        }
        result += "\n" + board.toString();
        return result;
    }
}
