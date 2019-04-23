package Race.evolution.Cars

import Race.common.Point
import org.scalatest.{FunSuite, Matchers}

class SimpleCarStateTest extends FunSuite with Matchers {

  test ("Byte round trip") {
    val state1 = SimpleCarState(Point(23, 34), -78.3, (-87, 0.0))

    val bytes = state1.asBytes()

    bytes.length shouldBe SimpleCarState.byteSize

    val state2 = SimpleCarState(bytes)

    state1 shouldBe state2
  }

  test ("Text round trip") {

    val state1 = SimpleCarState(Point(23, 34), -78.3, (-87, 0.0))

    val text = state1.asText()

    val state2 = SimpleCarState(text)

    state1 shouldBe state2
  }

}
