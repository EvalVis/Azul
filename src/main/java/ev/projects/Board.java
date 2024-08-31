package ev.projects;

public class Board {
    private final PatternLine[] patternLines;
    private final Wall wall;
    private final Floor floor;

    public Board(Wall wall, Floor floor) {
        this(
                new PatternLine[] {
                        new PatternLine(1), new PatternLine(2), new PatternLine(3), new PatternLine(4),
                        new PatternLine(5)
                },
                wall, floor
        );
    }

    public Board(PatternLine[] patternLines, Wall wall, Floor floor) {
        this.patternLines = patternLines;
        this.wall = wall;
        this.floor = floor;
    }

    public int floorPenalty() {
        return floor.score();
    }

    public void addTileToPatternLine(Tile tile, int count, int position) {
        if(wall.alreadyHas(tile, position)) {
            throw new ActionNotAllowedException("Wall already contains tiles(s) with " + tile + " colour.");
        }
        floor.add(patternLines[position].add(tile, count));
    }

    public int moveTilesFromPatternLinesToWall(int y) {
        int score = 0;
        for(PatternLine line : patternLines) {
            if (line.isFilled()) {
                score += wall.add(line.tile(), y);
                line.clear();
            }
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
