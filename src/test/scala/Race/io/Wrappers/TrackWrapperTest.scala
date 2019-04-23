package Race.io.Wrappers

import Race.Tracks.Dictionary
import org.scalatest.{FunSuite, Matchers}

class TrackWrapperTest extends FunSuite with Matchers {

  test ("Track to Byte") {
    val bytes = TrackWrapper(Dictionary.SideEight).asBytes()

    bytes.length shouldBe 416
  }

  test ("Track to Byte round trip") {
    val bytes = TrackWrapper(Dictionary.SideEight).asBytes()

    val track = TrackWrapper.fromBytes(bytes)

    track.points.zip(Dictionary.SideEight.points).foreach{
      case ((p1a, p2a), (p1b, p2b)) =>
        p1a.x shouldBe p1b.x
        p1a.y shouldBe p1b.y
        p2a.x shouldBe p2b.x
        p2a.y shouldBe p2b.y
    }
  }

  test ("Track to Text round trip") {
    val text = TrackWrapper(Dictionary.SideEight).asText()

    val track = TrackWrapper.fromText(text)

    track.points.zip(Dictionary.SideEight.points).foreach{
      case ((p1a, p2a), (p1b, p2b)) =>
        p1a.x shouldBe p1b.x
        p1a.y shouldBe p1b.y
        p2a.x shouldBe p2b.x
        p2a.y shouldBe p2b.y
    }
  }

}
