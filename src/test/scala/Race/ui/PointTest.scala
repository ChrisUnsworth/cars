package Race.ui

import Race.common.Point
import org.scalatest.{FunSuite, Matchers}

class PointTest extends FunSuite with Matchers {

  test ("mid point") {
    Point.midPoint(Point(0,0), Point(0,0)) shouldBe Point(0,0)
    Point.midPoint(Point(0,0), Point(0,10)) shouldBe Point(0,5)
    Point.midPoint(Point(0,2), Point(0,10)) shouldBe Point(0,6)
    Point.midPoint(Point(0,-10), Point(0,10)) shouldBe Point(0,0)
    Point.midPoint(Point(2,2), Point(10,10)) shouldBe Point(6,6)
  }

  test ("length") {
    Point.distance(Point(0,0), Point(0,0)) shouldBe 0.0
    Point.distance(Point(0,0), Point(0,10)) shouldBe 10
    Point.distance(Point(10,0), Point(0,0)) shouldBe 10
    Point.distance(Point(10,0), Point(0,10)).toInt shouldBe 14
    Point.distance(Point(0,10), Point(10,0)).toInt shouldBe 14
  }

  test ("angle from origin") {

    Math.toDegrees(Point(0, 10).angleFromOrigin()) shouldBe 90
    Math.toDegrees(Point(10, 10).angleFromOrigin()) shouldBe 45
    Math.toDegrees(Point(10, 0).angleFromOrigin()) shouldBe 0
    Math.toDegrees(Point(10, -10).angleFromOrigin()) shouldBe -45
    Math.toDegrees(Point(0, -10).angleFromOrigin()) shouldBe -90
    Math.toDegrees(Point(-10, -10).angleFromOrigin()) shouldBe -135
    Math.toDegrees(Point(-10, 0).angleFromOrigin()) shouldBe 180
    Math.toDegrees(Point(-10, 10).angleFromOrigin()) shouldBe 135
  }

}
