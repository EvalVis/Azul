package ev.projects;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class AzulApp {
    public static void main(String[] args) {
        SpringApplication.run(AzulApp.class, args);
    }

    @Bean
    Game game() {
        Lid lid = new Lid();
        return new Game(
                List.of(
                        new Player(new Board(new Wall(), new Floor(lid)), "Player 1"),
                        new Player(new Board(new Wall(), new Floor(lid)), "Player 2")
                ), new Center(), 0, lid
        );
    }
}
