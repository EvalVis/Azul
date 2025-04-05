import random

from azul.action_not_allowed_exception import ActionNotAllowedException
from azul.center import Center
from azul.lid import Lid
from azul.bag import Bag
from azul.factory_display import FactoryDisplay

class Game:
    def __init__(self, players, center=None, starting_player=None, lid=None):
        if center is None:
            center = Center()
        if starting_player is None:
            starting_player = random.randint(0, len(players) - 1)
        if lid is None:
            lid = Lid()
        self.players = players
        self.center = center
        self.current_player = starting_player
        self.bag = Bag()
        self.lid = lid
        self.factory_displays = [FactoryDisplay(center, self.bag.take_tiles(4, lid)) for _ in range(self.factory_display_count())]
        self.players[self.current_player].give_starting_marker()
        self.is_running = True

    def factory_display_count(self):
        return 1 + 2 * len(self.players)

    def change_factory_display(self, index, tiles):
        self.factory_displays[index] = FactoryDisplay(self.center, tiles)

    def set_bag(self, bag):
        self.bag = bag

    def execute_wall_tiling_phase(self):
        for player in self.players:
            player.move_tiles_to_wall(self.lid)
            player.give_floor_penalty()
            player.board.clear_floor()
        if all(player.board.wall.completed_horizontal_lines() == 0 for player in self.players):
            self.prepare_for_next_round()
        else:
            self.execute_game_ending_phase()

    def prepare_for_next_round(self):
        for i in range(1 + 2 * len(self.players)):
                self.factory_displays[i] = FactoryDisplay(self.center, self.bag.take_tiles(4, self.lid))

    def execute_game_ending_phase(self):
        for player in self.players:
            player.assign_game_ending_score()
        self.is_running = False

    def clear_factory_displays(self):
        for factory_display in self.factory_displays:
            factory_display.clear()

    def peek_center(self):
        return self.center.tiles

    def bag_tiles(self):
        return self.bag.tiles

    def execute_factory_offer_phase_with_factory(self, factory_index, tile_to_take, amount_to_place_on_floor, pattern_line_index):
        if not self.is_running:
            raise ActionNotAllowedException("Game has already ended.")
        self.players[self.current_player].take_tiles_from_factory(
            self.factory_displays[factory_index], tile_to_take, amount_to_place_on_floor, pattern_line_index
        )
        if all(factory_display.is_empty() for factory_display in self.factory_displays) and self.center.is_empty():
            self.execute_wall_tiling_phase()
        else:
            self.current_player = 0 if self.current_player == (len(self.players) - 1) else (self.current_player + 1)

    def execute_factory_offer_phase_with_center(self, tile_to_take, amount_to_place_on_floor, pattern_line_index):
        if not self.is_running:
            raise ActionNotAllowedException("Game has already ended.")
        self.players[self.current_player].take_tiles_from_center(
            self.center, tile_to_take, amount_to_place_on_floor, pattern_line_index
        )
        if all(factory_display.is_empty() for factory_display in self.factory_displays) and self.center.is_empty():
            self.execute_wall_tiling_phase()
        else:
            self.current_player = 0 if self.current_player == (len(self.players) - 1) else (self.current_player + 1)

    def __str__(self):
        result = []
        if self.is_running:
            result.append("Factories: " + " ".join(f"{i + 1}) {factory_display}" for i, factory_display in enumerate(self.factory_displays)))
            result.append(f"Center: {self.center}")
            result.append("\n".join(f"Player: {player}" for player in self.players))
            result.append(f"Bag: {self.bag}\nLid: {self.lid}")
        else:
            result.append("\n".join(f"{player.name}: {player.score}" for player in self.players))
        return "\n".join(result)

    def json_object(self):
        players_json = [player.json_object() for player in self.players]
        factory_displays_json = [factory_display.json_object() for factory_display in self.factory_displays]
        result = {
            "isRunning": self.is_running,
            "Factory displays": factory_displays_json,
            "Center": self.center.json_object(),
            "Players": players_json,
            "Bag": self.bag.json_object(),
            "Lid": self.lid.json_object(),
        }
        if not self.is_running:
            result["Winners"] = self.winners()
        return result

    def winners(self):
        max_score = max(player.score for player in self.players)
        best_players = [player for player in self.players if player.score == max_score]
        if len(best_players) > 1:
            max_completed_horizontal_lines = max(player.board.wall.completed_horizontal_lines() for player in self.players)
            return [player.name for player in self.players if player.board.wall.completed_horizontal_lines() == max_completed_horizontal_lines]
        return [player.name for player in best_players]