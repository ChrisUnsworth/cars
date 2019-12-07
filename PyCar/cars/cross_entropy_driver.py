from tensorflow import Session

from common.car import Car
from common.car_state import CarState
from common.driver import Driver
from common.point import Point
from common.track import Track
from network.fully_connected_layer import FullyConnectedLayer as Layer
from network.tensor_network import TensorNetwork as Network

import tensorflow as tf
import numpy as np


class CrossEntropyDriver:

    @staticmethod
    def steer_gran():
        return 10

    @staticmethod
    def acceleration_gran():
        return 10

    def __init__(self, hidden_layers, track: Track, car: Car):
        self.car = car
        self.track = track
        dimensions = [len(car.lidar_origins()) * 2 + 2] + hidden_layers + \
                     [CrossEntropyDriver.steer_gran() + CrossEntropyDriver.acceleration_gran()]
        layers = [Layer(dimensions[i-1], dimensions[i], tf.nn.sigmoid) for i in range(1, len(dimensions))]

        network = Network(layers)
        self.w = tf.constant(np.random.rand(network.weights_size()), tf.float64)
        self.x = tf.placeholder(tf.float64, shape=[network.in_size])
        self.nn = Network(layers).build(self.x, self.w)

    def initial_state(self) -> CarState:
        return CarState(self.track.start_point, self.track.start_angle, (0, 0))

    def action(self, session: Session, state: CarState) -> CarState:
        relative_points = [state.center_of_mass + p.rotate(state.direction) for p in self.car.lidar_origins()]
        lidar_points = [self.track.lidar(state.center_of_mass, p) for p in relative_points]
        distances = [self.cap_n_scale(Point.distance(state.center_of_mass, p[1]), 200) for p in lidar_points]
        center_idx = self.track.get_section_index(state.center_of_mass)
        track_distances = [self.scale(self.track.distance((center_idx, state.center_of_mass), p), 60)
                           for p in lidar_points]
        inputs = distances + track_distances + [state.momentum[0], state.momentum[1]]
        output = session.run(self.nn, feed_dict={self.x: inputs})

        return self.car.drive(output[0], output[1], state)

    @staticmethod
    def choose_action(distribution):
        s = sum(distribution)
        p = map(lambda x: x*(1/s), distribution)
        i = np.random.choice(range(len(distribution)), p=p)
        r = i * (2 / (len(distribution) - 1))
        return r - 1

    @staticmethod
    def cap_n_scale(x: float, m: float) -> float:
        return min(x, m) / m

    @staticmethod
    def scale(x: float, m: float) -> float:
        return x / m

