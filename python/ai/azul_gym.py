import gym
from gym import spaces
import numpy as np

from lib.azul.action_not_allowed_exception import ActionNotAllowedException
from lib.azul.center import Center
from lib.azul.floor import Floor
from lib.azul.board import Board
from lib.azul.player import Player
from lib.azul.game import Game
from lib.azul.lid import Lid
from lib.azul.wall import Wall
from lib.azul.tile import Tile
from lib.azul.factory_taking_request import FactoryTakingRequest
from lib.azul.center_taking_request import CenterTakingRequest


class AzulEnv(gym.Env):
    def __init__(self, player_count=2):
        super(AzulEnv, self).__init__()
        self.player_count = player_count
        self.factories = 1 + 2 * self.player_count
        self.game = None
        self.state = None
        self.current_player = 0

        # Action space:
        # (Take from factory: factory_index, tile_color, tiles_to_floor, pattern_line_index)
        # (Take from center: tile_color, tiles_to_floor, pattern_line_index)
        self.action_space = spaces.MultiDiscrete([
            self.factories + 1,  # factory index (0 is center)
            5,  # tile color (0-4)
            20,  # tiles to place on the floor
            5  # pattern line index (0-4)
        ])

        self.observation_space = spaces.Dict({
            "factories": spaces.Box(low=0, high=4, shape=(self.factories, 5), dtype=np.int32),
            "center": spaces.Box(low=0, high=3 * self.factories, shape=(5,), dtype=np.int32),
            "players": spaces.Tuple([
                spaces.Dict({
                    "pattern_lines": spaces.Box(low=0, high=5, shape=(5, 5), dtype=np.int32),
                    "wall": spaces.Box(low=0, high=5, shape=(5, 5), dtype=np.int32),
                    "floor": spaces.Box(low=0, high=5, shape=(7,), dtype=np.int32), # high 4 instead of 5 because of first player marker.
                    "is_starting": spaces.Discrete(2),
                    "score": spaces.Discrete(241)
                }) for _ in range(player_count)
            ]),
            "bag": spaces.Box(low=0, high=100, shape=(5,), dtype=np.int32),
            "lid": spaces.Box(low=0, high=100, shape=(5,), dtype=np.int32)
        })

        self.reset()

    def reset(self, seed=None, options=None):
        self.game = self.create_game()
        self.set_state()
        return self.state, {}

    @staticmethod
    def __convert_tile_dict_to_array__(tile_dict):
        tile_array = np.zeros(5, dtype=np.int32)
        for tile, count in tile_dict.items():
            tile_index = AzulEnv.__tile_to_number__(tile)
            if tile_index is not None:
                tile_array[tile_index] = count
        return tile_array


    @staticmethod
    def __number_to_tile__(number):
        return {
            0: "B",
            1: "Y",
            2: "R",
            3: "K",
            4: "W"
        }.get(number)

    @staticmethod
    def __tile_to_number__(tile):
        return {
            "B": 0,
            "Y": 1,
            "R": 2,
            "K": 3,
            "W": 4
        }.get(tile)

    def create_game(self):
        lid = Lid()
        players = []
        for i in range(self.player_count):
            players.append(Player(Board(wall=Wall(), floor=Floor(lid)), f"Player {i + 1}"))
        return Game(players, Center(), 0, lid)

    def step(self, action):
        if not self.game.json_object().get("isRunning"):
            return self.state, 0, False, None, {}
            
        factory_index, tile_to_take_number, tiles_to_place_floor, pattern_line_index = action
        tile_to_take = Tile(self.__number_to_tile__(tile_to_take_number))
        
        try:
            if factory_index == 0:
                CenterTakingRequest(tile_to_take, tiles_to_place_floor, pattern_line_index).validate(self.game)
                self.game.execute_factory_offer_phase_with_center(tile_to_take, tiles_to_place_floor, pattern_line_index)
            else:
                FactoryTakingRequest(factory_index - 1, tile_to_take, tiles_to_place_floor, pattern_line_index).validate(self.game)
                self.game.execute_factory_offer_phase_with_factory(factory_index - 1, tile_to_take, tiles_to_place_floor, pattern_line_index)
        except ActionNotAllowedException as e:
            return self.state, -2, None, None, {}

        self.set_state()
        self.current_player = 0 if self.current_player == (self.player_count - 1) else (self.current_player + 1)
        return self.state, -1, None, None, {}

    def set_state(self):
        game_state = self.game.json_object()
        self.state = {
            "factories": np.array([AzulEnv.__convert_tile_dict_to_array__(factory) for factory in game_state.get("Factory displays")]),
            "center": AzulEnv.__convert_tile_dict_to_array__(game_state.get("Center")),
            "players": [
                {
                    "pattern_lines": np.array(
                        [
                            [AzulEnv.__tile_to_number__(tile) for tile in row] + [5] * (5 - len(row))  # Fill empty spots with 5
                            for row in player_state.get("Board").get("Pattern lines")
                        ], dtype=np.int32),
                    "wall": np.array([[AzulEnv.__tile_to_number__(tile) if tile.isupper() else 5 for tile in row] for row in player_state.get("Board").get("Wall")], dtype=np.int32),
                    "floor": [AzulEnv.__tile_to_number__(tile) if tile != "M" else 5 for tile in player_state.get("Board").get("Floor")],
                    "score": player_state.get("Score")
                }
                for player_state in game_state.get("Players")
            ],
            "bag": AzulEnv.__convert_tile_dict_to_array__(game_state.get("Bag")),
            "lid": AzulEnv.__convert_tile_dict_to_array__(game_state.get("Lid"))
        }

    def observe(self, player_index):
        reward = 0
        terminated = not self.game.json_object().get("isRunning")
        if terminated:
            reward = self.state["players"][player_index]["score"]
        return self.state, reward, terminated, False

    def render(self):
        pass

    def close(self):
        pass