from common.point import Point
from math import radians, atan


class LineSlopeIntercept:
    def __init__(self, *args):
        if len(args) == 1:
            self.is_vertical = True
            self.x = args[0]
            self.b = float('Inf')
            return
        if len(args) == 2:
            self.is_vertical = False
            self.m = args[0]
            self.b = args[1]

    def angle(self) -> float:
        if self.is_vertical:
            return radians(90)
        else:
            return atan(self.m)

    def intersect(self, other):
        return intersect(self, other)

    @staticmethod
    def try_make(p1: Point, p2: Point):
        if p1 == p2:
            return None
        if p1.x() == p2.x():
            return LineSlopeIntercept(p1.x())
        m = (p2.y() - p1.y()) / float(p2.x() - p1.x())
        b = p1.y() - (m * p1.x())
        return LineSlopeIntercept(m, b)


def intersect(l1: LineSlopeIntercept, l2: LineSlopeIntercept):
    if l1.b == l2.b:
        return None

    if l1.is_vertical:
        x = round(l1.x)
        y = round(l2.m * l1.x + l2.b)
        return Point(x, y)

    if l2.is_vertical:
        return Point(round(l2.x), round(l1.m * l2.x + l1.b))

    if l1.m == l2.m:
        return None

    x = (l2.b - l1.b) / (l1.m - l2.m)
    y = l1.m * x + l1.b

    return Point(round(x), round(y))


