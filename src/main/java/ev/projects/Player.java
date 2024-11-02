package ev.projects;

import java.util.Map;

public class Player {
    private final Board board;
    private int score;
    private final String name;
    private boolean startsRound;

    public Player(Board board) {
        this(board, "Erwin");
    }

    public Player(Board board, String name) {
        this.board = board;
        this.score = 0;
        this.name = name;
        this.startsRound = false;
    }

    void takeTilesFromFactory(
            FactoryDisplay factoryDisplay, Tile tileToTake, int tilesToPlaceOnFloor, int patternLineIndex
    ) {
        placeTilesOnFloorAndPatternLine(
                factoryDisplay.giveTiles(tileToTake), tileToTake, tilesToPlaceOnFloor, patternLineIndex
        );
    }

    void takeTilesFromCenter(Center center, Tile tileToTake, int tilesToPlaceOnFloor, int patternLineIndex) {
        placeTilesOnFloorAndPatternLine(
                center.giveTiles(tileToTake, board), tileToTake, tilesToPlaceOnFloor, patternLineIndex
        );
    }

    private void placeTilesOnFloorAndPatternLine(
            int tileCount, Tile tileToPlace, int tilesToPlaceOnFloor, int patternLineIndex
    ) {
        if (tileCount < tilesToPlaceOnFloor) {
            throw new ActionNotAllowedException("You can't place more tiles on the floor than you have.");
        }
        tileCount -= tilesToPlaceOnFloor;
        board.addTilesToFloorLine(tileToPlace, tilesToPlaceOnFloor);
        board.addTileToPatternLine(tileToPlace, tileCount, patternLineIndex);
    }

    public void giveFloorPenalty() {
        score = Math.max(0, score + board.floorPenalty());
    }

    public void assignGameEndingScore() {
        score += board.gameEndingScore();
    }

    public void moveTilesToWall() {
        score += board.moveTilesFromPatternLinesToWall();
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

    @Override
    public String toString() {
        String result = name() + ":\nScore: " + score();
        if (startsRound()) {
            result += "\nHas the starting player token.";
        }
        result += "\n" + board.toString();
        return result;
    }

    public Map<String, Object> jsonObject() {
        return Map.of(
                "Name", name(), "Score", score(), "startsRound", startsRound(), "Board", board.jsonObject()
        );
    }
}
