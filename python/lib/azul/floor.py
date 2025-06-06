from azul.lid import Lid

class Floor:
    def __init__(self, lid=None):
        if lid is None:
            lid = Lid()
        self.tiles = []
        self.first_player_marker_position = -1
        self.lid = lid

    def clear(self):
        for tile in self.tiles:
            self.lid.add_tile(tile)
        self.tiles.clear()
        self.first_player_marker_position = -1

    def add_first_player_marker(self):
        self.first_player_marker_position = len(self.tiles)

    def add(self, tile, amount):
        if amount < 1:
            return
        tile_count_on_floor = self.tile_count_on_floor()
        overfill = tile_count_on_floor + amount - 7
        for _ in range(overfill):
            self.lid.add_tile(tile)
        amount_to_fill = min(amount, 7 - tile_count_on_floor)
        for _ in range(amount_to_fill):
            self.tiles.append(tile)

    def score(self):
        return {
            0: 0,
            1: -1,
            2: -2,
            3: -4,
            4: -6,
            5: -8,
            6: -11,
            7: -14,
        }.get(self.tile_count_on_floor(), RuntimeError("Unexpected tile count"))

    def tile_count_on_floor(self):
        return len(self.tiles) + 1 if (self.first_player_marker_position > -1 and len(self.tiles) < 7) else len(self.tiles)

    def __str__(self):
        if len(self.tiles) == 0:
            return "Empty" if self.first_player_marker_position == -1 else "M"
        result = "".join(str(tile) for tile in self.tiles)
        if self.first_player_marker_position > -1:
            result = result[:self.first_player_marker_position] + "M" + result[self.first_player_marker_position:]
        return " ".join(result)

    def json_list(self):
        json_list = [str(tile) for tile in self.tiles]
        if self.first_player_marker_position > -1:
            json_list.insert(self.first_player_marker_position, "M")
        return json_list