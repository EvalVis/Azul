import unittest

from azul.action_not_allowed_exception import ActionNotAllowedException
from azul.board import Board
from azul.center import Center
from azul.factory_display import FactoryDisplay
from azul.floor import Floor
from azul.lid import Lid
from azul.pattern_line import PatternLine
from azul.player import Player
from azul.tile import Tile
from azul.wall import Wall


class TestPatternLine(unittest.TestCase):
    def test_player_fills_pattern_line(self):
        pattern_lines = TestPatternLine.pattern_lines()
        pattern_lines[4].add(Tile.RED, 2)
        player = Player(Board(pattern_lines))

        player.take_tiles_from_factory(
            FactoryDisplay(Center(), [Tile.RED, Tile.RED, Tile.RED, Tile.BLUE]), Tile.RED, 0, 4
        )

        self.assertTrue(pattern_lines[4].is_filled())

    def test_player_adds_to_pattern_line(self):
        pattern_lines = TestPatternLine.pattern_lines()
        pattern_lines[4].add(Tile.RED, 2)
        player = Player(Board(pattern_lines))

        player.take_tiles_from_factory(
            FactoryDisplay(Center(), [Tile.RED, Tile.RED, Tile.BLUE, Tile.BLUE]), Tile.RED, 0, 4
        )

        self.assertFalse(pattern_lines[4].is_filled())

    def test_on_filled_pattern_line_tile_is_placed_in_correct_wall_position(self):
        wall = Wall()
        pattern_lines = TestPatternLine.pattern_lines()
        board = Board(pattern_lines, wall, Floor())

        pattern_lines[2].add(Tile.WHITE, 3)
        board.move_tiles_from_pattern_lines_to_wall(Lid())

        self.assertTrue(wall.already_has(Tile.WHITE, 2))

        for color in [Tile.WHITE, Tile.BLUE, Tile.YELLOW, Tile.RED, Tile.BLACK]:
            for pos in range(5):
                if color == Tile.WHITE and pos == 2:
                    continue
                self.assertFalse(wall.already_has(color, pos))

    def test_player_overfills_pattern_line(self):
        floor = Floor()
        pattern_lines = TestPatternLine.pattern_lines()
        board = Board(pattern_lines, Wall(), floor)
        board.add_tile_to_pattern_line(Tile.RED, 2, 2)
        player = Player(board)

        player.take_tiles_from_factory(
            FactoryDisplay(Center(), [Tile.RED, Tile.RED, Tile.RED, Tile.RED]), Tile.RED, 0, 2
        )

        self.assertTrue(pattern_lines[2].is_filled())
        self.assertEqual(-4, floor.score())

    def test_cant_add_tile_of_different_colour(self):
        wall = Wall()
        pattern_lines = TestPatternLine.pattern_lines()
        pattern_lines[4].add(Tile.RED, 2)
        player = Player(Board(pattern_lines, wall, Floor()))

        with self.assertRaises(ActionNotAllowedException):
            player.take_tiles_from_factory(
                FactoryDisplay(Center(), [Tile.RED, Tile.RED, Tile.BLUE, Tile.BLUE]), Tile.BLUE, 0, 4
            )

    @staticmethod
    def pattern_lines():
        return [PatternLine(1), PatternLine(2), PatternLine(3), PatternLine(4), PatternLine(5)]