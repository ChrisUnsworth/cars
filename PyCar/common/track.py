from PyQt5.QtCore import Qt
from PyQt5.QtGui import QPolygon
from itertools import accumulate
from math import fabs

from common.line_segment import LineSegment
from common.line_slope_intercept import LineSlopeIntercept
from common.point import Point
from common.track_section import TrackSection


class Track:
    def __init__(self, _points: list):
        """__init(self, _points)

        _points -- list of tuples of points"""
        assert (isinstance(_points[0], tuple) and isinstance(_points[0][0], Point)), \
            "_points should be a non empty list of tuples of Points"
        self.points = _points
        b1 = QPolygon([t[0] for t in _points])
        b2 = QPolygon([t[1] for t in _points])
        clockwise = b1.containsPoint(b2.point(0), Qt.OddEvenFill)
        if clockwise:
            self.start_finish_line = LineSegment(_points[0][0], _points[0][1])
            self.outer_boundary = b1
            self.inner_boundary = b2
        else:
            self.start_finish_line = LineSegment(_points[0][1], _points[0][0])
            self.outer_boundary = b2
            self.inner_boundary = b1
        first_corner = Point.midpoint(_points[1][0], _points[1][1])
        d = Track.side_check(first_corner, self.start_finish_line)
        self.start_direction = d / fabs(d)
        self.start_angle = self.start_finish_line.angle() * self.start_direction
        self.start_point = Point(0, -5).rotate(self.start_angle) + Point.midpoint(_points[0][0], _points[0][1])
        self.sections = [TrackSection(_points[i-1], _points[i]) for i in range(1, len(_points))] + \
                        [TrackSection(_points[-1], _points[0])]
        #self.boundary_segments = Track.calc_boundary_segments(_points)

        self.lap_length = [s.length for s in self.sections]

    def lap_change(self, p1: Point, p2: Point):
        if self.start_finish_line.intersect(LineSegment(p1, p2)) is None:
            return 0
        if self.at_start_of_lap(p2):
            return 1
        if self.at_start_of_lap(p1):
            return -1
        return 0

    def at_start_of_lap(self, p: Point):
        d = self.side_check(p, self.start_finish_line)
        if d == 0:
            return False
        return (d / fabs(d)) == self.start_direction

    def lidar(self, p1: Point, p2: Point) -> (int, Point):
        line = LineSlopeIntercept.try_make(p1, p2)
        hits = [(i, p) for i in range(len(self.sections)) for p in self.sections[i].boundary_intersects(line)]
        hits = [(i, p) for (i, p) in hits if Point.in_box(p2, p1, p) or Point.in_box(p, p1, p2)]
        if not hits:
            return -1, p2
        return min(hits, key=lambda t: Point.distance(p1, t[1]))

    def out_of_bounds(self, p: Point) -> bool:
        return self.inner_boundary.containsPoint(p, Qt.OddEvenFill) or not self.outer_boundary.containsPoint(p, Qt.OddEvenFill)

    def distance(self, a, b) -> float:
        p1, s1_i = (a, self.get_section_index(a)) if isinstance(a, Point) else (a[1], a[0])
        p2, s2_i = (b, self.get_section_index(b)) if isinstance(b, Point) else (b[1], b[0])
        if s1_i is None or s2_i is None:
            return 0
        if s1_i == s2_i:
            section = self.sections[s1_i]
            d1, d2 = section.distance_from_start(p1), section.distance_from_start(p2)
            return d2 - d1
        if self.start_finish_line.intersect(LineSegment(p1, p2)) is not None:
            if s1_i < s2_i:
                sign = -1.0
                max_id, max_point = s2_i, p2
                min_id, min_point = s1_i, p1
            else:
                sign = 1.0
                max_id, max_point = s1_i, p1
                min_id, min_point = s2_i, p2

            distance = (self.sections[max_id].distance_to_end(max_point)) + \
                sum([self.sections[i].length for i in range(max_id + 1, len(self.sections))]) + \
                sum([self.sections[i].length for i in range(0, min_id)]) + \
                self.sections[min_id].distance_from_start(min_point)
            return distance * sign

        if s1_i > s2_i:
            sign = -1.0
            max_id, max_point = s2_i, p2
            min_id, min_point = s1_i, p1
        else:
            sign = 1.0
            max_id, max_point = s1_i, p1
            min_id, min_point = s2_i, p2

        distance = (self.sections[min_id].distance_to_end(min_point)) + \
            sum([self.sections[i].length for i in range(min_id + 1, max_id)]) + \
            self.sections[max_id].distance_from_start(max_point)
        return distance * sign

    def get_section_index(self, p: Point):
        return next((i for i in range(len(self.sections)) if self.sections[i].is_in(p)), None)

    def run_length(self, run: list):
        return sum([self.distance(run[i].center_of_mass, run[i+1].center_of_mass) for i in range(len(run)-1)])

    @staticmethod
    def side_check(p: Point, sfl: LineSegment) -> int:
        a, b = sfl.p1, sfl.p2
        return (p.x() - a.x()) * (b.y() - a.y()) - (p.y() - a.y()) * (b.x() - a.x())

    @staticmethod
    def calc_boundary_segments(_points):
        return [LineSegment(_points[i][j], _points[(i + 1) % len(_points)][j])
                for i in range(len(_points))
                for j in range(2)]

    @staticmethod
    def calc_sections(points):
        return [QPolygon([points[i][0],
                          points[(i + 1) % len(points)][0],
                          points[(i + 1) % len(points)][1],
                          points[i][1]])
                for i in range(len(points))]
