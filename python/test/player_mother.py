from src.board import Board
from src.floor import Floor
from src.wall import Wall
from src.player import Player

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
