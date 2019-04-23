package Race.Training.ThreeCarRace

import Race.common.Point
import org.scalatest.{FunSuite, Matchers}

class ThreeCarRaceTest extends FunSuite with Matchers {

  test ("radar") {

    ThreeCarRace.radarSector(Point(1, 0)) shouldBe (1, 1.0)
    ThreeCarRace.radarSector(Point(-1, 0)) shouldBe (4, 1.0)
    ThreeCarRace.radarSector(Point(1, 1)) shouldBe (4, 1.0)
  }

}
