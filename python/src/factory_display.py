from src.tile import Tile

class FactoryDisplay:
    def __init__(self, center, tiles):
        self.center = center
        self.tiles = list(tiles)

    def give_tiles(self, tile):
        given_tiles = sum(1 for t in self.tiles if t == tile)
        for t in self.tiles:
            if t != tile:
                self.center.add_tile(t)
        self.tiles.clear()
        return given_tiles

    def get_tiles(self):
        return self.tiles

    def clear(self):
        self.tiles.clear()

    def __str__(self):
        return "Empty" if self.is_empty() else Tile.printed_tiles(self.tiles)

    def json_object(self):
        return Tile.grouped_tiles(self.tiles)

    def is_empty(self):
        return not self.tiles