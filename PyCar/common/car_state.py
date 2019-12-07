from PyQt5.QtGui import QPolygon

from common.point import Point


class CarState:
    def __init__(self, center_of_mass: Point, direction: float, momentum: (float, float)):
        self.center_of_mass = center_of_mass
        self.momentum = momentum
        self.direction = direction

    def position(self) -> QPolygon:
        points = [Point(-5, 10), Point(5, 10), Point(5, -10), Point(-5, -10)]
        points = map(lambda p: p.rotate(self.direction) + self.center_of_mass, points)
        return QPolygon(points)
