package Race.ui.Models

import java.awt.Polygon

import Race.common.{LineSegment, LineSlopeIntercept, Point}

object Track {

  def apply(track: Race.common.Track): Track = apply(track.points)

  def apply(points: Iterable[(Point, Point)]): Track = {
    val boundarySegments = points.tails.flatMap(x => {
      x.size match {
        case l if l > 1 => List(LineSegment(x.head._1, x.tail.head._1), LineSegment(x.head._2, x.tail.head._2))
        case l if l == 1 => List(LineSegment(x.head._1, points.head._1), LineSegment(x.head._2, points.head._2))
        case _ => List()
      }
    })

    val startPoint = Point.midPoint(points.head)
    val centerSegments = points
      .map(Point.midPoint)
      .tails
      .flatMap { x =>
        x.size match {
          case l if l > 1 => Some(LineSegment(x.head, x.tail.head))
          case l if l == 1 => Some(LineSegment(x.head, startPoint))
          case _ => None
        }
      }.toList
      .scanLeft(None: Option[(Double, LineSegment)]){ case (p, l2) =>
        p match {
          case None => Some((0.0, l2))
          case Some((d, l1)) => Some(d + l1.length, l2)
        }}
      .flatten

    val last = centerSegments.last

    val b1 = polygonFromPoints(points.map(_._1))
    val b2 = polygonFromPoints(points.map(_._2))
    val clockwise = b1.contains(points.head._2)

    val startFinishLine =
      if (clockwise) LineSegment(points.head._1, points.head._2)
      else LineSegment(points.head._2, points.head._1)

    val firstCorner = Point.midPoint(points.tail.head)
    val d = startLineSideCheck(firstCorner, startFinishLine)
    val direction = d / Math.abs(d)
    val startAngle = startFinishLine.angle * direction

    TrackImpl(
      Point(0, -5).rotate(startAngle) + startPoint,
      startAngle,
      startFinishLine,
      direction,
      last._1 + last._2.length,
      if (clockwise) b1 else b2,
      if (clockwise) b2 else b1,
      boundarySegments.toList,
      centerSegments
    )
  }


  def startLineSideCheck(p: Point, sfl: LineSegment): Int = {
    val a = sfl.p1
    val b = sfl.p2
    (p.x - a.x) * (b.y - a.y) - (p.y - a.y) * (b.x - a.x)
  }


  def polygonFromPoints(points: Iterable[Point]): Polygon = {
    new Polygon(points.map(_.x).toArray, points.map(_.y).toArray, points.size)
  }

  case class TrackImpl(
                        StartPoint: Point,
                        StartDirection: Double,
                        StartFinishLine: LineSegment,
                        StartLapSign: Int,
                        lapLength: Double,
                        OuterBoundary: Polygon,
                        InnerBoundary: Polygon,
                        boundarySegments: List[LineSegment],
                        centerLine: List[(Double, LineSegment)]
                      ) extends Track {

    override def inBounds(car: Polygon): Boolean = OuterBoundary.contains(car.getBounds2D) && (!InnerBoundary.contains(car.getBounds2D))

    override def lidar(p1: Point, p2: Point): Point = {
      val line = LineSlopeIntercept(p1, p2)
      val hits = boundarySegments
        .flatMap(_.intersect(line))
        .filter(p => Point.inSquare(p2, p1, p) || Point.inSquare(p, p1, p2))

      if (hits.isEmpty) return p2
      hits.minBy(p => Point.distance(p1, p))
    }

    override def distance(p: Point): Option[(Double, Point)] = {
      val c = centerLine
        .map { case (d, l) => (d, l.p1, l.closestPoint(p)) }
        .filter(_._3.isDefined)
        .map { case (d, p1, p2) => (d, p1, p2.get) }

      if (c.isEmpty) return None

      val (distance, lastSegmentEnd, closestPoint) = c.minBy { case (_, _, p2) => Point.distance(p, p2) }

      Some((distance + Point.distance(lastSegmentEnd, closestPoint), closestPoint))
    }

    override def lapChange(p1: Point, p2: Point): Int = {
      if (StartFinishLine.intersect(LineSegment(p1, p2)).isEmpty) return 0
      val d = startLineSideCheck(p2, StartFinishLine)
      if (d == 0) 0
      else (d / Math.abs(d)) * StartLapSign
    }
  }
}

trait Track {
  def StartPoint: Point
  def StartDirection: Double
  def OuterBoundary: Polygon
  def InnerBoundary: Polygon
  def inBounds(car: Polygon): Boolean
  def lidar(p1: Point, p2: Point): Point
  def distance(p: Point): Option[(Double, Point)]
  def lapLength: Double
  def lapChange(p1: Point, p2: Point): Int
}