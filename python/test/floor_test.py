import unittest

from flask import Flask

from azul_ai_gym.floor import Floor
from azul_ai_gym.game import Game
from azul_ai_gym.center import Center
from azul_ai_gym.tile import Tile
from azul_ai_gym.lid import Lid
from azul_ai_gym.factory_display import FactoryDisplay
from player_mother import PlayerMother
from game_mother import GameMother


class TestFloor(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.app = Flask(__name__)
        cls.app_context = cls.app.app_context()
        cls.app_context.push()

    @classmethod
    def tearDownClass(cls):
        cls.app_context.pop()

    def test_player_can_add_tiles_to_floor_line(self):
        floor = Floor()
        player = PlayerMother().new_player(floor=floor)
        game = Game([player, PlayerMother().new_player()], Center(), 0)
        game.change_factory_display(0, [Tile.RED, Tile.RED, Tile.BLUE, Tile.YELLOW])

        player.take_tiles_from_factory(
            FactoryDisplay(Center(), [Tile.RED, Tile.RED, Tile.BLUE, Tile.BLUE]), Tile.RED, 2, 4
        )

        self.assertEqual(-2, floor.score())

    def test_floor_penalty_cant_make_player_score_negative(self):
        floor = Floor()
        floor.add(Tile.RED, 3)
        player = PlayerMother().new_player(floor=floor)
        player.add_score(3)
        game = GameMother.new_2_player_game(player1=player)

        game.execute_wall_tiling_phase()

        self.assertEqual(0, player.score)

    def test_overflown_floor_tiles_go_to_lid(self):
        lid = Lid()
        floor = Floor(lid=lid)
        floor.add(Tile.RED, 8)
        floor.add(Tile.BLUE, 1)

        self.assertListEqual([Tile.RED, Tile.BLUE], list(lid.tiles))