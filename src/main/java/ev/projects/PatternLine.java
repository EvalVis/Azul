package ev.projects;

public class PatternLine {
    private final int size;
    private int tileCount;
    private Tile tile;
    private final Floor floor;
    private final Wall wall;

    public PatternLine(int size, Floor floor, Wall wall) {
        this.size = size;
        tileCount = 0;
        tile = null;
        this.floor = floor;
        this.wall = wall;
    }

    public void add(Tile tile, int count) {
        add(tile, count, 0);
    }

    public void add(Tile tile, int count, int y) {
        if(tileCount > 0) {
            if (this.tile != tile) {
                throw new ActionNotAllowedException(
                        "Tile(s) with " + this.tile + " colour is on the pattern line. Can't add a tile with " + tile + " colour."
                );
            }
        }
        if(wall.alreadyHas(tile, y)) { // TODO: Move this logic out.
            throw new ActionNotAllowedException("Wall already contains tiles(s) with " + tile + " colour.");
        }
        else {
            this.tile = tile;
        }
        floor.add(Math.max(0, tileCount + count - size));
        tileCount = Math.min(size, tileCount + count);
    }

    public boolean isFilled() {
        return tileCount == size;
    }

    public int tileCount() {
        return tileCount;
    }

    public Tile colour() {
        return tile;
    }

    public void clear() {
        tileCount = 0;
        tile = null;
    }

    public int floorPenalty() {
        return floor.score();
    }
}
