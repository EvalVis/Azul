package ev.projects;

import java.util.List;

public enum Tile {
    BLUE("B"), YELLOW("Y"), RED("R"), BLACK("K"), WHITE("W");

    private final String displayName;

    Tile(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static String count(List<Tile> tiles) {
        int[] tileCount = new int[Tile.values().length];
        for (Tile tile : tiles) {
            tileCount[tile.ordinal()]++;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < tileCount.length; i++) {
            if (tileCount[i] > 0) {
                if(tileCount[i] == 1) {
                    result.append(Tile.values()[i].toString());
                }
                else {
                    result.append(tileCount[i]).append(Tile.values()[i].toString());
                }
                result.append(" ");
            }
        }
        if(!result.isEmpty()) {
            return result.substring(0, result.length() - 1);
        }
        return result.toString();
    }
}
