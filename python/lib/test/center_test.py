import unittest

from azul.center import Center
from azul.floor import Floor
from azul.game import Game
from azul.tile import Tile
from player_mother import PlayerMother


class TestCenter(unittest.TestCase):
    def test_player_taking_from_center_first_with_empty_floor_gets_penalty(self):
        floor = Floor()
        game = Game([PlayerMother().new_player(floor=floor), PlayerMother().new_player()], Center(), 1)
        game.execute_factory_offer_phase_with_factory(0, game.factory_displays[0].tiles[0], 0, 4)

        game.execute_factory_offer_phase_with_center(game.peek_center()[0], 0, 4)

        self.assertEqual(-1, floor.score())

    def test_player_taking_from_center_first_with_non_empty_floor_gets_penalty(self):
        floor = Floor()
        game = Game([PlayerMother().new_player(floor=floor), PlayerMother().new_player()], Center(), 1)
        game.execute_factory_offer_phase_with_factory(0, game.factory_displays[0].tiles[0], 0, 4)

        game.execute_factory_offer_phase_with_center(game.peek_center()[0], 1, 4)

        self.assertEqual(-2, floor.score())

    def test_player_taking_from_center_second_does_not_get_penalty(self):
        center = Center()
        floor1 = Floor()
        player1 = PlayerMother().new_player(floor=floor1)
        player2 = PlayerMother().new_player()
        game = Game([player1, player2], center, 0)
        game.change_factory_display(0, [Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW])
        game.execute_factory_offer_phase_with_factory(0, Tile.RED, 0, 4)
        player2.take_tiles_from_center(center, Tile.BLUE, 0, 4)

        player2.take_tiles_from_center(center, Tile.YELLOW, 0, 3)

        self.assertEqual(0, floor1.score())