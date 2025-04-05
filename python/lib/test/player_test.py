import unittest

from lib.azul.board import Board
from lib.azul.center import Center
from lib.azul.floor import Floor
from lib.azul.game import Game
from lib.azul.pattern_line import PatternLine
from lib.azul.player import Player
from lib.azul.tile import Tile
from lib.azul.wall import Wall


class TestPlayer(unittest.TestCase):
    def test_player_can_add_tiles_to_pattern_line_and_floor(self):
        floor = Floor()
        pattern_lines = self.pattern_lines()
        player = Player(Board(pattern_lines, Wall(), floor))
        game = Game([player, Player(Board(pattern_lines))], Center(), 0)
        game.change_factory_display(0, [Tile.RED, Tile.RED, Tile.RED, Tile.BLUE])

        game.execute_factory_offer_phase_with_factory(0, Tile.RED, 2, 2)

        self.assertEqual(1, pattern_lines[2].tile_count)
        self.assertEqual(-2, floor.score())

    @staticmethod
    def pattern_lines():
        return [PatternLine(1), PatternLine(2), PatternLine(3), PatternLine(4), PatternLine(5)]