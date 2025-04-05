from azul.action_not_allowed_exception import ActionNotAllowedException

class Player:
    def __init__(self, board, name="Erwin"):
        self.board = board
        self.score = 0
        self.name = name
        self.starts_round = False

    def take_tiles_from_factory(self, factory_display, tile_to_take, tiles_to_place_on_floor, pattern_line_index):
        self.place_tiles_on_floor_and_pattern_line(
            factory_display.give_tiles(tile_to_take), tile_to_take, tiles_to_place_on_floor, pattern_line_index
        )

    def take_tiles_from_center(self, center, tile_to_take, tiles_to_place_on_floor, pattern_line_index):
        self.place_tiles_on_floor_and_pattern_line(
            center.give_tiles(tile_to_take, self.board), tile_to_take, tiles_to_place_on_floor, pattern_line_index
        )

    def place_tiles_on_floor_and_pattern_line(self, tile_count, tile_to_place, tiles_to_place_on_floor, pattern_line_index):
        if tile_count < tiles_to_place_on_floor:
            raise ActionNotAllowedException("You can't place more tiles on the floor than you have.")
        tile_count -= tiles_to_place_on_floor
        self.board.add_tiles_to_floor_line(tile_to_place, tiles_to_place_on_floor)
        self.board.add_tile_to_pattern_line(tile_to_place, tile_count, pattern_line_index)

    def give_floor_penalty(self):
        penalized_score = self.score + self.board.floor_penalty()
        self.score = penalized_score if penalized_score >= 0 else 0

    def assign_game_ending_score(self):
        self.score += self.board.game_ending_score()

    def move_tiles_to_wall(self, lid):
        self.score += self.board.move_tiles_from_pattern_lines_to_wall(lid)

    def add_score(self, score):
        self.score += score

    def give_starting_marker(self):
        self.starts_round = True

    def starts_round(self):
        return self.starts_round

    def __str__(self):
        result = f"{self.name}:\nScore: {self.score}"
        if self.starts_round:
            result += "\nHas the starting player token."
        result += f"\n{self.board}"
        return result

    def json_object(self):
        return {
            "Name": self.name,
            "Score": self.score,
            "startsRound": self.starts_round,
            "Board": self.board.json_object()
        }