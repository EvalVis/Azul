package ev.projects;

public record FactoryTakingRequest(int factoryIndex, Tile tileToTake, int tilesToPutOnFloor, int patternLineIndex) {
}
