package ev.projects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bag {
    private final Random random;
    private final List<Tile> tiles;

    Bag() {
        this.random = new Random();
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
            takenTiles.add(tiles.remove(random.nextInt(tiles.size())));
        }
        return takenTiles;
    }

    public List<Tile> tiles() {
        return tiles;
    }
}
