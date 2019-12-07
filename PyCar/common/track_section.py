from PyQt5.QtCore import Qt
from PyQt5.QtGui import QPolygon

from common.line_segment import LineSegment
from common.line_slope_intercept import LineSlopeIntercept
from common.point import Point


class TrackSection:
    def __init__(self, start, end):
        self.start = Point.midpoint(start[0], start[1])
        self.end = Point.midpoint(end[0], end[1])
        self.polygon = QPolygon([start[0], end[0], end[1], start[1]])
        self.length = Point.distance(Point.midpoint(start[0], start[1]), Point.midpoint(end[0], end[1]))
        self.boundaries = [LineSegment(start[0], end[0]), LineSegment(start[1], end[1])]

    def is_in(self, p: Point) -> bool:
        return self.polygon.containsPoint(p, Qt.OddEvenFill)

    def distance_from_start(self, p: Point):
        a = Point.distance(self.start, p)
        b = self.length - Point.distance(self.end, p)
        return (a + b) / 2.0

    def distance_to_end(self, p: Point):
        a = Point.distance(self.end, p)
        b = self.length - Point.distance(self.start, p)
        return (a + b) / 2.0

    def boundary_intersects(self, line: LineSlopeIntercept):
        return [p for p in [l.intersect(line) for l in self.boundaries] if p is not None]
