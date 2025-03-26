from azul_ai_gym.board import Board
from azul_ai_gym.floor import Floor
from azul_ai_gym.wall import Wall
from azul_ai_gym.player import Player

class PlayerMother:
    @staticmethod
    def new_player(name=None, wall=None, floor=None):
        if floor is None:
            floor = Floor()
        if wall is None:
            wall = Wall()
        board = Board(wall=wall, floor=floor)
        if name:
            return Player(board, name)
        return Player(board)
