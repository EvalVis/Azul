import unittest

from azul.board import Board
from azul.center import Center
from azul.game import Game
from azul.tile import Tile
from player_mother import PlayerMother


class TestGameStart(unittest.TestCase):
    def test_factory_displays_are_filled(self):
        center = Center()
        player1_board = Board()
        player2_board = Board()
        player3_board = Board()
        game = Game([PlayerMother().new_player(player1_board), PlayerMother().new_player(player2_board)], center)

        blue = sum(fd.give_tiles(Tile.BLUE) for fd in game.factory_displays)
        yellow = center.give_tiles(Tile.YELLOW, player2_board)
        red = center.give_tiles(Tile.RED, player3_board)
        black = center.give_tiles(Tile.BLACK, player1_board)
        white = center.give_tiles(Tile.WHITE, player2_board)
        self.assertEqual(100, blue + yellow + red + black + white + len(game.bag_tiles()))
        blue_from_bag = sum(1 for t in game.bag_tiles() if t == Tile.BLUE)
        yellow_from_bag = sum(1 for t in game.bag_tiles() if t == Tile.YELLOW)
        red_from_bag = sum(1 for t in game.bag_tiles() if t == Tile.RED)
        black_from_bag = sum(1 for t in game.bag_tiles() if t == Tile.BLACK)
        white_from_bag = sum(1 for t in game.bag_tiles() if t == Tile.WHITE)
        self.assertEqual(20, blue + blue_from_bag)
        self.assertEqual(20, yellow + yellow_from_bag)
        self.assertEqual(20, red + red_from_bag)
        self.assertEqual(20, black + black_from_bag)
        self.assertEqual(20, white + white_from_bag)

    def test_one_player_has_starting_marker(self):
        player1 = PlayerMother().new_player()
        player2 = PlayerMother().new_player()

        Game([player1, player2], Center(), 0)

        self.assertTrue(player1.starts_round and not player2.starts_round)

    def test_players_start_with_0_points(self):
        player1 = PlayerMother().new_player()
        player2 = PlayerMother().new_player()

        Game([player1, player2])

        self.assertEqual(0, player1.score)
        self.assertEqual(0, player2.score)