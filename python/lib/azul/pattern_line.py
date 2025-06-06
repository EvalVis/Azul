from azul.action_not_allowed_exception import ActionNotAllowedException

class PatternLine:
    def __init__(self, size):
        self.size = size
        self.tile_count = 0
        self.tile = None

    def add(self, tile, count):
        if count < 1:
            return
        if self.is_colour_invalid(tile):
            raise ActionNotAllowedException(
                f"Tile(s) with {self.tile} colour is on the pattern line. Can't add a tile with {tile} colour."
            )
        self.tile = tile
        overfill = max(0, self.tile_count + count - self.size)
        self.tile_count = min(self.size, self.tile_count + count)
        return overfill

    def is_filled(self):
        return self.tile_count == self.size

    def clear(self):
        self.tile_count = 0
        self.tile = None

    def is_colour_invalid(self, tile):
        return self.tile_count > 0 and self.tile != tile

    def is_pattern_line_going_to_overflow(self, count):
        return self.tile_count + count > self.size

    def __str__(self):
        return (
            (f"{self.tile} " * self.tile_count if self.tile else "")
            + "E " * (self.size - self.tile_count)
        )

    def json_list(self):
        return [str(self.tile)] * self.tile_count if self.tile else []