package ev.projects;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/takeFromFactory")
    public String takeTilesFromFactory(FactoryTakingRequest factoryTakingRequest) {
        game.executeFactoryOfferPhaseWithFactory(
                factoryTakingRequest.factoryIndex(), factoryTakingRequest.tileToTake(),
                factoryTakingRequest.tilesToPutOnFloor(), factoryTakingRequest.patternLineIndex()
        );
        return game.toString();
    }

    @PostMapping("/takeFromCenter")
    public String takeTilesFromCenter(CenterTakingRequest centerTakingRequest) {
        game.executeFactoryOfferPhaseWithCenter(
                centerTakingRequest.tileToTake(), centerTakingRequest.tilesToPutOnFloor(),
                centerTakingRequest.patternLineIndex()
        );
        return game.toString();
    }


}
