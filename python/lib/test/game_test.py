import unittest

from azul.bag import Bag
from azul.board import Board
from azul.center import Center
from azul.floor import Floor
from azul.game import Game
from azul.lid import Lid
from azul.pattern_line import PatternLine
from azul.player import Player
from azul.tile import Tile
from azul.wall import Wall
from player_mother import PlayerMother
from wall_mother import WallMother


class TestGame(unittest.TestCase):
    def test_game_ending_executes_on_filled_horizontal_line(self):
        center = Center()
        center.add_tile(Tile(Tile.BLUE))
        player = PlayerMother().new_player(wall=WallMother.with_almost_completed_horizontal_line())
        game = Game([player, PlayerMother().new_player()], center, 0)
        game.clear_factory_displays()

        game.execute_factory_offer_phase_with_center(Tile(Tile.BLUE), 0, 0)

        self.assertEqual(6, player.score)

    def test_wall_tiling_executes_on_empty_factory_displays_and_center(self):
        center = Center()
        center.add_tile(Tile.BLUE)
        pattern_lines = self.pattern_lines()
        player = Player(Board(pattern_lines))
        game = Game([player, PlayerMother().new_player()], center, 0)
        game.clear_factory_displays()

        game.execute_factory_offer_phase_with_center(Tile(Tile.BLUE), 0, 0)

        self.assertFalse(pattern_lines[0].is_filled())

    def test_player_takes_and_places_factory_tiles(self):
        pattern_lines = self.pattern_lines()
        floor = Floor()
        player = Player(Board(pattern_lines, Wall(), floor))
        game = Game([player, PlayerMother().new_player()], Center(), 0)
        game.change_factory_display(0, [Tile.RED, Tile.RED, Tile.RED, Tile.RED])

        game.execute_factory_offer_phase_with_factory(0, Tile.RED, 2, 1)

        self.assertEqual(-2, floor.score())
        self.assertEqual("R R", str(floor))
        self.assertEqual(2, pattern_lines[1].tile_count)
        self.assertEqual(Tile.RED, pattern_lines[1].tile)

    def test_player_takes_and_places_center_tiles(self):
        pattern_lines = self.pattern_lines()
        floor = Floor()
        player = Player(Board(pattern_lines, Wall(), floor))
        center = Center()
        game = Game([player, PlayerMother().new_player()], center, 1)
        game.change_factory_display(0, [Tile.RED, Tile.RED, Tile.YELLOW, Tile.BLACK])
        game.execute_factory_offer_phase_with_factory(0, Tile.YELLOW, 1, 3)

        game.execute_factory_offer_phase_with_center(Tile.RED, 1, 0)

        self.assertEqual(-2, floor.score())
        self.assertEqual("M R", str(floor))
        self.assertEqual(1, pattern_lines[0].tile_count)
        self.assertEqual(Tile.RED, pattern_lines[0].tile)
        self.assertEqual("K", str(center))

    def test_players_take_tiles_in_order(self):
        center = Center()
        pattern_lines1 = self.pattern_lines()
        player1 = Player(Board(pattern_lines1))
        pattern_lines2 = self.pattern_lines()
        player2 = Player(Board(pattern_lines2))
        game = Game([player1, player2], center, 0)

        game.execute_factory_offer_phase_with_factory(0, game.factory_displays[0].tiles[0], 0, 0)
        self.assertTrue(pattern_lines1[0].tile_count > 0)

        game.execute_factory_offer_phase_with_factory(1, game.factory_displays[1].tiles[0], 0, 0)
        self.assertTrue(pattern_lines2[0].tile_count > 0)

        game.execute_factory_offer_phase_with_factory(2, game.factory_displays[2].tiles[0], 0, 1)
        self.assertTrue(pattern_lines1[1].tile_count > 0)

        game.execute_factory_offer_phase_with_factory(3, game.factory_displays[3].tiles[0], 0, 1)
        self.assertTrue(pattern_lines2[1].tile_count > 0)

    def test_game_is_displayed(self):
        lid = Lid()
        player1 = PlayerMother().new_player(wall=Wall(), floor=Floor(lid), name="Robert")
        wall2 = Wall()
        floor2 = Floor(lid)
        player2 = PlayerMother().new_player(wall=wall2, floor=floor2, name="Roger")
        center = Center()
        game = Game([player1, player2], center, 0, lid)
        game.change_factory_display(0, [Tile.RED, Tile.RED, Tile.RED, Tile.BLUE])
        game.change_factory_display(1, [Tile.RED, Tile.RED, Tile.YELLOW, Tile.BLUE])
        game.change_factory_display(2, [Tile.YELLOW, Tile.YELLOW, Tile.YELLOW, Tile.YELLOW])
        game.change_factory_display(3, [Tile.WHITE, Tile.WHITE, Tile.WHITE, Tile.WHITE])
        game.change_factory_display(4, [Tile.BLACK, Tile.WHITE, Tile.WHITE, Tile.YELLOW])
        game.set_bag(Bag(self.init_tiles_in_bag([18, 14, 15, 19, 14])))
        game.execute_factory_offer_phase_with_factory(4, Tile.WHITE, 1, 2)
        floor2.add(Tile.RED, 1)
        floor2.add(Tile.YELLOW, 2)
        player2.take_tiles_from_center(center, Tile.YELLOW, 0, 0)
        wall2.add(Tile.BLUE, 2)
        game.execute_wall_tiling_phase()

        json_object = game.json_object()

        self.assertEqual("{'W': 1, 'R': 1, 'Y': 2}", str(json_object["Lid"]))
        self.assertTrue("'Pattern lines': [[], [], ['W'], [], []]" in str(json_object["Players"]))
        self.assertTrue(
            "'Wall': [['b', 'Y', 'r', 'k', 'w'], ['w', 'b', 'y', 'r', 'k'], ['k', 'w', 'B', 'y', 'r'], ['r', 'k', 'w', 'b', 'y'], ['y', 'r', 'k', 'w', 'b']]"
                in str(json_object["Players"])
        )

    @staticmethod
    def init_tiles_in_bag(amounts):
        tiles = []
        for i, count in enumerate(amounts):
            tiles.extend([Tile(list(Tile)[i])] * count)
        return tiles

    @staticmethod
    def pattern_lines():
        return [PatternLine(1), PatternLine(2), PatternLine(3), PatternLine(4), PatternLine(5)]