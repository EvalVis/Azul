from enum import Enum
from collections import Counter

class Tile(Enum):
    BLUE = "B"
    YELLOW = "Y"
    RED = "R"
    BLACK = "K"
    WHITE = "W"

    def __str__(self):
        return self.value

    @staticmethod
    def printed_tiles(tiles):
        tile_count = Counter(tiles)
        result = []
        for tile, count in tile_count.items():
            if count == 1:
                result.append(str(tile))
            else:
                result.append(f"{count}{tile}")
        return " ".join(result)

    @staticmethod
    def grouped_tiles(tiles):
        return {str(tile): count for tile, count in Counter(tiles).items()}