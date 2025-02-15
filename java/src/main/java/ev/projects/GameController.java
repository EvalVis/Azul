package ev.projects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GameController {
    private final Game game;

    public GameController(Game game) {
        this.game = game;
    }

    @GetMapping("/show")
    public String show() {
        return game.toString();
    }

    @GetMapping("/showJson")
    public Map<String, Object> showJson() {
        return game.jsonObject();
    }

    @PostMapping("/takeFromFactory")
    public Map<String, Object> takeTilesFromFactory(@RequestBody FactoryTakingRequest factoryTakingRequest) {
        game.executeFactoryOfferPhaseWithFactory(
                factoryTakingRequest.factoryIndex(), factoryTakingRequest.tileToTake(),
                factoryTakingRequest.tilesToPutOnFloor(), factoryTakingRequest.patternLineIndex()
        );
        return game.jsonObject();
    }

    @PostMapping("/takeFromCenter")
    public Map<String, Object> takeTilesFromCenter(@RequestBody CenterTakingRequest centerTakingRequest) {
        game.executeFactoryOfferPhaseWithCenter(
                centerTakingRequest.tileToTake(), centerTakingRequest.tilesToPutOnFloor(),
                centerTakingRequest.patternLineIndex()
        );
        return game.jsonObject();
    }


}
