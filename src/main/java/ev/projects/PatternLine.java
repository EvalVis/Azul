package ev.projects;

public class PatternLine {
    private final int size;
    private int tileCount;
    private Tile tile;

    public PatternLine(int size) {
        this.size = size;
        tileCount = 0;
        tile = null;
    }

    public int add(Tile tile, int count) {
        if(tileCount > 0 && this.tile != tile) {
            throw new ActionNotAllowedException(
                    "Tile(s) with " + this.tile + " colour is on the pattern line. Can't add a tile with " + tile + " colour."
            );
        }
        this.tile = tile;
        int overfill = Math.max(0, tileCount + count - size);
        tileCount = Math.min(size, tileCount + count);
        return overfill;
    }

    public int position() {
        return size - 1;
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
}
