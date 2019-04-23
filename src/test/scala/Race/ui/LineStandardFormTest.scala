package Race.ui

import Race.common.{LineStandardForm, Point}
import org.scalatest.{FunSuite, Matchers}

class LineStandardFormTest extends FunSuite with Matchers {

  test ("from point") {

    val p1 = Point(-1, 6)
    val p2 = Point(5, -4)

    val line1 = LineStandardForm(p1, p2)
    val line2 = LineStandardForm(p2, p1)

    line1.a shouldBe line2.a +- 0.0000001
    line1.b shouldBe line2.b +- 0.0000001
    line1.c shouldBe line2.c +- 0.0000001
  }

  test ("closest real point") {

    val p1 = Point(0, 0)
    val p2 = Point(10, 10)

    val line = LineStandardForm(p1, p2)

    LineStandardForm.closestRealPoint(line, Point(0, 10)) shouldBe (5, 5)
    LineStandardForm.closestRealPoint(line, Point(1, 9)) shouldBe (5, 5)
    LineStandardForm.closestRealPoint(line, Point(1, 1)) shouldBe (1, 1)
    LineStandardForm.closestRealPoint(line, Point(20, 21)) shouldBe (20.5, 20.5)
  }
}
