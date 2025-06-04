from typing import Dict

import numpy as np
import matplotlib.pyplot as plt
import matplotlib.patches as patches
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
        
        # Add matplotlib figure storage for reusing the same plot
        self.fig = None
        self.ax_bag = None
        self.ax_center = None
        self.ax_lid = None
        self.ax_scores = None
        self.ax_factories = None
        self.ax_floor = None
        plt.ion()  # Turn on interactive mode

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
            
        # Get tile counts from the bag and factories
        bag_counts = self.state["bag"]
        lid_counts = self.state["lid"]
        center_counts = self.state["center"]
        factories = self.state["factories"]
        
        # Define tile colors and names
        tile_colors = ['#4169E1', '#FFD700', '#DC143C', '#000000', '#F5F5F5']  # Blue, Yellow, Red, Black, White
        tile_names = ['Blue', 'Yellow', 'Red', 'Black', 'White']
        tile_letters = ['B', 'Y', 'R', 'K', 'W']
        
        # Create figure with subplots if they don't exist, otherwise reuse
        if self.fig is None or self.ax_center is None or self.ax_scores is None or self.ax_factories is None:
            # Create figure with subplots - center at top, scores, factories
            self.fig = plt.figure(figsize=(18, 12))
            self.fig.patch.set_facecolor('#E6E6FA')  # Light lavender background
            
            # Top row: center statistics only
            self.ax_center = plt.subplot2grid((3, 1), (0, 0))
            
            # Player scores
            self.ax_scores = plt.subplot2grid((3, 1), (1, 0))
            
            # Factories subplot
            self.ax_factories = plt.subplot2grid((3, 1), (2, 0))
        else:
            # Clear existing axes for redrawing
            self.ax_center.clear()
            self.ax_scores.clear()
            self.ax_factories.clear()
        
        # Set background colors
        self.ax_center.set_facecolor('#F0F8FF')
        self.ax_scores.set_facecolor('#F0F8FF')
        self.ax_factories.set_facecolor('#F0F8FF')
        
        # Create center bar chart
        bars_center = self.ax_center.bar(range(5), center_counts, color=tile_colors, edgecolor='black', linewidth=1.5)
        bars_center[4].set_edgecolor('black')
        bars_center[4].set_linewidth(2)
        
        # Customize center plot
        self.ax_center.set_title('Center Statistics', fontsize=14, fontweight='bold', pad=10)
        self.ax_center.set_ylabel('Count', fontsize=10)
        self.ax_center.set_xticks(range(5))
        self.ax_center.set_xticklabels([f'{letter}' for letter in tile_letters], fontsize=10)
        
        # Add count labels on center bars
        for bar, count in zip(bars_center, center_counts):
            height = bar.get_height()
            if count > 0:  # Only show label if there are tiles
                self.ax_center.text(bar.get_x() + bar.get_width()/2., height + 0.05,
                                  f'{count}', ha='center', va='bottom', fontsize=9, fontweight='bold')
        
        # Set y-axis limits
        self.ax_center.set_ylim(0, max(center_counts) + 2 if max(center_counts) > 0 else 5)
        
        # Add grids
        self.ax_center.grid(True, alpha=0.3, linestyle='--')
        
        # Set scores title
        self.ax_scores.set_title('Player Scores', fontsize=16, fontweight='bold', pad=20)
        
        # Create scores table instead of bar chart
        player_scores = [player["score"] for player in self.state["players"]]
        
        # Clear the axes and turn off axis
        self.ax_scores.axis('off')
        
        # Create table data
        table_data = []
        for i, score in enumerate(player_scores):
            table_data.append([f'Player {i+1}', str(score)])
        
        # Create table
        table = self.ax_scores.table(cellText=table_data,
                                   colLabels=['Player', 'Score'],
                                   cellLoc='center',
                                   loc='center',
                                   colWidths=[0.3, 0.2])
        
        # Style the table
        table.auto_set_font_size(False)
        table.set_fontsize(12)
        table.scale(1, 2)
        
        # Style header row
        for i in range(2):
            table[(0, i)].set_facecolor('#4169E1')
            table[(0, i)].set_text_props(weight='bold', color='white')
        
        # Style data rows with alternating colors
        for i in range(1, len(table_data) + 1):
            for j in range(2):
                if i % 2 == 0:
                    table[(i, j)].set_facecolor('#F0F8FF')
                else:
                    table[(i, j)].set_facecolor('#E6E6FA')
                table[(i, j)].set_text_props(weight='bold')
        
        # Set factories title
        self.ax_factories.set_title('Factory Displays', fontsize=16, fontweight='bold', pad=20)
        
        # Calculate grid layout for factories
        num_factories = len(factories)
        cols = min(4, num_factories)  # Max 4 factories per row
        rows = (num_factories + cols - 1) // cols  # Ceiling division
        
        factory_width = 4  # Each factory takes 4 tile positions
        factory_height = 1
        margin_x = 1
        margin_y = 0.5
        
        # Draw each factory
        for factory_idx, factory_tiles in enumerate(factories):
            row = factory_idx // cols
            col = factory_idx % cols
            
            # Calculate factory position
            start_x = col * (factory_width + margin_x)
            start_y = (rows - 1 - row) * (factory_height + margin_y)
            
            # Draw factory label
            self.ax_factories.text(start_x + factory_width/2, start_y + factory_height + 0.1,
                                 f'Factory No. {factory_idx + 1}', ha='center', va='bottom',
                                 fontsize=12, fontweight='bold')
            
            # Draw tiles in this factory
            tile_pos = 0
            for tile_type, count in enumerate(factory_tiles):
                for _ in range(count):
                    if tile_pos < 4:  # Max 4 tiles per factory
                        x = start_x + tile_pos
                        y = start_y
                        
                        # Create colored square for tile
                        square = plt.Rectangle((x, y), 0.8, 0.8, 
                                             facecolor=tile_colors[tile_type],
                                             edgecolor='black', linewidth=2)
                        self.ax_factories.add_patch(square)
                        
                        # Add tile letter
                        self.ax_factories.text(x + 0.4, y + 0.4, tile_letters[tile_type],
                                             ha='center', va='center', fontsize=14, fontweight='bold',
                                             color='white' if tile_type != 4 else 'black')  # White text except on white tiles
                        
                        tile_pos += 1
        
        # Set axis limits and remove ticks
        total_width = cols * (factory_width + margin_x) - margin_x
        total_height = rows * (factory_height + margin_y) + 0.5
        
        self.ax_factories.set_xlim(-0.5, total_width + 0.5)
        self.ax_factories.set_ylim(-0.5, total_height)
        self.ax_factories.set_xticks([])
        self.ax_factories.set_yticks([])
        self.ax_factories.set_aspect('equal')
        
        # Add legend for tile colors and letters
        legend_elements = []
        for i, (color, letter, name) in enumerate(zip(tile_colors, tile_letters, tile_names)):
            legend_elements.append(plt.Rectangle((0, 0), 1, 1, facecolor=color, edgecolor='black', 
                                               label=f'{letter} = {name}'))
        
        # Add first player marker to legend
        legend_elements.append(plt.Rectangle((0, 0), 1, 1, facecolor='white', edgecolor='black', 
                                           label='M = 1st Player Marker'))
        
        self.ax_factories.legend(handles=legend_elements, loc='upper right', fontsize=9, 
                               title='Tile Types', title_fontsize=10)
        
        # Show bag and lid in separate popup window
        self.show_bag_lid_popup(bag_counts, lid_counts, tile_colors, tile_letters)
        
        # Show individual player windows (floor, pattern lines, wall)
        self.show_individual_player_windows(tile_colors, tile_letters)
        
        # Update the display
        self.fig.tight_layout()
        self.fig.canvas.draw()
        self.fig.canvas.flush_events()
        plt.show(block=False)
        plt.pause(0.01)  # Brief pause to ensure the plot updates
    
    def show_bag_lid_popup(self, bag_counts, lid_counts, tile_colors, tile_letters):
        """Show bag and lid statistics in a separate popup window"""
        # Create popup window if it doesn't exist
        if not hasattr(self, 'bag_lid_fig') or self.bag_lid_fig is None:
            self.bag_lid_fig = plt.figure(figsize=(12, 6))
            self.bag_lid_fig.canvas.manager.set_window_title('Azul - Bag & Lid Statistics')
            self.bag_lid_fig.patch.set_facecolor('#E6E6FA')
            
            # Create subplots for bag and lid
            self.ax_bag = plt.subplot2grid((2, 1), (0, 0))
            self.ax_lid = plt.subplot2grid((2, 1), (1, 0))
            
            self.ax_bag.set_facecolor('#F0F8FF')
            self.ax_lid.set_facecolor('#F0F8FF')
        else:
            # Clear existing axes
            self.ax_bag.clear()
            self.ax_lid.clear()
            self.ax_bag.set_facecolor('#F0F8FF')
            self.ax_lid.set_facecolor('#F0F8FF')
        
        # Create bag bar chart
        bars_bag = self.ax_bag.bar(range(5), bag_counts, color=tile_colors, edgecolor='black', linewidth=1.5)
        bars_bag[4].set_edgecolor('black')
        bars_bag[4].set_linewidth(2)
        
        # Create lid bar chart  
        bars_lid = self.ax_lid.bar(range(5), lid_counts, color=tile_colors, edgecolor='black', linewidth=1.5)
        bars_lid[4].set_edgecolor('black')
        bars_lid[4].set_linewidth(2)
        
        # Customize bag plot
        self.ax_bag.set_title('Bag Statistics', fontsize=14, fontweight='bold', pad=10)
        self.ax_bag.set_ylabel('Count', fontsize=10)
        self.ax_bag.set_xticks(range(5))
        self.ax_bag.set_xticklabels([f'{letter}' for letter in tile_letters], fontsize=10)
        
        # Customize lid plot
        self.ax_lid.set_title('Lid Statistics', fontsize=14, fontweight='bold', pad=10)
        self.ax_lid.set_ylabel('Count', fontsize=10)
        self.ax_lid.set_xticks(range(5))
        self.ax_lid.set_xticklabels([f'{letter}' for letter in tile_letters], fontsize=10)
        
        # Add count labels on bag bars
        for bar, count in zip(bars_bag, bag_counts):
            height = bar.get_height()
            if count > 0:  # Only show label if there are tiles
                self.ax_bag.text(bar.get_x() + bar.get_width()/2., height + 0.05,
                               f'{count}', ha='center', va='bottom', fontsize=9, fontweight='bold')
        
        # Add count labels on lid bars
        for bar, count in zip(bars_lid, lid_counts):
            height = bar.get_height()
            if count > 0:  # Only show label if there are tiles
                self.ax_lid.text(bar.get_x() + bar.get_width()/2., height + 0.05,
                               f'{count}', ha='center', va='bottom', fontsize=9, fontweight='bold')
        
        # Set y-axis limits
        self.ax_bag.set_ylim(0, max(bag_counts) + 2 if max(bag_counts) > 0 else 5)
        self.ax_lid.set_ylim(0, max(lid_counts) + 2 if max(lid_counts) > 0 else 5)
        
        # Add grids
        self.ax_bag.grid(True, alpha=0.3, linestyle='--')
        self.ax_lid.grid(True, alpha=0.3, linestyle='--')
        
        # Update the popup display
        self.bag_lid_fig.tight_layout()
        self.bag_lid_fig.canvas.draw()
        self.bag_lid_fig.canvas.flush_events()
        plt.show(block=False)

    def show_individual_player_windows(self, tile_colors, tile_letters):
        """Show individual player windows (floor, pattern lines, wall)"""
        num_players = len(self.state["players"])
        
        # Initialize player windows list if not exists
        if not hasattr(self, 'player_figs') or self.player_figs is None:
            self.player_figs = []
            self.player_axes = []
            
            for player_idx in range(num_players):
                # Create window for each player
                fig = plt.figure(figsize=(12, 10))
                fig.canvas.manager.set_window_title(f'Azul - Player {player_idx + 1}')
                fig.patch.set_facecolor('#E6E6FA')
                
                # Create subplots: floor, pattern lines, wall
                ax_floor = plt.subplot2grid((3, 1), (0, 0))
                ax_pattern = plt.subplot2grid((3, 1), (1, 0))
                ax_wall = plt.subplot2grid((3, 1), (2, 0))
                
                ax_floor.set_facecolor('#F0F8FF')
                ax_pattern.set_facecolor('#F0F8FF')
                ax_wall.set_facecolor('#F0F8FF')
                
                self.player_figs.append(fig)
                self.player_axes.append((ax_floor, ax_pattern, ax_wall))
        else:
            # Clear existing axes
            for ax_floor, ax_pattern, ax_wall in self.player_axes:
                ax_floor.clear()
                ax_pattern.clear()
                ax_wall.clear()
                ax_floor.set_facecolor('#F0F8FF')
                ax_pattern.set_facecolor('#F0F8FF')
                ax_wall.set_facecolor('#F0F8FF')
        
        # Draw each player's data
        for player_idx, player_data in enumerate(self.state["players"]):
            ax_floor, ax_pattern, ax_wall = self.player_axes[player_idx]
            
            # Draw floor for this player
            self.draw_single_player_floor(player_data, ax_floor, tile_colors, tile_letters, player_idx)
            
            # Draw pattern lines for this player
            self.draw_single_player_pattern_lines(player_data, ax_pattern, tile_colors, tile_letters, player_idx)
            
            # Draw wall for this player
            self.draw_single_player_wall(player_data, ax_wall, tile_colors, tile_letters, player_idx)
            
            # Update the display
            self.player_figs[player_idx].tight_layout()
            self.player_figs[player_idx].canvas.draw()
            self.player_figs[player_idx].canvas.flush_events()
            plt.show(block=False)

    def draw_single_player_floor(self, player_data, ax_floor, tile_colors, tile_letters, player_idx):
        """Draw floor for a single player"""
        ax_floor.set_title('Floor', fontsize=14, fontweight='bold', pad=15)
        
        # Floor penalty values (standard Azul penalties)
        floor_penalties = [-1, -1, -2, -2, -2, -3, -3]
        floor_tiles = player_data["floor"]
        
        # Draw floor positions
        for pos in range(7):
            x = pos + 0.5
            y = 0.5
            
            # Draw floor slot background
            slot = plt.Rectangle((x, y), 0.8, 0.8, facecolor='lightgray', 
                               edgecolor='black', linewidth=1, alpha=0.3)
            ax_floor.add_patch(slot)
            
            # Draw penalty value below the slot
            ax_floor.text(x + 0.4, y - 0.2, f'{floor_penalties[pos]}',
                         ha='center', va='center', fontsize=10, fontweight='bold',
                         color='red')
            
            # Draw tile if present
            if pos < len(floor_tiles):
                tile_type = floor_tiles[pos]
                
                if tile_type == 5:  # First player marker
                    # Draw first player marker as a special white square
                    marker = plt.Rectangle((x, y), 0.8, 0.8, facecolor='white', 
                                         edgecolor='black', linewidth=2)
                    ax_floor.add_patch(marker)
                    
                    # Add text for first player marker
                    ax_floor.text(x + 0.4, y + 0.4, 'M',
                                 ha='center', va='center', fontsize=14, fontweight='bold',
                                 color='black')
                else:
                    # Draw regular tile
                    tile_square = plt.Rectangle((x, y), 0.8, 0.8, 
                                                facecolor=tile_colors[tile_type],
                                                edgecolor='black', linewidth=2)
                    ax_floor.add_patch(tile_square)
                    
                    # Add tile letter
                    ax_floor.text(x + 0.4, y + 0.4, tile_letters[tile_type],
                                 ha='center', va='center', fontsize=12, fontweight='bold',
                                 color='white' if tile_type != 4 else 'black')
        
        # Set axis limits and remove ticks
        ax_floor.set_xlim(-0.5, 7.5)
        ax_floor.set_ylim(-0.5, 1.5)
        ax_floor.set_xticks([])
        ax_floor.set_yticks([])
        ax_floor.set_aspect('equal')

    def draw_single_player_pattern_lines(self, player_data, ax_pattern, tile_colors, tile_letters, player_idx):
        """Draw pattern lines for a single player"""
        ax_pattern.set_title('Pattern Lines', fontsize=14, fontweight='bold', pad=15)
        
        pattern_lines = player_data["pattern_lines"]
        
        # Draw each pattern line row in triangular formation (1 to 5 tiles)
        for row in range(5):
            row_length = row + 1  # Row 0 has 1 tile, row 1 has 2 tiles, etc.
            pattern_row = pattern_lines[row]
            
            # Right-align all rows to create proper Azul pattern lines structure
            right_edge = 4.5  # Right edge position
            start_x = right_edge - (row_length * 0.8)  # Start position for right alignment
            y = 4 - row + 0.5  # Row 0 at top, row 4 at bottom
            
            # Draw tiles for this row
            for pos in range(row_length):
                x = start_x + pos * 0.8
                
                # Draw slot background
                slot = plt.Rectangle((x, y), 0.7, 0.7, facecolor='lightgray', 
                                   edgecolor='black', linewidth=1, alpha=0.3)
                ax_pattern.add_patch(slot)
                
                # Check if there's a tile in this position
                if pos < len(pattern_row):
                    tile_type = pattern_row[pos]
                    
                    # Only draw if it's not empty (value 5 means empty)
                    if tile_type != 5:
                        # Draw tile
                        tile_square = plt.Rectangle((x, y), 0.7, 0.7, 
                                                  facecolor=tile_colors[tile_type],
                                                  edgecolor='black', linewidth=2)
                        ax_pattern.add_patch(tile_square)
                        
                        # Add tile letter
                        ax_pattern.text(x + 0.35, y + 0.35, tile_letters[tile_type],
                                       ha='center', va='center', fontsize=10, fontweight='bold',
                                       color='white' if tile_type != 4 else 'black')
        
        # Set axis limits and styling
        ax_pattern.set_xlim(0, 5)
        ax_pattern.set_ylim(0, 6)
        ax_pattern.set_xticks([])
        ax_pattern.set_yticks([])
        ax_pattern.set_aspect('equal')
        
        # Add a subtle grid
        ax_pattern.grid(True, alpha=0.2, linestyle='--')

    def draw_single_player_wall(self, player_data, ax_wall, tile_colors, tile_letters, player_idx):
        """Draw wall for a single player"""
        ax_wall.set_title('Wall', fontsize=14, fontweight='bold', pad=15)
        
        wall = player_data["wall"]
        
        # Standard Azul wall pattern (each row has different tile order)
        # Row 0: B Y R K W (0 1 2 3 4)
        # Row 1: W B Y R K (4 0 1 2 3)  
        # Row 2: K W B Y R (3 4 0 1 2)
        # Row 3: R K W B Y (2 3 4 0 1)
        # Row 4: Y R K W B (1 2 3 4 0)
        wall_pattern = [
            [0, 1, 2, 3, 4],  # Row 0
            [4, 0, 1, 2, 3],  # Row 1
            [3, 4, 0, 1, 2],  # Row 2
            [2, 3, 4, 0, 1],  # Row 3
            [1, 2, 3, 4, 0]   # Row 4
        ]
        
        # Draw wall for this player
        for row in range(5):
            for col in range(5):
                x = col + 0.5
                y = 4 - row + 0.5  # Row 0 at top, row 4 at bottom
                
                # Get expected tile type for this position
                expected_tile_type = wall_pattern[row][col]
                
                # Draw slot background with pattern color (very light)
                slot = plt.Rectangle((x, y), 0.8, 0.8, 
                                   facecolor=tile_colors[expected_tile_type], 
                                   edgecolor='black', linewidth=1, alpha=0.1)
                ax_wall.add_patch(slot)
                
                # Draw border for the slot
                border = plt.Rectangle((x, y), 0.8, 0.8, 
                                     facecolor='none', edgecolor='black', linewidth=1)
                ax_wall.add_patch(border)
                
                # Check if there's a tile placed in this position
                placed_tile_type = wall[row][col]
                
                if placed_tile_type != 5:  # 5 means empty
                    # Draw placed tile
                    tile_square = plt.Rectangle((x, y), 0.8, 0.8, 
                                                  facecolor=tile_colors[placed_tile_type],
                                                  edgecolor='black', linewidth=2)
                    ax_wall.add_patch(tile_square)
                    
                    # Add tile letter
                    ax_wall.text(x + 0.4, y + 0.4, tile_letters[placed_tile_type],
                               ha='center', va='center', fontsize=12, fontweight='bold',
                               color='white' if placed_tile_type != 4 else 'black')
                else:
                    # Show expected tile letter faintly
                    ax_wall.text(x + 0.4, y + 0.4, tile_letters[expected_tile_type],
                               ha='center', va='center', fontsize=10, fontweight='bold',
                               color='gray', alpha=0.3)
        
        # Set axis limits and styling
        ax_wall.set_xlim(-0.5, 5.5)
        ax_wall.set_ylim(-0.5, 5.5)
        ax_wall.set_xticks([])
        ax_wall.set_yticks([])
        ax_wall.set_aspect('equal')
        
        # Add a subtle grid
        ax_wall.grid(True, alpha=0.2, linestyle='--')

    def close(self):
        if self.fig is not None:
            plt.close(self.fig)
            self.fig = None
            self.ax_center = None
            self.ax_scores = None
            self.ax_factories = None
        
        # Close bag and lid popup if it exists
        if hasattr(self, 'bag_lid_fig') and self.bag_lid_fig is not None:
            plt.close(self.bag_lid_fig)
            self.bag_lid_fig = None
            self.ax_bag = None
            self.ax_lid = None
        
        # Close individual player windows if they exist
        if hasattr(self, 'player_figs') and self.player_figs is not None:
            for fig in self.player_figs:
                plt.close(fig)
            self.player_figs = None
            self.player_axes = []
