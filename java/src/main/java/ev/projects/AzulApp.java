package ev.projects;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class AzulApp {
    private static String[] args;

    public static void main(String[] args) {
        AzulApp.args = args;
        SpringApplication.run(AzulApp.class, args);
    }

    @Bean
    Game game() {
        Lid lid = new Lid();
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playerCount(); i++) {
            players.add(new Player(new Board(new Wall(), new Floor(lid)), "Player " + (i + 1)));
        }
        return new Game(players, new Center(), 0, lid);
    }

    private int playerCount() {
        if (args.length == 0) {
            throw new RuntimeException("Please provide a player count.");
        }
        try {
            int playerCount = Integer.parseInt(args[0]);
            if (playerCount < 2 || playerCount > 4) {
                throw new RuntimeException("Player count must be equal to 2, 3 or 4.");
            }
            return playerCount;
        } catch (NumberFormatException e) {
            throw new RuntimeException("Failed to read player count.");
        }
    }
}
