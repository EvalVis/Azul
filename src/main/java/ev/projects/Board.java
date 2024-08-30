package ev.projects;

public class Board {
    private final Player player;
    private final PatternLine patternLine;
    private final Wall wall;

    public Board(Player player, PatternLine patternLine, Wall wall) {
        this.player = player;
        this.patternLine = patternLine;
        this.wall = wall;
    }

    public void givePlayerFloorPenalty() {
        if (Math.abs(patternLine.floorPenalty()) > player.score()) {
            player.addScore(-player.score());
        }
        else {
            player.addScore(patternLine.floorPenalty());
        }
    }

    public void moveTileToWall() {
        if (patternLine.isFilled()) {
            wall.add(patternLine.colour(), 0);
            patternLine.clear();
        }
    }

    public void assignGameEndingScore() {
        player.addScore(wall.completedHorizontalLines() * 2);
        player.addScore(wall.completedVerticalLines() * 7);
        player.addScore(wall.completedTiles() * 10);
    }

    public int playerScore() {
        return player.score();
    }

    public Player player() {
        return player;
    }

    public Wall wall() {
        return wall;
    }
}
