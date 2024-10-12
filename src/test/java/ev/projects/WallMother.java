package ev.projects;

public class WallMother {
    Wall withCompletedTwoHorizontalLines() {
        Wall wall = withCompletedHorizontalLine();
        wall.add(Tile.WHITE, 1);
        wall.add(Tile.BLUE, 1);
        wall.add(Tile.YELLOW, 1);
        wall.add(Tile.RED, 1);
        wall.add(Tile.BLACK, 1);
        return wall;
    }

    Wall withCompletedHorizontalLine() {
        Wall wall = withAlmostCompletedHorizontalLine();
        wall.add(Tile.BLUE, 0);
        return wall;
    }

    Wall withAlmostCompletedHorizontalLine() {
        Wall wall = new Wall();
        wall.add(Tile.YELLOW, 0);
        wall.add(Tile.RED, 0);
        wall.add(Tile.BLACK, 0);
        wall.add(Tile.WHITE, 0);
        return wall;
    }

    Wall withThreeCompletedVerticalLines() {
        Wall wall = new Wall();
        wall.add(Tile.BLUE, 0);
        wall.add(Tile.WHITE, 1);
        wall.add(Tile.BLACK, 2);
        wall.add(Tile.RED, 3);
        wall.add(Tile.YELLOW, 4);
        wall.add(Tile.YELLOW, 0);
        wall.add(Tile.BLUE, 1);
        wall.add(Tile.WHITE, 2);
        wall.add(Tile.BLACK, 3);
        wall.add(Tile.RED, 4);
        wall.add(Tile.RED, 0);
        wall.add(Tile.YELLOW, 1);
        wall.add(Tile.BLUE, 2);
        wall.add(Tile.WHITE, 3);
        wall.add(Tile.BLACK, 4);
        return wall;
    }

    Wall withCompletedBlueAndYellowTiles() {
        Wall wall = new Wall();
        wall.add(Tile.BLUE, 0);
        wall.add(Tile.BLUE, 1);
        wall.add(Tile.BLUE, 2);
        wall.add(Tile.BLUE, 3);
        wall.add(Tile.BLUE, 4);
        wall.add(Tile.YELLOW, 0);
        wall.add(Tile.YELLOW, 1);
        wall.add(Tile.YELLOW, 2);
        wall.add(Tile.YELLOW, 3);
        wall.add(Tile.YELLOW, 4);
        return wall;
    }
}
