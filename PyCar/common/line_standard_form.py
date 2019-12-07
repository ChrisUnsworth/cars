from common.point import Point
from math import pow


class LineStandardForm:
    def __init__(self, *args):
        if len(args) == 1:
            self.is_vertical = True
            self.x = float(args[0])
        if len(args) == 3:
            self.is_vertical = False
            self.a = float(args[0])
            self.b = float(args[1])
            self.c = float(args[2])

    def closest_real_point(self, p: Point) -> (float, float):
        if self.is_vertical:
            return self.x, p.y()
        (a, b, c, x, y) = self.a, self.b, -self.c, p.x(), p.y()
        _x = (((b*x - a*y) * b) - (a * c)) / (pow(a, 2) + pow(b, 2))
        _y = (((-b*x + a*y) * a) - (b * c)) / (pow(a, 2) + pow(b, 2))
        return _x, _y

    def closest_point(self, p: Point) -> Point:
        x, y = self.closest_real_point(p)
        return Point(round(x), round(y))

    @staticmethod
    def try_make(p1: Point, p2: Point):
        if p1 == p2:
            return None
        if p1.x() == p2.x():
            return LineStandardForm(p1.x())
        m = (p2.y() - p1.y()) / (p2.x() - p1.x())
        a = -m
        b = 1
        c = a * p1.x() + p1.y()
        return LineStandardForm(a, b, c)
