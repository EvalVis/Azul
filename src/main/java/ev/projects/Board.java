package ev.projects;

public class Board {
    private final PatternLine patternLine;
    private final Wall wall;

    public Board(PatternLine patternLine, Wall wall) {
        this.patternLine = patternLine;
        this.wall = wall;
    }

    public int floorPenalty() {
        return patternLine.floorPenalty();
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
}
