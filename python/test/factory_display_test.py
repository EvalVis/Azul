import unittest

from flask import Flask

from src.center import Center
from src.tile import Tile
from game_mother import GameMother


class TestFactoryDisplay(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.app = Flask(__name__)
        cls.app_context = cls.app.app_context()
        cls.app_context.push()

    @classmethod
    def tearDownClass(cls):
        cls.app_context.pop()

    def test_leftover_tiles_are_pushed_to_center(self):
        center = Center()
        game = GameMother().new_2_player_game(center=center)
        game.change_factory_display(0, [Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW])

        game.execute_factory_offer_phase_with_factory(0, Tile.RED, 0, 0)

        self.assertEqual(0, center.count(Tile.RED))
        self.assertEqual(1, center.count(Tile.BLUE))
        self.assertEqual(1, center.count(Tile.YELLOW))