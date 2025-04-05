from azul.tile import Tile

class FactoryDisplay:
    def __init__(self, center, tiles):
        self.center = center
        self.tiles = list(tiles)

    def give_tiles(self, tile):
        given_tiles = self.count(tile)
        for t in self.tiles:
            if t != tile:
                self.center.add_tile(t)
        self.tiles.clear()
        return given_tiles

    def tile_exist(self, tile):
        return tile in self.tiles

    def count(self, tile):
        return sum(1 for t in self.tiles if t == tile)

    def clear(self):
        self.tiles.clear()

    def __str__(self):
        return "Empty" if self.is_empty() else Tile.printed_tiles(self.tiles)

    def json_object(self):
        return Tile.grouped_tiles(self.tiles)

    def is_empty(self):
        return not self.tiles