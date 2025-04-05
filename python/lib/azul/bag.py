import random
from azul.tile import Tile

class Bag:
    def __init__(self, tiles=None):
        if tiles is None:
            tiles = self.init_tiles()
        self.tiles = tiles

    @staticmethod
    def init_tiles():
        result = []
        for tile_value in Tile:
            result.extend([tile_value] * 20)
        return result

    def take_tiles(self, amount, lid):
        taken_tiles = []
        if amount > len(self.tiles):
            self.tiles.extend(lid.give_tiles())
        for _ in range(min(len(self.tiles), amount)):
            taken_tiles.append(self.tiles.pop(random.randint(0, len(self.tiles) - 1)))
        return taken_tiles

    def tiles(self):
        return self.tiles

    def __str__(self):
        if len(self.tiles) == 0:
            return "Empty"
        return Tile.printed_tiles(self.tiles)

    def json_object(self):
        return Tile.grouped_tiles(self.tiles)