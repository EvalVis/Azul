from azul.tile import Tile

class Lid:
    def __init__(self):
        self.tiles = []

    def add_tile(self, addition):
        self.tiles.append(addition)

    def tiles(self):
        return self.tiles

    def give_tiles(self):
        tiles_to_give = self.tiles
        self.tiles = []
        return tiles_to_give

    def __str__(self):
        return Tile.printed_tiles(self.tiles)

    def json_object(self):
        return Tile.grouped_tiles(self.tiles)