from typing import Dict

import numpy as np
import matplotlib.pyplot as plt
from azul.action_not_allowed_exception import ActionNotAllowedException
from azul.board import Board
from azul.center import Center
from azul.center_taking_request import CenterTakingRequest
from azul.factory_taking_request import FactoryTakingRequest
from azul.floor import Floor
from azul.game import Game
from azul.lid import Lid
from azul.player import Player
from azul.tile import Tile
from azul.wall import Wall
from gym import spaces
from pettingzoo import AECEnv
from pettingzoo.utils import agent_selector
from render import AzulRenderer


class AzulEnv(AECEnv):
    metadata = {
        "name": "azul_env_v1"
    }
    def __init__(self, player_count=2, max_moves=None):
        super(AzulEnv, self).__init__()
        self.player_count = player_count
        self.agents = [f"player_{i}" for i in range(player_count)]
        self._cumulative_rewards = {agent: 0 for agent in self.agents}
        self.truncations = {agent: False for agent in self.agents}
        self.terminations = {agent: False for agent in self.agents}
        self.infos = {agent: {} for agent in self.agents}
        self._agent_selector = agent_selector(self.agents)
        self.agent_selection = self._agent_selector.reset()
        self.factories = 1 + 2 * player_count
        self.game = None
        self.state = None
        self.current_move = 0
        self.max_moves = player_count * 150 if max_moves is None else max_moves
        
        # Initialize the new renderer
        self.renderer = AzulRenderer()

        self.observation_spaces: Dict[str, spaces.Space] = {
            agent: spaces.Dict({
                "factories": spaces.Box(low=0, high=4, shape=(self.factories, 5), dtype=np.int32),
                "center": spaces.Box(low=0, high=3 * self.factories, shape=(5,), dtype=np.int32),
                "players": spaces.Tuple([
                    spaces.Dict({
                        "pattern_lines": spaces.Box(low=0, high=5, shape=(5, 5), dtype=np.int32),
                        "wall": spaces.Box(low=0, high=5, shape=(5, 5), dtype=np.int32),
                        "floor": spaces.Box(low=0, high=5, shape=(7,), dtype=np.int32),
                        # high is 5 instead of 4 because of first player marker.
                        "is_starting": spaces.Discrete(2),
                        "score": spaces.Discrete(241)
                    }) for _ in range(player_count)
                ]),
                "bag": spaces.Box(low=0, high=100, shape=(5,), dtype=np.int32),
                "lid": spaces.Box(low=0, high=100, shape=(5,), dtype=np.int32)
            }) for agent in self.agents
        }

        self.action_spaces: Dict[str, spaces.Space] = {
            agent: spaces.MultiDiscrete([
                self.factories + 1,  # factory index (0 is center)
                5,  # tile color (0-4)
                20,  # tiles to place on the floor
                5  # pattern line index (0-4)
            ]) for agent in self.agents
        }

        self.reset()

    def observation_space(self, agent):
        return self.observation_spaces[agent]

    def action_space(self, agent):
        return self.action_spaces[agent]

    def reset(self, seed=None, options=None):
        self.game = self.create_game()
        self.set_state()
        self._cumulative_rewards = {agent: 0 for agent in self.agents}
        self.truncations = {agent: False for agent in self.agents}
        self.terminations = {agent: False for agent in self.agents}
        self.infos = {agent: {} for agent in self.agents}
        self.agent_selection = self._agent_selector.reset()
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

    def set_state(self):
        game_state = self.game.json_object()
        self.state = {
            "factories": np.array(
                [AzulEnv.__convert_tile_dict_to_array__(factory) for factory in game_state.get("Factory displays")]),
            "center": AzulEnv.__convert_tile_dict_to_array__(game_state.get("Center")),
            "players": [
                {
                    "pattern_lines": np.array(
                        [
                            [AzulEnv.__tile_to_number__(tile) for tile in row] + [5] * (5 - len(row))
                            # Fill empty spots with 5
                            for row in player_state.get("Board").get("Pattern lines")
                        ], dtype=np.int32),
                    "wall": np.array(
                        [[AzulEnv.__tile_to_number__(tile) if tile.isupper() else 5 for tile in row] for row in
                         player_state.get("Board").get("Wall")], dtype=np.int32),
                    "floor": [AzulEnv.__tile_to_number__(tile) if tile != "M" else 5 for tile in
                              player_state.get("Board").get("Floor")],
                    "score": player_state.get("Score")
                }
                for player_state in game_state.get("Players")
            ],
            "bag": AzulEnv.__convert_tile_dict_to_array__(game_state.get("Bag")),
            "lid": AzulEnv.__convert_tile_dict_to_array__(game_state.get("Lid"))
        }

    def step(self, action):
        factory_index, tile_to_take_number, tiles_to_place_floor, pattern_line_index = action
        tile_to_take = Tile(self.__number_to_tile__(tile_to_take_number))

        try:
            if factory_index == 0:
                CenterTakingRequest(tile_to_take, tiles_to_place_floor, pattern_line_index).validate(self.game)
                self.game.execute_factory_offer_phase_with_center(tile_to_take, tiles_to_place_floor,
                                                                  pattern_line_index)
            else:
                FactoryTakingRequest(factory_index - 1, tile_to_take, tiles_to_place_floor,
                                     pattern_line_index).validate(self.game)
                self.game.execute_factory_offer_phase_with_factory(factory_index - 1, tile_to_take,
                                                                   tiles_to_place_floor, pattern_line_index)
        except ActionNotAllowedException:
            reward = -2
            self._cumulative_rewards[self.agent_selection] += reward
            return self.state, reward, False, False, {}

        self.set_state()
        self.agent_selection = self._agent_selector.next()
        reward = -1
        self._cumulative_rewards[self.agent_selection] += reward

        terminated = not self.game.json_object().get("isRunning")
        if terminated:
            self.terminations = {agent: True for agent in self.agents}
            self.add_score()

        self.current_move += 1
        truncated = self.current_move >= self.max_moves

        if truncated:
            self.truncations = {agent: True for agent in self.agents}
            self.add_score()

        return self.state, reward, terminated, truncated, {}

    def add_score(self):
        for i, a in enumerate(self.agents):
            self._cumulative_rewards[a] += self.state["players"][i]["score"]

    def observe(self, agent):
        return self.state

    def render(self):
        print(self.state)
        if self.state is None:
            return
            
        # Get tile counts from the state
        bag_counts = self.state["bag"]
        lid_counts = self.state["lid"]
        center_counts = self.state["center"]
        factories = self.state["factories"]
        
        # Use the new renderer
        self.renderer.render(self.state, bag_counts, lid_counts, center_counts, factories)

    def close(self):
        self.renderer.close()
