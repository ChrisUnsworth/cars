from unittest import TestCase

from common.line_segment import LineSegment
from common.point import Point
from tracks.simple_ring import SimpleRing
from common.track import Track


class TestTrack(TestCase):
    def test_boundary_segments(self):
        segments = SimpleRing.track().boundary_segments
        self.assertEqual(18, len(segments))
        slopes = list(map(lambda s: s.line_slope, segments))
        self.assertEqual(18, len(slopes))
        self.assertFalse(None in slopes)

    def test_start_line_side_check(self):
        sfl = LineSegment(Point(225, 425), Point(325, 425))
        self.assertEqual(-7500, Track.side_check(Point(300, 500), sfl))
        self.assertEqual(2500, Track.side_check(Point(300, 400), sfl))
        self.assertEqual(-2500, Track.side_check(Point(300, 450), sfl))

    def test_lap_change(self):
        track = SimpleRing.track()

        self.assertEqual(0, track.lap_change(Point(300, 450), Point(300, 500)))
        self.assertEqual(1, track.lap_change(Point(300, 450), Point(300, 400)))
        self.assertEqual(-1, track.lap_change(Point(300, 400), Point(300, 450)))
