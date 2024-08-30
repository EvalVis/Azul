package ev.projects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class Wall {
    private final Player player;
    private final WallTile[][] tiles;

    Wall() {
        this(new Player(new Floor()));
    }

    public Wall(Player player) {
        this.player = player;
        tiles = new WallTile[][] {
                {
                        new WallTile(Tile.BLUE), new WallTile(Tile.YELLOW), new WallTile(Tile.RED), new WallTile(Tile.BLACK),
                        new WallTile(Tile.WHITE)
                },
                {
                        new WallTile(Tile.WHITE), new WallTile(Tile.BLUE), new WallTile(Tile.YELLOW), new WallTile(Tile.RED),
                        new WallTile(Tile.BLACK)
                },
                {
                        new WallTile(Tile.BLACK), new WallTile(Tile.WHITE), new WallTile(Tile.BLUE), new WallTile(Tile.YELLOW),
                        new WallTile(Tile.RED)
                },
                {
                        new WallTile(Tile.RED), new WallTile(Tile.BLACK), new WallTile(Tile.WHITE), new WallTile(Tile.BLUE),
                        new WallTile(Tile.YELLOW)
                },
                {
                        new WallTile(Tile.YELLOW), new WallTile(Tile.RED), new WallTile(Tile.BLACK), new WallTile(Tile.WHITE),
                        new WallTile(Tile.BLUE)
                }
        };
    }

    public void add(Tile tile, int y) {
        int x = (tile.ordinal() + y) % 5;
        tiles[y][x].isPlaced = true;
        player.addScore(score(x, y));
    }

    public int score(int x, int y) {
        int score = 0;
        int horizontalScore = 0;
        int marker = x - 1;
        while(marker >= 0 && tiles[y][marker].isPlaced) {
            marker--;
            horizontalScore++;
        }
        marker = x + 1;
        while(marker < tiles[y].length && tiles[y][marker].isPlaced) {
            marker++;
            horizontalScore++;
        }
        if (horizontalScore > 0) {
            score++;
        }
        score += horizontalScore;
        int verticalScore = 0;
        marker = y - 1;
        while(marker >= 0 && tiles[marker][x].isPlaced) {
            marker--;
            verticalScore++;
        }
        marker = y + 1;
        while(marker < tiles.length && tiles[marker][x].isPlaced) {
            marker++;
            verticalScore++;
        }
        if (verticalScore > 0) {
            score++;
        }
        score += verticalScore;
        if (score == 0) {
            score = 1;
        }
        return score;
    }

    public boolean alreadyHas(Tile tile) {
        return alreadyHas(tile, 0);
    }

    public boolean alreadyHas(Tile tile, int y) {
        return tiles[y][(tile.ordinal() + y) % 5].isPlaced;
    }

    public int completedHorizontalLines() {
        int result = 0;
        for (WallTile[] tile : tiles) {
            if (Arrays.stream(tile).allMatch(t -> t.isPlaced)) {
                result++;
            }
        }
        return result;
    }

    public int completedVerticalLines() {
        return (int) IntStream
                .range(0, tiles.length)
                .filter(col -> IntStream.range(0, tiles[0].length).allMatch(row -> tiles[row][col].isPlaced))
                .count();
    }

    public int completedTiles() {
        Set<Tile> notCompletedTiles = new HashSet<>();

        Arrays.stream(tiles).flatMap(Arrays::stream).forEach(t -> {
            if (!t.isPlaced) {
                notCompletedTiles.add(t.tile);
            }
        });
        return Tile.values().length - notCompletedTiles.size();
    }

    static class WallTile {
        private final Tile tile;
        private boolean isPlaced;

        public WallTile(Tile tile) {
            this.tile = tile;
        }
    }
}
