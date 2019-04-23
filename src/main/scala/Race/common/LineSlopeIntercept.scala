package Race.common

object LineSlopeIntercept {
  def apply(p1: Point, p2: Point): LineSlopeIntercept = {
    tryMake(p1, p2).getOrElse(throw new RuntimeException("Unable to make line from 2 identical points"))
  }

  def tryMake(p1: Point, p2: Point): Option[LineSlopeIntercept] = {
    if (p1 == p2) None
    if (p1.x == p2.x) return Some(Vertical(p1.x))
    val m = (p2.y - p1.y).toDouble / (p2.x - p1.x).toDouble
    val b = p1.y - (m * p1.x)
    Some(LineSlopeInterceptImpl(m, b))
  }

  def intersect(l1: LineSlopeIntercept, l2: LineSlopeIntercept): Option[Point] = {
    if (l1.b == l2.b) return None

    if (l1.isVertical) {
      val x = l1.asInstanceOf[Vertical].x
      return Some(Point(Math.round(x).toInt, Math.round(l2.m * x + l2.b).toInt))
    }

    if (l2.isVertical) {
      val x = l2.asInstanceOf[Vertical].x
      return Some(Point(Math.round(x).toInt, Math.round(l1.m * x + l1.b).toInt))
    }

    val x = (l2.b - l1.b) / (l1.m - l2.m)
    val y = l1.m * x + l1.b

    Some(Point(Math.round(x).toInt, Math.round(y).toInt))
  }

  case class LineSlopeInterceptImpl(m: Double, b: Double) extends LineSlopeIntercept {
    override def intersect(other: LineSlopeIntercept): Option[Point] = LineSlopeIntercept.intersect(this, other)
    override def isVertical: Boolean = false
    override def angle: Double = Math.atan(m)
  }

  case class Vertical(x: Double) extends LineSlopeIntercept {
    override def m: Double = Double.NaN
    override def b: Double = Double.NaN
    override def isVertical: Boolean = true
    override def angle: Double = Math.toRadians(90)
    override def intersect(other: LineSlopeIntercept): Option[Point] = LineSlopeIntercept.intersect(this, other)
  }
}

trait LineSlopeIntercept {
  def m: Double
  def b: Double
  def isVertical: Boolean
  def angle: Double // from x-axis

  def intersect(other: LineSlopeIntercept): Option[Point]
}