package ev.projects;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FactoryDisplay {
    private final Center center;
    private Tile[] tiles;

    public FactoryDisplay(Center center, Tile tile0, Tile tile1, Tile tile2, Tile tile3) {
        this.center = center;
        tiles = new Tile[] {tile0, tile1, tile2, tile3};
    }

    public int giveTiles(Tile tile) {
        Arrays.stream(tiles).filter(t -> !t.equals(tile)).forEach(center::addTile);
        long givenTiles = Arrays.stream(tiles).filter(t -> t.equals(tile)).count();
        tiles = null;
        return (int) givenTiles;
    }

    public Tile[] tiles() {
        return tiles;
    }

    public void clear() {
        tiles = null;
    }

    public boolean isEmpty() {
        return tiles == null;
    }

    @Override
    public String toString() {
        if (tiles == null) {
            return "Empty";
        }
        return Tile.count(Arrays.stream(tiles).toList());
    }
}
