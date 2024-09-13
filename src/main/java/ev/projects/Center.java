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

    public List<Tile> giveTiles(Tile tile, Board board) {
        List<Tile> tilesToGive = tiles.stream().filter(t -> t.equals(tile)).toList();
        tiles.removeAll(tilesToGive);
        if (nobodyHasTakenFromCenter) { // TODO: Move out this logic. Center should not know about the board. Game should.
            nobodyHasTakenFromCenter = false;
            board.addTilesToFloorLine(1);
        }
        return tilesToGive;
    }

    public long count(Tile tile) {
        return tiles.stream().filter(t -> t.equals(tile)).count();
    }
}
