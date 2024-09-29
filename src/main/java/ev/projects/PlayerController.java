package ev.projects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {
    private final Game game;

    public PlayerController(Game game) {
        this.game = game;
    }

    @GetMapping("/show")
    public String show() {
        return game.toString();
    }
}
