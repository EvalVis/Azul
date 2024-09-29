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

    public List<Tile> giveTiles(Tile tile) {
        Arrays.stream(tiles).filter(t -> !t.equals(tile)).forEach(center::addTile);
        List<Tile> givenTiles = Arrays.stream(tiles).filter(t -> t.equals(tile)).collect(Collectors.toList());
        tiles = null;
        return givenTiles;
    }

    public Tile[] tiles() {
        return tiles;
    }

    @Override
    public String toString() {
        if (tiles == null) {
            return "Empty";
        }
        return Tile.count(Arrays.stream(tiles).toList());
    }
}
