import sys
from flask import Flask, request, jsonify
from werkzeug.exceptions import HTTPException
from azul_ai_gym.action_not_allowed_exception import ActionNotAllowedException
from azul_ai_gym.lid import Lid
from azul_ai_gym.player import Player
from azul_ai_gym.board import Board
from azul_ai_gym.wall import Wall
from azul_ai_gym.floor import Floor
from azul_ai_gym.game import Game
from azul_ai_gym.center import Center
from azul_ai_gym.game_controller import GameController
from azul_ai_gym.factory_taking_request import FactoryTakingRequest
from azul_ai_gym.center_taking_request import CenterTakingRequest

def create_app():
    app = Flask(__name__)
    game_controller = GameController(create_game())

    app.add_url_rule('/show', 'show', game_controller.show)
    app.add_url_rule('/showJson', 'show_json', game_controller.show_json)
    app.add_url_rule(
        '/takeFromFactory',
        'take_tiles_from_factory',
        lambda: game_controller.take_tiles_from_factory(FactoryTakingRequest(**request.get_json())),
        methods=['POST']
    )
    app.add_url_rule(
        '/takeFromCenter',
        'take_tiles_from_center',
        lambda: game_controller.take_tiles_from_center(CenterTakingRequest(**request.get_json())),
        methods=['POST']
    )

    def handle_action_not_allowed_exception(e):
        return jsonify({"error": e.message}), 400

    def handle_exception(e):
        if isinstance(e, HTTPException):
            return jsonify(error=str(e)), e.code
        return jsonify(error="An unexpected error occurred"), 500

    app.register_error_handler(ActionNotAllowedException, handle_action_not_allowed_exception)
    app.register_error_handler(Exception, handle_exception)
    return app


def create_game():
    lid = Lid()
    players = []
    player_count = get_player_count()
    for i in range(player_count):
        players.append(Player(Board(wall=Wall(), floor=Floor(lid)), f"Player {i + 1}"))
    return Game(players, Center(), 0, lid)


def get_player_count():
    if len(sys.argv) < 2:
        raise RuntimeError("Please provide a player count.")
    try:
        player_count = int(sys.argv[1])
        if player_count not in {2, 3, 4}:
            raise RuntimeError("Player count must be equal to 2, 3, or 4.")
        return player_count
    except ValueError:
        raise RuntimeError("Failed to read player count.")


if __name__ == '__main__':
    app = create_app()
    app.run()
