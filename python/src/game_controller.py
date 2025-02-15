from flask import jsonify
from src.factory_taking_request import FactoryTakingRequest
from src.center_taking_request import CenterTakingRequest
from src.tile import Tile

class GameController:
    def __init__(self, game):
        self.game = game

    def show(self):
        return str(self.game)

    def show_json(self):
        return jsonify(self.game.json_object())

    def take_tiles_from_factory(self, factory_taking_request: FactoryTakingRequest):
        self.game.execute_factory_offer_phase_with_factory(
            factory_taking_request.factory_index, Tile(factory_taking_request.tile_to_take),
            factory_taking_request.tiles_to_put_on_floor, factory_taking_request.pattern_line_index
        )
        return jsonify(self.game.json_object())

    def take_tiles_from_center(self, center_taking_request: CenterTakingRequest):
        self.game.execute_factory_offer_phase_with_center(
            Tile(center_taking_request.tile_to_take), center_taking_request.tiles_to_put_on_floor,
            center_taking_request.pattern_line_index
        )
        return jsonify(self.game.json_object())