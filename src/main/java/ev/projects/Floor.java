package ev.projects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Floor {
    private final List<Tile> tiles;
    private int firstPlayerMarkerPosition;
    private final Lid lid;

    public Floor() {
        this(new Lid());
    }

    public Floor(Lid lid) {
        tiles = new ArrayList<>();
        firstPlayerMarkerPosition = -1;
        this.lid = lid;
    }

    void clear() {
        tiles.forEach(lid::addTile);
        tiles.clear();
        firstPlayerMarkerPosition = -1;
    }

    void addFirstPlayerMarker() {
        firstPlayerMarkerPosition = tiles.size();
    }

    void add(Tile tile, int amount) {
        int overfill = tiles.size() + amount - 7;
        for (int i = 0; i < overfill; i++) {
            lid.addTile(tile);
        }
        int amountToFill = Math.min(amount, 7 - tiles.size());
        for (int i = 0; i < amountToFill; i++) {
            tiles.add(tile);
        }
    }

    int score() {
        int tileCountWithMarker = (firstPlayerMarkerPosition > -1) ? (tiles.size() + 1) : tiles.size();
        return switch (tileCountWithMarker) {
            case 0 -> 0;
            case 1 -> -1;
            case 2 -> -2;
            case 3 -> -4;
            case 4 -> -6;
            case 5 -> -8;
            case 6 -> -11;
            case 7 -> -14;
            default -> throw new RuntimeException("Unexpected tile count");
        };
    }

    @Override
    public String toString() {
        if (tiles.size() == 0) {
            if (firstPlayerMarkerPosition == -1) {
                return "Empty";
            }
            return "M";
        }
        StringBuilder result = new StringBuilder(
                tiles.stream().map(Tile::toString).collect(Collectors.joining(""))
        );
        if (firstPlayerMarkerPosition > -1) {
            result.insert(firstPlayerMarkerPosition, "M");
        }
        return String.join(" ", result.toString().split(""));
    }

    public List<String> jsonList() {
        List<String> jsonList = new ArrayList<>();
        for (Tile tile : tiles) {
            jsonList.add(tile.toString());
        }
        if (firstPlayerMarkerPosition > -1) {
            jsonList.add(firstPlayerMarkerPosition, "M");
        }
        return jsonList;
    }
}
