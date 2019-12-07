from common.track import Track
from common.point import Point


class SimpleRing:
    @staticmethod
    def track():
        return Track([
            (Point(225, 425), Point(325, 425)),
            (Point(225, 325), Point(325, 375)),
            (Point(325, 225), Point(350, 350)),
            (Point(425, 225), Point(400, 350)),
            (Point(525, 325), Point(425, 375)),
            (Point(525, 525), Point(425, 475)),
            (Point(425, 625), Point(400, 500)),
            (Point(325, 625), Point(350, 500)),
            (Point(225, 525), Point(325, 475))
        ])