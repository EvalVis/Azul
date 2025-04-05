import unittest

from game_mother import GameMother
from azul.game import Game
from player_mother import PlayerMother
from wall_mother import WallMother


class TestGameEnding(unittest.TestCase):
    def test_player_scores_for_completed_horizontal_lines(self):
        player = PlayerMother().new_player(wall=WallMother().with_completed_two_horizontal_lines())
        game = GameMother.new_2_player_game(player1=player)
        current_score = player.score

        game.execute_game_ending_phase()

        self.assertEqual(4, player.score - current_score)

    def test_player_scores_for_completed_vertical_lines(self):
        player = PlayerMother().new_player(wall=WallMother().with_three_completed_vertical_lines())
        game = GameMother.new_2_player_game(player1=player)
        current_score = player.score

        game.execute_game_ending_phase()

        self.assertEqual(21, player.score - current_score)

    def test_player_scores_for_completed_tiles(self):
        player = PlayerMother().new_player(wall=WallMother().with_completed_blue_and_yellow_tiles())
        game = GameMother.new_2_player_game(player1=player)
        current_score = player.score

        game.execute_game_ending_phase()

        self.assertEqual(20, player.score - current_score)

    def test_winner_is_declared(self):
        player1 = PlayerMother().new_player(name="Joke")
        player1.add_score(10)
        player2 = PlayerMother().new_player(name="Alfonso")
        player2.add_score(20)
        player3 = PlayerMother().new_player(name="Ra")
        player3.add_score(15)
        game = Game([player1, player2, player3])

        winner = game.winners()

        self.assertEqual(1, len(winner))
        self.assertEqual("Alfonso", winner[0])

    def test_resolves_draw_with_horizontal_lines(self):
        player1 = PlayerMother().new_player(wall=WallMother().with_completed_two_horizontal_lines(), name="Joke")
        player1.add_score(20)
        player2 = PlayerMother().new_player(wall=WallMother().with_completed_horizontal_line(), name="Alfonso")
        player2.add_score(20)
        game = Game([player1, player2])

        winner = game.winners()

        self.assertEqual(1, len(winner))
        self.assertEqual("Joke", winner[0])

    def test_both_players_win_if_drawn_and_same_amount_of_completed_horizontal_lines(self):
        player1 = PlayerMother().new_player(wall=WallMother().with_completed_two_horizontal_lines(), name="Joke")
        player1.add_score(20)
        player2 = PlayerMother().new_player(wall=WallMother().with_completed_two_horizontal_lines(), name="Alfonso")
        player2.add_score(20)
        game = Game([player1, player2])

        winners = game.winners()

        self.assertEqual(2, len(winners))