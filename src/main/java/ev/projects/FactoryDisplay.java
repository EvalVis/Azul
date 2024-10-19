package ev.projects;

import java.util.ArrayList;
import java.util.List;

public class FactoryDisplay {
    private final Center center;
    private final List<Tile> tiles;

    public FactoryDisplay(Center center, List<Tile> tiles) {
        this.center = center;
        this.tiles = new ArrayList<>(tiles);
    }

    public int giveTiles(Tile tile) {
        tiles.stream().filter(t -> !t.equals(tile)).forEach(center::addTile);
        long givenTiles = tiles.stream().filter(t -> t.equals(tile)).count();
        tiles.clear();
        return (int) givenTiles;
    }

    public List<Tile> tiles() {
        return tiles;
    }

    public void clear() {
        tiles.clear();
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Empty";
        }
        return Tile.count(tiles);
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }
}
