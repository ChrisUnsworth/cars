package Race.common

import org.scalatest.{FunSuite, Matchers}

class LineSlopeInterceptTest extends FunSuite with Matchers {

  test ("vertical") {
    val line = LineSlopeIntercept(Point(0, 0), Point(0, 10))
    line.isVertical shouldBe true
  }

  test ("horizontal") {
    val line = LineSlopeIntercept(Point(0, 0), Point(10, 0))
    line.b shouldBe 0
    line.isVertical shouldBe false
  }

  test ("Intersept") {
    val vLine = LineSlopeIntercept(Point(0, 0), Point(0, 10))
    val hLine = LineSlopeIntercept(Point(1, 5), Point(5, 5))

    val intersect = vLine.intersect(hLine)

    intersect shouldBe Some(Point(0, 5))
  }
}
