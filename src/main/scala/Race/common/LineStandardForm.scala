package Race.common

object LineStandardForm {
  def apply(p1: Point, p2: Point): LineStandardForm = {
    if (p1.x == p2.x) return  vertical(p1.x)
    val (x1, y1, x2, y2) = (p1.x, p1.y, p2.x, p2.y)
    val m = (y2 - y1).toDouble / (x2 - x1).toDouble
    val a = -m
    val b = 1
    val c = a * x1 + y1
    LineStandardFormImpl(a, b, c)
  }

  def closestRealPoint(line: LineStandardForm, point: Point): (Double, Double) = {
    line match {
      case v: vertical => return (v.x, point.y)
      case _ =>
    }

    val a = line.a
    val b = line.b
    val c = -line.c
    val x = point.x.toDouble
    val y = point.y.toDouble
    val X = (((b*x - a*y) * b) - (a * c)) / (Math.pow(a, 2) + Math.pow(b, 2))
    val Y = (((-b*x + a*y) * a) - (b * c)) / (Math.pow(a, 2) + Math.pow(b, 2))
    (X, Y)
  }

  def closestPoint(line: LineStandardForm, point: Point): Point = {
    val (x, y) = closestRealPoint(line, point)
    Point(Math.round(x).toInt, Math.round(y).toInt)
  }

  case class LineStandardFormImpl(a: Double, b: Double, c: Double) extends LineStandardForm
  case class vertical(x: Int) extends LineStandardForm {
    override def a: Double = Double.NaN
    override def b: Double = Double.NaN
    override def c: Double = Double.NaN
  }
}

// line in standard form aX + bY = c
trait LineStandardForm {
  def a: Double
  def b: Double
  def c: Double
}