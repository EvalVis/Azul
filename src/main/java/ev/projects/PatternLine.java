package ev.projects;

import java.util.ArrayList;
import java.util.List;

public class PatternLine {
    private final int size;
    private final List<Tile> tiles;
    private final Floor floor;
    private final Wall wall;

    public PatternLine(int size, Floor floor, Wall wall) {
        this.size = size;
        this.tiles = new ArrayList<>();
        this.floor = floor;
        this.wall = wall;
    }

    public void add(List<Tile> newTiles) {
        add(newTiles, 0);
    }

    public void add(List<Tile> newTiles, int y) {
        if(tiles.size() > 0) {
            if (!newTiles.get(0).equals(tiles.get(0))) {
                throw new ActionNotAllowedException(
                        tiles.get(0) + " colour is on the pattern line. Can't add " + tiles.get(0) + " colour."
                );
            }
        }
        if(wall.alreadyHas(newTiles.get(0), y)) { // TODO: Move this logic out.
            throw new ActionNotAllowedException("Wall already contains " + newTiles.get(0) + " colour.");
        }
        if (tiles.size() + newTiles.size() > size) {
            floor.add(tiles.size() + newTiles.size() - size); // TODO: Move this logic out.
            tiles.addAll(newTiles.stream().limit(size - tiles.size()).toList());
            return;
        }
        tiles.addAll(newTiles);
    }

    public boolean isFilled() {
        return tiles.size() == size;
    }

    public int tileCount() {
        return tiles.size();
    }

    public Tile colour() {
        return tiles.get(0);
    }

    public void clear() {
        tiles.clear();
    }

    public int floorPenalty() {
        return floor.score();
    }
}
