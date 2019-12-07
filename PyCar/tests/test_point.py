from unittest import TestCase

from common.point import Point
from math import radians, degrees


class TestPoint(TestCase):
    def test_add(self):
        p = Point(1, 2) + Point(3, 4)
        self.assertEqual(p.x(), 4)
        self.assertEqual(p.y(), 6)

    def test_sub(self):
        p = Point(6, 6) - Point(3, 4)
        self.assertEqual(p.x(), 3)
        self.assertEqual(p.y(), 2)

    def test_rotate(self):
        p = Point(0, 1).rotate(radians(90))
        self.assertEqual(p.x(), -1)
        self.assertEqual(p.y(), 0)
        p = Point(0, 1).rotate(radians(-90))
        self.assertEqual(p.x(), 1)
        self.assertEqual(p.y(), 0)
        p = Point(1, 0).rotate(radians(90))
        self.assertEqual(p.x(), 0)
        self.assertEqual(p.y(), 1)
        p = Point(10, 0).rotate(radians(45))
        self.assertEqual(p.x(), 7)
        self.assertEqual(p.y(), 7)

    def test_angle_from_origin(self):
        self.assertEqual(degrees(Point(0, 10).angle_from_origin()), 90)
        self.assertEqual(degrees(Point(10, 10).angle_from_origin()), 45)
        self.assertEqual(degrees(Point(10, 0).angle_from_origin()), 0)
        self.assertEqual(degrees(Point(10, -10).angle_from_origin()), -45)
        self.assertEqual(degrees(Point(0, -10).angle_from_origin()), -90)
        self.assertEqual(degrees(Point(-10, -10).angle_from_origin()), -135)
        self.assertEqual(degrees(Point(-10, 0).angle_from_origin()), 180)
        self.assertEqual(degrees(Point(-10, 10).angle_from_origin()), 135)

    def test_distance(self):
        self.assertEqual(Point.distance(Point(0, 0), Point(0, 0)), 0.0)
        self.assertEqual(Point.distance(Point(0, 0), Point(0, 10)), 10)
        self.assertEqual(Point.distance(Point(10, 0), Point(0, 0)), 10)
        self.assertEqual(int(Point.distance(Point(10, 0), Point(0, 10))), 14)
        self.assertEqual(int(Point.distance(Point(0, 10), Point(10, 0))), 14)

    def test_midpoint(self):
        self.assertEqual(Point.midpoint(Point(0, 0), Point(0, 0)), Point(0, 0))
        self.assertEqual(Point.midpoint(Point(0, 0), Point(0, 10)), Point(0, 5))
        self.assertEqual(Point.midpoint(Point(0, 2), Point(0, 10)), Point(0, 6))
        self.assertEqual(Point.midpoint(Point(0, -10), Point(0, 10)), Point(0, 0))
        self.assertEqual(Point.midpoint(Point(2, 2), Point(10, 10)), Point(6, 6))
