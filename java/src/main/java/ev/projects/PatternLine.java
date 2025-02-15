package ev.projects;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public boolean isFilled() {
        return tileCount == size;
    }

    public int tileCount() {
        return tileCount;
    }

    public Tile tile() {
        return tile;
    }

    public void clear() {
        tileCount = 0;
        tile = null;
    }

    @Override
    public String toString() {
        return Optional.ofNullable(tile).map(t -> (t + " ").repeat(tileCount)).orElse("")
                + "E ".repeat(size - tileCount);
    }

    public List<String> jsonList() {
        return Stream.generate(() -> tile.toString())
                    .limit(tileCount)
                    .collect(Collectors.toList());
    }
}
