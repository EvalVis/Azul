package ev.projects;

public class PlayerMother {

    Player newPlayer() {
        return newPlayer(new Floor());
    }

    Player newPlayer(String name) {
        return new Player(new Board(), name);
    }

    Player newPlayer(Floor floor) {
        return newPlayer(new Wall(), floor);
    }

    Player newPlayer(Wall wall) {
        return newPlayer(wall, new Floor());
    }

    Player newPlayer(Wall wall, String name) {
        return new Player(new Board(wall, new Floor()), name);
    }

    Player newPlayer(Wall wall, Floor floor) {
        return new Player(new Board(wall, floor));
    }
}
