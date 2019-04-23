package Race.evolution.Cars

import Race.common.Point
import org.scalatest.{FunSuite, Matchers}

class SimpleCarRouteTest extends FunSuite with Matchers {

  test ("Byte round trip") {
    val states = List(
      SimpleCarState(Point(23, 34), -78.3, (-87, 0.0)),
      SimpleCarState(Point(65, 87), 12.9, (46.99999, 7.5)),
      SimpleCarState(Point(3534534, 1), 12.4, (-46.99499, 34.5)))

    val route1 = SimpleCarRoute(states)

    val bytes: Array[Byte] = route1.asBytes()

    val route2 = SimpleCarRoute(bytes)

    for ((s1, s2) <- route1.states.zip(route2.states)){
      s2 shouldBe s1
    }
  }

  test ("Text round trip") {
    val states = List(
      SimpleCarState(Point(23, 34), -78.3, (-87, 0.0)),
      SimpleCarState(Point(65, 87), 12.9, (46.99999, 7.5)),
      SimpleCarState(Point(3534534, 1), 12.4, (-46.99499, 34.5)))

    val route1 = SimpleCarRoute(states)

    val text: String = route1.asText()

    val route2 = SimpleCarRoute(text)

    for ((s1, s2) <- route1.states.zip(route2.states)){
      s2 shouldBe s1
    }
  }

}
