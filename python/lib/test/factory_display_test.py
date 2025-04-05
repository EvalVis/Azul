import unittest

from game_mother import GameMother
from azul.center import Center
from azul.tile import Tile

class TestFactoryDisplay(unittest.TestCase):
    def test_leftover_tiles_are_pushed_to_center(self):
        center = Center()
        game = GameMother().new_2_player_game(center=center)
        game.change_factory_display(0, [Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW])

        game.execute_factory_offer_phase_with_factory(0, Tile.RED, 0, 0)

        self.assertEqual(0, center.count(Tile.RED))
        self.assertEqual(1, center.count(Tile.BLUE))
        self.assertEqual(1, center.count(Tile.YELLOW))