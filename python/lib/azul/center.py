from azul.tile import Tile

class Center:
    def __init__(self):
        self.tiles = []
        self.nobody_has_taken_from_center = True

    def add_tile(self, tile):
        self.tiles.append(tile)

    def give_tiles(self, tile, board):
        tile_count = sum(1 for t in self.tiles if t == tile)
        self.tiles = [t for t in self.tiles if t != tile]
        if self.nobody_has_taken_from_center:
            self.nobody_has_taken_from_center = False
            board.add_first_player_marker_to_floor_line()
        return tile_count

    def count(self, tile):
        return sum(1 for t in self.tiles if t == tile)

    def is_empty(self):
        return len(self.tiles) == 0

    def tile_exist(self, tile):
        return tile in self.tiles

    def __str__(self):
        if self.is_empty():
            return "Empty"
        return Tile.printed_tiles(self.tiles)

    def json_object(self):
        return Tile.grouped_tiles(self.tiles)