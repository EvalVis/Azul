from azul.action_not_allowed_exception import ActionNotAllowedException
from azul.tile import Tile
from azul.game import Game


class CenterTakingRequest:
    def __init__(self, tile_to_take, tiles_to_put_on_floor, pattern_line_index):
        self.tile_to_take = Tile(tile_to_take)
        self.tiles_to_put_on_floor = tiles_to_put_on_floor
        self.pattern_line_index = pattern_line_index

    def validate(self, game: Game):
        center = game.center
        pattern_lines = game.players[game.current_player].board.pattern_lines
        wall = game.players[game.current_player].board.wall
        if center.is_empty():
            raise ActionNotAllowedException("Can't take tile(s) from an empty center.")
        if not center.tile_exist(self.tile_to_take):
            raise ActionNotAllowedException(f"{self.tile_to_take} tile you want to take is not in the center.")
        matching_tile_count = center.count(self.tile_to_take)
        if self.tiles_to_put_on_floor < 0 or self.tiles_to_put_on_floor > matching_tile_count:
            raise ActionNotAllowedException(f"Tiles to put on floor count must be between 0 and tile taken count ({matching_tile_count} in this case).")
        if self.tiles_to_put_on_floor == matching_tile_count:
            return
        if self.pattern_line_index < 0 or self.pattern_line_index >= 5:
            raise ActionNotAllowedException("Pattern line index must be between 0 and 5")
        if pattern_lines[self.pattern_line_index].is_colour_invalid(self.tile_to_take):
            raise ActionNotAllowedException(f"{self.tile_to_take} tile colour is invalid to put on pattern line")
        if wall.already_has(self.tile_to_take, self.pattern_line_index):
            raise ActionNotAllowedException(f"Wall already has {self.tile_to_take} tile.")

