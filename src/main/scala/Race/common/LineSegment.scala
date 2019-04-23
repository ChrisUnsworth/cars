package Race.common

object LineSegment {

  def apply(p1: Point, p2: Point): LineSegment = LineSegmentImpl(p1, p2)


  def intersect(s: LineSegment, l2: LineSlopeIntercept): Option[Point] = {
    val l1 = s match {
      case local: LineSegmentImpl => local.lineSlope
      case _ => LineSlopeIntercept.tryMake(s.p1, s.p2)
    }

    if (l1.isEmpty) return None

    val intersect: Option[Point] = l1.get.intersect(l2)

    intersect match {
      case Some(p) if s.inSquare(p) => intersect
      case _ => None
    }
  }

  def intersect(s1: LineSegment, s2: LineSegment): Option[Point] = {
    val l1 = s1 match {
      case local: LineSegmentImpl => local.lineSlope
      case _ => LineSlopeIntercept.tryMake(s1.p1, s1.p2)
    }

    val l2 = s2 match {
      case local: LineSegmentImpl => local.lineSlope
      case _ => LineSlopeIntercept.tryMake(s2.p1, s2.p2)
    }

    if (l1.isEmpty || l2.isEmpty) return None

    val intersect: Option[Point] = l1.get.intersect(l2.get)

    intersect match {
      case Some(p) if s1.inSquare(p) && s2.inSquare(p) => intersect
      case _ => None
    }
  }

  def closestPoint(line: LineSegment, point: Point): Option[Point] = {
    if (line.lineStandard.isEmpty) return None
    val p = LineStandardForm.closestPoint(line.lineStandard.get, point)
    Some(p).filter(line.inSquare)
  }

  case class LineSegmentImpl(p1: Point, p2: Point) extends LineSegment {
    lazy val lineSlope: Option[LineSlopeIntercept] = LineSlopeIntercept.tryMake(p1, p2)
    lazy val lineStandard: Option[LineStandardForm] = Some(LineStandardForm(p1, p2))

    override def length: Double = Point.distance(p1, p2)
    override def intersect(other: LineSlopeIntercept): Option[Point] = LineSegment.intersect(this, other)
    override def intersect(other: LineSegment): Option[Point] = LineSegment.intersect(this, other)
    override def inSquare(p: Point): Boolean = Point.inSquare(p, p1, p2)
    override def closestPoint(point: Point): Option[Point] = LineSegment.closestPoint(this, point)
    override def angle: Double = {
      if (lineSlope.isEmpty) return 0
      val angle = lineSlope.get.angle
      if (p2.x > p1.x) angle
      else if (angle > 0) (2 * Math.PI) - angle
      else (-2 * Math.PI) - angle
    }
  }
}

trait LineSegment {
  def p1: Point
  def p2: Point

  def length: Double
  def lineSlope: Option[LineSlopeIntercept]
  def lineStandard: Option[LineStandardForm]
  def intersect(other: LineSlopeIntercept): Option[Point]
  def intersect(other: LineSegment): Option[Point]
  def inSquare(p: Point): Boolean
  def closestPoint(point: Point): Option[Point]
  def angle: Double // from x-axis
}