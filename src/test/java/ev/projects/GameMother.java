package ev.projects;

import java.util.List;

public class GameMother {

    Game new2PlayerGame(Center center) {
        return new2PlayerGame(new PlayerMother().newPlayer(), center);
    }

    Game new2PlayerGame(Player player1) {
        return new2PlayerGame(player1, new Center());
    }

    Game new2PlayerGame(Player player1, Center center) {
        return new Game(List.of(player1, new PlayerMother().newPlayer()), center);
    }
}
