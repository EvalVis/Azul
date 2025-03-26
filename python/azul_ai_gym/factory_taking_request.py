from azul_ai_gym.action_not_allowed_exception import ActionNotAllowedException
from azul_ai_gym.tile import Tile
from azul_ai_gym.game import Game


class FactoryTakingRequest:
    def __init__(self, factory_index, tile_to_take, tiles_to_put_on_floor, pattern_line_index):
        self.factory_index = factory_index
        self.tile_to_take = Tile(tile_to_take)
        self.tiles_to_put_on_floor = tiles_to_put_on_floor
        self.pattern_line_index = pattern_line_index

    def validate(self, game: Game):
        pattern_lines = game.players[game.current_player].board.pattern_lines
        wall = game.players[game.current_player].board.wall
        if self.factory_index < 0 or self.factory_index >= game.factory_display_count():
            raise ActionNotAllowedException(f"Factory index must be between 0 and {game.factory_display_count()}.")
        factory = game.factory_displays[self.factory_index]
        if factory.is_empty():
            raise ActionNotAllowedException(f"Factory with an index of {self.factory_index} is empty.")
        if not factory.tile_exist(self.tile_to_take):
            raise ActionNotAllowedException(f"{self.tile_to_take} tile is not on the factory display.")
        matching_tile_count = factory.count(self.tile_to_take)
        if self.tiles_to_put_on_floor < 0 or self.tiles_to_put_on_floor > matching_tile_count:
            raise ActionNotAllowedException("Tiles to put on floor count must be between 0 and tile taken count.")
        if self.tiles_to_put_on_floor == matching_tile_count:
            return
        if self.pattern_line_index < 0 or self.pattern_line_index >= 5:
            raise ActionNotAllowedException("Pattern line index must be between 0 and 5.")
        if pattern_lines[self.pattern_line_index].is_colour_invalid(self.tile_to_take):
            raise ActionNotAllowedException(f"{self.tile_to_take} tile colour is invalid to put on pattern line")
        if pattern_lines[self.pattern_line_index].is_pattern_line_going_to_overflow(matching_tile_count - self.tiles_to_put_on_floor):
            raise ActionNotAllowedException("Pattern line will overflow. Put more tiles on the floor.")
        if wall.already_has(self.tile_to_take, self.pattern_line_index):
            raise ActionNotAllowedException(f"Wall already has {self.tile_to_take} tile.")