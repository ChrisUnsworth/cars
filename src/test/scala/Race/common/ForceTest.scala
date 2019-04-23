package Race.common

import org.scalatest.{FunSuite, Matchers}

class ForceTest extends FunSuite with Matchers {

  test ("testy" ) {

    solution(Array(Point2D(-1, -2), Point2D(1, 2), Point2D(2, 4), Point2D(2, -2), Point2D(-3, 2))) shouldBe 4
    solution(Array(Point2D(0, 1), Point2D(0, -1), Point2D(0, -2))) shouldBe 2
    solution(Array(Point2D(1, 0), Point2D(-1, 0), Point2D(-2, 0))) shouldBe 2
  }

  case class Point2D(x: Int, y: Int)

  def solution(a: Array[Point2D]): Int = {
    // write your code in Scala 2.12
    a.map(p => (gradient(p), quadrant(p))).distinct.length
  }

  def gradient(p: Point2D): Double = p.y / p.x.toDouble
  def quadrant(p: Point2D): Int = (if (p.x >= 0) 2 else 0) + (if (p.y >= 0) 1 else 0)

  test ("Force Vector direction"){
    {
      val (x, y) = Force.vector(100, Math.toRadians(45))
      x shouldBe 70.71 +- 0.01
      y shouldBe -70.71 +- 0.01
    }

    {
      val (x, y) = Force.vector(100, Math.toRadians(-45))
      x shouldBe -70.71 +- 0.01
      y shouldBe -70.71 +- 0.01
    }

    {
      val (x, y) = Force.vector(100, Math.toRadians(-135))
      x shouldBe -70.71 +- 0.01
      y shouldBe 70.71 +- 0.01
    }

    {
      val (x, y) = Force.vector(100, Math.toRadians(135))
      x shouldBe 70.71 +- 0.01
      y shouldBe 70.71 +- 0.01
    }
  }

  test ("Force Vector negative magnitude"){
    {
      val (x, y) = Force.vector(-100, Math.toRadians(45))
      x shouldBe -70.71 +- 0.01
      y shouldBe 70.71 +- 0.01
    }

    {
      val (x, y) = Force.vector(-100, Math.toRadians(-45))
      x shouldBe 70.71 +- 0.01
      y shouldBe 70.71 +- 0.01
    }

    {
      val (x, y) = Force.vector(-100, Math.toRadians(-135))
      x shouldBe 70.71 +- 0.01
      y shouldBe -70.71 +- 0.01
    }

    {
      val (x, y) = Force.vector(-100, Math.toRadians(135))
      x shouldBe -70.71 +- 0.01
      y shouldBe -70.71 +- 0.01
    }
  }

}
