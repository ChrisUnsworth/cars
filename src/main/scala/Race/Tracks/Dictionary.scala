package Race.Tracks

import Race.common.{Point, Track}

object Dictionary {
  def trackNames: Iterable[String] = tracks.keys
  def track(name: String): Track = Track(tracks(name))

  def SimpleRingPoints:Iterable[(Point, Point)] = tracks("Simple Ring")
  def SimpleRing: Track = Track(tracks("Simple Ring"))
  def CTrack: Track = Track(tracks("C"))
  def SideEight: Track = Track(tracks("Side Eight"))
  def Mmmm: Track = Track(tracks("Mmmm"))
  def BigC: Track = Track(tracks("BigC"))

  private val tracks: Map[String, Iterable[(Point, Point)]] = Map(
    "BigC" -> Iterable(
      (Point(225, 425), Point(362, 409)),
      (Point(225, 325), Point(325, 375)),
      (Point(325, 225), Point(350, 350)),
      (Point(450, 217), Point(488, 303)),
      (Point(575, 210), Point(633, 299)),
      (Point(681, 201), Point(961, 270)),
      (Point(792, 153), Point(975, 124)),
      (Point(616, 160), Point(918, 34)),
      (Point(533, 138), Point(711, 52)),
      (Point(432, 149), Point(505, 71)),
      (Point(356, 144), Point(337, 26)),
      (Point(280, 169), Point(265, 46)),
      (Point(207, 230), Point(93, 167)),
      (Point(162, 364), Point(66, 377)),
      (Point(206, 501), Point(156, 647)),
      (Point(306, 553), Point(303, 603)),
      (Point(460, 582), Point(426, 629)),
      (Point(619, 580), Point(515, 694)),
      (Point(732, 580), Point(737, 678)),
      (Point(838, 588), Point(992, 724)),
      (Point(815, 535), Point(999, 467)),
      (Point(691, 546), Point(822, 383)),
      (Point(567, 558), Point(621, 395)),
      (Point(335, 515), Point(471, 404))),
    "Mmmm" -> Iterable(
      (Point(47, 462), Point(166, 485)),
      (Point(51, 345), Point(177, 352)),
      (Point(58, 207), Point(156, 265)),
      (Point(78, 78), Point(153, 182)),
      (Point(176, 69), Point(174, 243)),
      (Point(219, 192), Point(206, 365)),
      (Point(232, 320), Point(239, 488)),
      (Point(269, 242), Point(289, 348)),
      (Point(270, 149), Point(318, 298)),
      (Point(306, 67), Point(329, 252)),
      (Point(394, 230), Point(334, 290)),
      (Point(391, 299), Point(337, 352)),
      (Point(409, 333), Point(313, 422)),
      (Point(437, 358), Point(370, 453)),
      (Point(584, 347), Point(414, 421)),
      (Point(1060, 568), Point(876, 582)),
      (Point(1021, 690), Point(855, 630)),
      (Point(1018, 742), Point(746, 652)),
      (Point(86, 725), Point(169, 614))),
    "Simple Ring" -> Iterable(
      (Point(225, 425), Point(325, 425)),
      (Point(225, 325), Point(325, 375)),
      (Point(325, 225), Point(350, 350)),
      (Point(425, 225), Point(400, 350)),
      (Point(525, 325), Point(425, 375)),
      (Point(525, 525), Point(425, 475)),
      (Point(425, 625), Point(400, 500)),
      (Point(325, 625), Point(350, 500)),
      (Point(225, 525), Point(325, 475))),
    "C" -> Iterable(
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
      (Point(252, 460), Point(391, 475))),
    "Side Eight" -> Iterable(
      (Point(71, 422), Point(166, 425)),
      (Point(87, 320), Point(199, 343)),
      (Point(222, 178), Point(229, 280)),
      (Point(312, 195), Point(289, 275)),
      (Point(343, 240), Point(304, 325)),
      (Point(373, 285), Point(344, 415)),
      (Point(416, 333), Point(394, 429)),
      (Point(462, 265), Point(467, 403)),
      (Point(531, 208), Point(498, 354)),
      (Point(615, 154), Point(594, 270)),
      (Point(679, 159), Point(625, 293)),
      (Point(777, 353), Point(651, 378)),
      (Point(876, 435), Point(682, 424)),
      (Point(859, 518), Point(660, 480)),
      (Point(844, 611), Point(699, 530)),
      (Point(830, 705), Point(701, 587)),
      (Point(751, 740), Point(654, 595)),
      (Point(618, 745), Point(593, 601)),
      (Point(506, 614), Point(549, 485)),
      (Point(476, 573), Point(476, 466)),
      (Point(426, 583), Point(413, 483)),
      (Point(396, 638), Point(363, 528)),
      (Point(326, 685), Point(319, 579)),
      (Point(264, 728), Point(281, 599)),
      (Point(148, 678), Point(211, 563)),
      (Point(84, 518), Point(179, 495)))
  )
}
