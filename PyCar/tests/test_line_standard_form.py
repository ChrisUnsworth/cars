from unittest import TestCase

from common.line_standard_form import LineStandardForm
from common.point import Point


class TestLineStandardForm(TestCase):
    def test_closest_real_point(self):
        p1 = Point(0, 0)
        p2 = Point(10, 10)

        line = LineStandardForm.try_make(p1, p2)

        self.assertEqual(line.closest_real_point(Point(0, 10)), (5, 5))
        self.assertEqual(line.closest_real_point(Point(1, 9)), (5, 5))
        self.assertEqual(line.closest_real_point(Point(1, 1)), (1, 1))
        self.assertEqual(line.closest_real_point(Point(20, 21)), (20.5, 20.5))

    def test_from_point(self):
        p1 = Point(-1, 6)
        p2 = Point(5, -4)

        line1 = LineStandardForm.try_make(p1, p2)
        line2 = LineStandardForm.try_make(p2, p1)

        self.assertAlmostEqual(line1.a, line2.a, 7)
        self.assertAlmostEqual(line1.b, line2.b, 7)
        self.assertAlmostEqual(line1.c, line2.c, 7)
