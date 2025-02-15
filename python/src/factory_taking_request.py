class FactoryTakingRequest:
    def __init__(self, factory_index, tile_to_take, tiles_to_put_on_floor, pattern_line_index):
        self.factory_index = factory_index
        self.tile_to_take = tile_to_take
        self.tiles_to_put_on_floor = tiles_to_put_on_floor
        self.pattern_line_index = pattern_line_index