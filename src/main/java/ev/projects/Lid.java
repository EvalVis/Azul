package ev.projects;

import java.util.ArrayList;
import java.util.List;

public class Lid {
    private final List<Tile> tiles;

    Lid() {
        this.tiles = new ArrayList<>();
    }

    public void addTile(Tile addition) {
        tiles.add(addition);
    }

    public List<Tile> tiles() {
        return tiles;
    }

    public List<Tile> giveTiles() {
        List<Tile> tilesToGive = tiles;
        tiles.clear();
        return tilesToGive;
    }

    @Override
    public String toString() {
        if (tiles.size() == 0) {
            return "Empty";
        }
        return Tile.count(tiles);
    }
}
