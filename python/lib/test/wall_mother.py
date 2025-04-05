from azul.wall import Wall
from azul.tile import Tile

class WallMother:
    @staticmethod
    def with_completed_two_horizontal_lines():
        wall = WallMother.with_completed_horizontal_line()
        wall.add(Tile.WHITE, 1)
        wall.add(Tile.BLUE, 1)
        wall.add(Tile.YELLOW, 1)
        wall.add(Tile.RED, 1)
        wall.add(Tile.BLACK, 1)
        return wall

    @staticmethod
    def with_completed_horizontal_line():
        wall = WallMother.with_almost_completed_horizontal_line()
        wall.add(Tile.BLUE, 0)
        return wall

    @staticmethod
    def with_almost_completed_horizontal_line():
        wall = Wall()
        wall.add(Tile.YELLOW, 0)
        wall.add(Tile.RED, 0)
        wall.add(Tile.BLACK, 0)
        wall.add(Tile.WHITE, 0)
        return wall

    @staticmethod
    def with_three_completed_vertical_lines():
        wall = Wall()
        tiles_and_positions = [
            (Tile.BLUE, 0), (Tile.WHITE, 1), (Tile.BLACK, 2), (Tile.RED, 3), (Tile.YELLOW, 4),
            (Tile.YELLOW, 0), (Tile.BLUE, 1), (Tile.WHITE, 2), (Tile.BLACK, 3), (Tile.RED, 4),
            (Tile.RED, 0), (Tile.YELLOW, 1), (Tile.BLUE, 2), (Tile.WHITE, 3), (Tile.BLACK, 4)
        ]
        for tile, position in tiles_and_positions:
            wall.add(tile, position)
        return wall

    @staticmethod
    def with_completed_blue_and_yellow_tiles():
        wall = Wall()
        for i in range(5):
            wall.add(Tile.BLUE, i)
            wall.add(Tile.YELLOW, i)
        return wall
