from common.car import Car
from common.car_state import CarState
from common.driver import Driver
from common.point import Point
from common.track import Track
from network.fully_connected_layer import FullyConnectedLayer as Layer
from network.numpy_network import NumpyNetwork as Network

import numpy as np


class SimpleCarDriver(Driver):
    def __init__(self, hidden_layers, track: Track, car: Car):
        self.car = car
        self.track = track
        dimensions = [len(car.lidar_origins()) * 2 + 2] + hidden_layers + [2]
        layers = [Layer(dimensions[i-1], dimensions[i], np.tanh) for i in range(1, len(dimensions))]
        self.nn = Network(layers)

    def initial_state(self) -> CarState:
        return CarState(self.track.start_point, self.track.start_angle, (0, 0))

    def action(self, state: CarState, network) -> CarState:
        relative_points = [state.center_of_mass + p.rotate(state.direction) for p in self.car.lidar_origins()]
        lidar_points = [self.track.lidar(state.center_of_mass, p) for p in relative_points]
        distances = [self.cap_n_scale(Point.distance(state.center_of_mass, p[1]), 200) for p in lidar_points]
        center_idx = self.track.get_section_index(state.center_of_mass)
        track_distances = [self.scale(self.track.distance((center_idx, state.center_of_mass), p), 60)
                           for p in lidar_points]
        inputs = distances + track_distances + [state.momentum[0], state.momentum[1]]
        output = network(inputs)
        return self.car.drive(output[0], output[1], state)

    @staticmethod
    def cap_n_scale(x: float, m: float) -> float:
        return min(x, m) / m

    @staticmethod
    def scale(x: float, m: float) -> float:
        return x / m

    def time_trial(self, limit: int, weights):
        network = self.nn.build(weights)
        states = [self.initial_state()]
        for _ in range(limit):
            location = states[-1].center_of_mass
            if self.track.out_of_bounds(location):
                return states
            states.append(self.action(states[-1], network))

        return states

    def track_distance(self, run):
        return sum([self.track.distance(run[i-1].center_of_mass, run[i].center_of_mass) for i in range(1, len(run))])
