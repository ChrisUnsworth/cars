from PyQt5.QtCore import QPoint
from math import cos, sin, pi, atan, sqrt, pow, fabs


class Point(QPoint):
    def __init__(self, x: int, y: int):
        super().__init__(x, y)

    def __add__(self, other: QPoint):
        return Point(self.x() + other.x(), self.y() + other.y())

    def __sub__(self, other: QPoint):
        return Point(self.x() - other.x(), self.y() - other.y())

    def rotate(self, *args):
        """usage:
        rotate(self, rad) rad = angle in radians
        rotate(self, origin, rad) origin = center of rotation, rad = angle in radians"""
        if len(args) == 1:
            return Point(int(round((self.x() * cos(args[0])) - (self.y() * sin(args[0])))),
                         int(round((self.y() * cos(args[0])) + (self.x() * sin(args[0])))))
        if len(args) == 2 and isinstance(args[0], QPoint):
            return (self - args[0]).rotate(args[1]) + self

    def angle_from_origin(self) -> float:
        """angle from the the x axis"""
        if self.x() == 0:
            if self.y() > 0:
                return pi * 0.5
            else:
                return pi * -0.5

        angle = atan(self.y() / self.x())

        if self.x() > 0:
            return angle

        if self.y() < 0:
            return - pi + angle
        else:
            return pi + angle

    def distance_from_origin(self) -> float:
        return sqrt(pow(self.x(), 2) + pow(self.y(), 2))

    @staticmethod
    def distance(p1: QPoint, p2: QPoint) -> float:
        return sqrt(pow(fabs(p1.x() - p2.x()), 2) + pow(fabs(p1.y() - p2.y()), 2))

    @staticmethod
    def midpoint(p1: QPoint, p2: QPoint):
        return Point((p1.x() + p2.x()) / 2, (p1.y() + p2.y()) / 2)

    @staticmethod
    def in_box(p: QPoint, p1: QPoint, p2: QPoint) -> bool:
        if (p.x() > p1.x()) and (p.x() > p2.x()):
            return False
        if (p.x() < p1.x()) and (p.x() < p2.x()):
            return False
        if (p.y() > p1.y()) and (p.y() > p2.y()):
            return False
        if (p.y() < p1.y()) and (p.y() < p2.y()):
            return False
        return True
