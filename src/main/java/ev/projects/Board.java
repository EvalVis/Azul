package ev.projects;

public class Board {
    private final PatternLine patternLine;
    private final Wall wall;
    private final Floor floor;

    public Board(PatternLine patternLine, Wall wall, Floor floor) {
        this.patternLine = patternLine;
        this.wall = wall;
        this.floor = floor;
    }

    public int floorPenalty() {
        return floor.score();
    }

    public void addTileToPatternLine(Tile tile, int count, int y) {
        floor.add(patternLine.add(tile, count, y));
    }

    public int moveTileToWall(int y) {
        int score = 0;
        if (patternLine.isFilled()) {
            score = wall.add(patternLine.colour(), y);
            patternLine.clear();
        }
        return score;
    }

    public int gameEndingScore() {
        return wall.completedHorizontalLines() * 2 + wall.completedVerticalLines() * 7 + wall.completedTiles() * 10;
    }

    public Wall wall() {
        return wall;
    }

    public void addTilesToFloorLine(int amount) {
        floor.add(amount);
    }
}
