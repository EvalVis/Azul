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

    @Override
    public String toString() {
        return Tile.count(tiles);
    }
}
