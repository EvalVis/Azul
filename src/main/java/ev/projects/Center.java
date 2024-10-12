package ev.projects;

import java.util.ArrayList;
import java.util.List;

public class Center {
    private final List<Tile> tiles;
    private boolean nobodyHasTakenFromCenter;

    public Center() {
        tiles = new ArrayList<>();
        nobodyHasTakenFromCenter = true;
    }

    public void addTile(Tile tile) {
        tiles.add(tile);
    }

    public int giveTiles(Tile tile, Board board) {
        int tileCount = (int) tiles.stream().filter(t -> t.equals(tile)).count();
        tiles.removeIf(t -> t.equals(tile));
        if (nobodyHasTakenFromCenter) {
            nobodyHasTakenFromCenter = false;
            board.addFirstPlayerMarkerToFloorLine();
        }
        return tileCount;
    }

    public long count(Tile tile) {
        return tiles.stream().filter(t -> t.equals(tile)).count();
    }

    public List<Tile> tiles() {
        return tiles;
    }

    @Override
    public String toString() {
        return Tile.count(tiles);
    }
}
