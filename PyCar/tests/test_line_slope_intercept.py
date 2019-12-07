from unittest import TestCase

from common.line_slope_intercept import LineSlopeIntercept, intersect
from common.point import Point


class TestLineSlopeIntercept(TestCase):
    def test_vertical(self):
        line = LineSlopeIntercept.try_make(Point(1, 0), Point(1, 10))
        self.assertTrue(line.is_vertical)
        self.assertEqual(line.x, 1)

    def test_horizontal(self):
        line = LineSlopeIntercept.try_make(Point(0, 0), Point(10, 0))
        self.assertEqual(line.b, 0)
        self.assertFalse(line.is_vertical)
        line = LineSlopeIntercept.try_make(Point(1, 5), Point(5, 5))
        self.assertEqual(line.b, 5)
        self.assertFalse(line.is_vertical)

    def test_intercept(self):
        v_line = LineSlopeIntercept.try_make(Point(0, 0), Point(0, 10))
        h_line = LineSlopeIntercept.try_make(Point(1, 5), Point(5, 5))
        point = intersect(v_line, h_line)
        self.assertEqual(point, Point(0, 5))

    def test_intercept2(self):
        line1 = LineSlopeIntercept.try_make(Point(0, 2), Point(2, 0))
        line2 = LineSlopeIntercept.try_make(Point(0, 0), Point(5, 5))
        point = intersect(line1, line2)
        self.assertEqual(point, Point(1, 1))
