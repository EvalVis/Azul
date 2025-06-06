from azul.pattern_line import PatternLine
from azul.wall import Wall
from azul.floor import Floor
from azul.action_not_allowed_exception import ActionNotAllowedException

class Board:
    def __init__(self, pattern_lines=None, wall=None, floor=None):
        self.pattern_lines = pattern_lines if pattern_lines else [PatternLine(i) for i in range(1, 6)]
        self.wall = wall if wall else Wall()
        self.floor = floor if floor else Floor()

    def floor_penalty(self):
        return self.floor.score()

    def add_tile_to_pattern_line(self, tile, count, position):
        if count < 1:
            return
        if self.wall.already_has(tile, position):
            raise ActionNotAllowedException(f"Wall already contains tile(s) with {tile} colour.")
        self.floor.add(tile, self.pattern_lines[position].add(tile, count))

    def move_tiles_from_pattern_lines_to_wall(self, lid):
        score = 0
        for i, line in enumerate(self.pattern_lines):
            if line.is_filled():
                score += self.wall.add(line.tile, i)
                for _ in range(line.tile_count - 1):
                    lid.add_tile(line.tile)
                line.clear()
        return score

    def game_ending_score(self):
        return (
            self.wall.completed_horizontal_lines() * 2 +
            self.wall.completed_vertical_lines() * 7 +
            self.wall.completed_tiles() * 10
        )

    def add_tiles_to_floor_line(self, tile, amount):
        self.floor.add(tile, amount)

    def add_first_player_marker_to_floor_line(self):
        self.floor.add_first_player_marker()

    def clear_floor(self):
        self.floor.clear()

    def json_object(self):
        pattern_lines_json = [pattern_line.json_list() for pattern_line in self.pattern_lines]
        return {
            "Wall": self.wall.json_list(),
            "Floor": self.floor.json_list(),
            "Pattern lines": pattern_lines_json
        }

    def __str__(self):
        result = ["Board:", "Pattern lines:"]
        result.extend(str(pattern_line) for pattern_line in self.pattern_lines)
        result.append("Wall:")
        result.append(str(self.wall))
        result.append(f"Floor: {self.floor}")
        return "\n".join(result)
