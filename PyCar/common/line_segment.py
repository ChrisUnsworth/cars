from common.line_slope_intercept import LineSlopeIntercept
from common.line_standard_form import LineStandardForm
from common.point import Point
from math import pi


class LineSegment:
    def __init__(self, _p1: Point, _p2: Point):
        self.p1 = _p1
        self.p2 = _p2
        self._line_slope = None
        self._line_standard = None

    def length(self) -> float:
        return Point.distance(self.p1, self.p2)

    def length_from_start(self, p: Point) -> float:
        x = self.closest_point(p)
        return Point.distance(self.p1, x) if x is not None else None

    def length_from_end(self, p: Point) -> float:
        x = self.closest_point(p)
        return Point.distance(self.p2, x) if x is not None else None

    @property
    def line_slope(self) -> LineSlopeIntercept:
        if self._line_slope is None:
            self._line_slope = LineSlopeIntercept.try_make(self.p1, self.p2)
        return self._line_slope

    @property
    def line_standard(self) -> LineStandardForm:
        if self._line_standard is None:
            self._line_standard = LineStandardForm.try_make(self.p1, self.p2)
        return self._line_standard

    def in_box(self, p: Point) -> bool:
        return Point.in_box(p, self.p1, self.p2)

    def intersect(self, other):
        l1 = self.line_slope
        if isinstance(other, LineSlopeIntercept):
            l2 = other
            def check_l2(x): return True
        else:
            l2 = other.line_slope
            def check_l2(x): return other.in_box(x)

        if l1 is None or l2 is None:
            return None
        intersect = l1.intersect(l2)
        if intersect is not None and self.in_box(intersect) and check_l2(intersect):
            return intersect
        else:
            return None

    def closest_point(self, p: Point):
        lsf = self.line_standard
        if lsf is None:
            return None
        cp = lsf.closest_point(p)
        if cp is not None and self.in_box(cp):
            return cp
        return None

    def angle(self) -> float:
        ls = self.line_slope
        if ls is None:
            return 0
        angle = ls.angle()
        if self.p2.x() > self.p1.x(): return angle
        else:
            if angle > 0:
                return (2 * pi) - angle
            else:
                return (-2 * pi) - angle

