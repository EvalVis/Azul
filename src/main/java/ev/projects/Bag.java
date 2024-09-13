package ev.projects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bag {
    private final List<Tile> tiles;

    Bag() {
        tiles = new ArrayList<>();
        for (Tile tileValue : Tile.values()) {
            for (int j = 0; j < 20; j++) {
                tiles.add(tileValue);
            }
        }
    }

    public List<Tile> takeTiles(int amount) {
        List<Tile> takenTiles = new ArrayList<>(amount);
        for(int i = 0; i < amount; i++) {
            takenTiles.add(tiles.remove(new Random().nextInt(tiles.size())));
        }
        return takenTiles;
    }

    public List<Tile> tiles() {
        return tiles;
    }
}
