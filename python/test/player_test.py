import unittest

from flask import Flask

from src.floor import Floor
from src.pattern_line import PatternLine
from src.board import Board
from src.wall import Wall
from src.player import Player
from src.game import Game
from src.tile import Tile
from src.center import Center

class TestPlayer(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.app = Flask(__name__)
        cls.app_context = cls.app.app_context()
        cls.app_context.push()

    @classmethod
    def tearDownClass(cls):
        cls.app_context.pop()

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