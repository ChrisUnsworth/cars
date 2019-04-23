package Race.common

import Race.Tracks.Dictionary
import org.scalatest.{FunSuite, Matchers}

class TrackTest extends FunSuite with Matchers {

  test ("Boundry Segments") {
    val segments = Track(Dictionary.SimpleRing).boundarySegments

    segments.size shouldBe 18

    segments.flatMap(s => s.lineSlope).size  shouldBe 18
  }

  test (" center Segments") {
    val segments = Track(Dictionary.SimpleRing).centerLine

    segments.size shouldBe 9
  }

  test ("startLineSideCheck") {
     val sfl = LineSegment(Point(225, 425), Point(325, 425))

    Track.startLineSideCheck(Point(300, 500), sfl) shouldBe -7500
    Track.startLineSideCheck(Point(300, 400), sfl) shouldBe 2500
    Track.startLineSideCheck(Point(300, 450), sfl) shouldBe -2500
  }

  test ("Lap Change") {
    val track = Track(Dictionary.SimpleRing)

    track.lapChange(Point(300, 450), Point(300, 500)) shouldBe 0
    track.lapChange(Point(300, 450), Point(300, 400)) shouldBe 1
    track.lapChange(Point(300, 400), Point(300, 450)) shouldBe -1
  }
}
