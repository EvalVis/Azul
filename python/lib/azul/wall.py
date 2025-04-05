from azul.tile import Tile

class Wall:
    def __init__(self):
        self.tiles = [
            [WallTile(Tile.BLUE), WallTile(Tile.YELLOW), WallTile(Tile.RED), WallTile(Tile.BLACK), WallTile(Tile.WHITE)],
            [WallTile(Tile.WHITE), WallTile(Tile.BLUE), WallTile(Tile.YELLOW), WallTile(Tile.RED), WallTile(Tile.BLACK)],
            [WallTile(Tile.BLACK), WallTile(Tile.WHITE), WallTile(Tile.BLUE), WallTile(Tile.YELLOW), WallTile(Tile.RED)],
            [WallTile(Tile.RED), WallTile(Tile.BLACK), WallTile(Tile.WHITE), WallTile(Tile.BLUE), WallTile(Tile.YELLOW)],
            [WallTile(Tile.YELLOW), WallTile(Tile.RED), WallTile(Tile.BLACK), WallTile(Tile.WHITE), WallTile(Tile.BLUE)],
        ]

    def add(self, tile, y):
        x = (list(Tile).index(tile) + y) % 5
        self.tiles[y][x].is_placed = True
        return self.score(x, y)

    def score(self, x, y):
        score = 0
        horizontal_score = 0
        marker = x - 1
        while marker >= 0 and self.tiles[y][marker].is_placed:
            marker -= 1
            horizontal_score += 1
        marker = x + 1
        while marker < len(self.tiles[y]) and self.tiles[y][marker].is_placed:
            marker += 1
            horizontal_score += 1
        if horizontal_score > 0:
            score += 1
        score += horizontal_score
        vertical_score = 0
        marker = y - 1
        while marker >= 0 and self.tiles[marker][x].is_placed:
            marker -= 1
            vertical_score += 1
        marker = y + 1
        while marker < len(self.tiles) and self.tiles[marker][x].is_placed:
            marker += 1
            vertical_score += 1
        if vertical_score > 0:
            score += 1
        score += vertical_score
        if score == 0:
            score = 1
        return score

    def already_has(self, tile, y):
        return self.tiles[y][(list(Tile).index(tile) + y) % 5].is_placed

    def completed_horizontal_lines(self):
        return sum(1 for row in self.tiles if all(tile.is_placed for tile in row))

    def completed_vertical_lines(self):
        return sum(1 for col in range(len(self.tiles[0])) if all(row[col].is_placed for row in self.tiles))

    def completed_tiles(self):
        not_completed_tiles = {tile.tile for row in self.tiles for tile in row if not tile.is_placed}
        return len(Tile) - len(not_completed_tiles)

    def __str__(self):
        return "\n".join(" ".join(str(tile) for tile in row) for row in self.tiles)

    def json_list(self):
        return [[str(tile) for tile in row] for row in self.tiles]

class WallTile:
    def __init__(self, tile):
        self.tile = tile
        self.is_placed = False

    def __str__(self):
        return str(self.tile).upper() if self.is_placed else str(self.tile).lower()