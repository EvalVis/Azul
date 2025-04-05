from azul.game import Game
from azul.center import Center
from player_mother import PlayerMother

class GameMother:
    @staticmethod
    def new_2_player_game(center=None, player1=None):
        if center is None:
            center = Center()
        if player1 is None:
            player1 = PlayerMother().new_player()
        player2 = PlayerMother().new_player()
        return Game([player1, player2], center)