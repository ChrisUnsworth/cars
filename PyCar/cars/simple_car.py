from math import radians, pi, sin, cos

from common.car import Car
from common.car_state import CarState
from common.point import Point


class SimpleCar(Car):
    def __init__(self, max_acceleration=10, max_decel=7, max_steer=radians(15), preserved_momentum=0.7):
        self.max_acceleration = max_acceleration
        self.max_decel = max_decel
        self.max_steer = max_steer
        self.preserved_momentum = preserved_momentum

    def drive(self, acceleration, steer, state: CarState) -> CarState:
        capped_steer = max(-1.0, min(1.0, steer)) * self.max_steer + state.direction

        if capped_steer > pi:
            new_direction = -pi + (capped_steer - pi)
        elif capped_steer < -pi:
            new_direction = pi + (capped_steer + pi)
        else:
            new_direction = capped_steer

        capped_acc = max(-1.0, min(1.0, acceleration))

        if capped_acc > 0:
            acc_force = self.max_acceleration * capped_acc
        else:
            acc_force = self.max_decel * capped_acc

        x, y = SimpleCar.force_vector(acc_force, new_direction)

        new_momentum = ((state.momentum[0] * self.preserved_momentum) + x,
                        (state.momentum[1] * self.preserved_momentum) + y)

        new_position = state.center_of_mass + Point(round(new_momentum[0]), round(new_momentum[1]))

        return CarState(new_position, new_direction, new_momentum)

    def lidar_origins(self):
        return [Point(0, -1000),
                Point(-500, -1000),
                Point(500, -1000),
                Point(-1000, -700),
                Point(1000, -700),
                Point(-1000, 0),
                Point(1000, 0)]

    @staticmethod
    def force_vector(magnitude: float, rad: float) -> (float, float):
        return sin(rad) * magnitude, cos(rad) * -magnitude
