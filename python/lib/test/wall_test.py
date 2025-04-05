import unittest

from azul.action_not_allowed_exception import ActionNotAllowedException
from azul.board import Board
from azul.center import Center
from azul.factory_display import FactoryDisplay
from azul.floor import Floor
from azul.game import Game
from azul.lid import Lid
from azul.pattern_line import PatternLine
from azul.player import Player
from azul.tile import Tile
from azul.wall import Wall
from player_mother import PlayerMother


class TestWall(unittest.TestCase):

    def test_tile_gets_placed_on_wall_if_pattern_line_is_filled(self):
        wall = Wall()
        pattern_lines = self.pattern_lines()
        pattern_lines[4].add(Tile.RED, 5)
        game = Game([Player(Board(pattern_lines, wall, Floor()))])

        game.execute_wall_tiling_phase()

        self.assertTrue(wall.already_has(Tile.RED, 4))
        self.assertEqual(0, pattern_lines[4].tile_count)

    def test_tile_does_not_get_placed_on_wall_if_pattern_line_is_not_filled(self):
        wall = Wall()
        pattern_lines = self.pattern_lines()
        pattern_lines[4].add(Tile.RED, 4)
        game = Game([Player(Board(pattern_lines, wall, Floor()))])

        game.execute_wall_tiling_phase()

        self.assertFalse(wall.already_has(Tile.RED, 4))
        self.assertEqual(4, pattern_lines[4].tile_count)

    def test_cant_add_colour_to_pattern_line_if_wall_has_that_colour(self):
        wall = Wall()
        player = PlayerMother.new_player(wall=wall)
        wall.add(Tile.RED, 4)

        with self.assertRaises(ActionNotAllowedException):
            player.take_tiles_from_factory(
                FactoryDisplay(Center(), [Tile.RED, Tile.RED, Tile.RED, Tile.BLUE]), Tile.RED, 0, 4
            )

    def test_player_scores_a_point_when_placing_tile_on_empty_wall(self):
        wall = Wall()
        pattern_lines = self.pattern_lines()
        pattern_lines[0].add(Tile.RED, 1)
        player = Player(Board(pattern_lines, wall, Floor()))

        player.move_tiles_to_wall(Lid())

        self.assertEqual(1, player.score)

    def test_player_scores_points_when_placing_tile_on_wall(self):
        wall = Wall()
        pattern_lines = self.pattern_lines()
        pattern_lines[2].add(Tile.YELLOW, 3)
        player = Player(Board(pattern_lines, wall, Floor()))
        wall.add(Tile.BLACK, 2)
        wall.add(Tile.WHITE, 2)
        wall.add(Tile.BLUE, 2)
        wall.add(Tile.BLACK, 0)
        wall.add(Tile.RED, 1)
        current_score = player.score

        player.move_tiles_to_wall(Lid())

        self.assertEqual(7, player.score - current_score)

    def test_player_scores_a_point_when_placing_tile_on_wall(self):
        wall = Wall()
        pattern_lines = self.pattern_lines()
        pattern_lines[2].add(Tile.YELLOW, 3)
        player = Player(Board(pattern_lines, wall, Floor()))
        wall.add(Tile.BLACK, 2)
        wall.add(Tile.WHITE, 2)
        wall.add(Tile.BLACK, 0)
        current_score = player.score

        player.move_tiles_to_wall(Lid())

        self.assertEqual(1, player.score - current_score)

    @staticmethod
    def pattern_lines():
        return [PatternLine(1), PatternLine(2), PatternLine(3), PatternLine(4), PatternLine(5)]
