package ev.projects;

public class Floor {
    private int tiles;

    void add(int amount) {
        tiles = Math.min(tiles + amount, 7);
    }

    int score() {
        return switch (tiles) {
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
}
