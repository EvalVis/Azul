package ev.projects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlayerController {
    @GetMapping("/ping")
    public String ping() {
        return "Pong!";
    }
}
