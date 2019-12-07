from common.point import Point
from common.track import Track


class C:
    @staticmethod
    def track():
        return Track([
            (Point(225, 425), Point(325, 425)),
            (Point(225, 325), Point(325, 375)),
            (Point(325, 225), Point(350, 350)),
            (Point(425, 225), Point(446, 315)),
            (Point(470, 179), Point(610, 170)),
            (Point(432, 149), Point(505, 71)),
            (Point(356, 144), Point(337, 26)),
            (Point(280, 169), Point(265, 46)),
            (Point(207, 230), Point(93, 167)),
            (Point(162, 364), Point(66, 377)),
            (Point(206, 501), Point(156, 647)),
            (Point(277, 557), Point(386, 646)),
            (Point(331, 550), Point(452, 554)),
            (Point(300, 517), Point(462, 511)),
            (Point(252, 460), Point(391, 475))
        ])
