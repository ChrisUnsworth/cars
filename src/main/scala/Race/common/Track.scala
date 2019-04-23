package Race.common

import java.awt.Polygon

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

    val x = points.tails
      .filter(_.size > 1)
      .map { l => new Polygon(
        Array(l.head._1.x, l.tail.head._1.x, l.tail.head._2.x, l.head._2.x),
        Array(l.head._1.y, l.tail.head._1.y, l.tail.head._2.y, l.head._2.y),4)}
    val lastSection = new Polygon(
      Array(points.head._1.x, points.last._1.x, points.last._2.x, points.head._2.x),
      Array(points.head._1.y, points.last._1.y, points.last._2.y, points.head._2.y),4)

    val sections: List[Polygon] = x.toList ++ List(lastSection)

    TrackImpl(
      points,
      Point(0, -5).rotate(startAngle) + startPoint,
      startAngle,
      startFinishLine,
      direction,
      last._1 + last._2.length,
      if (clockwise) b1 else b2,
      if (clockwise) b2 else b1,
      sections,
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
                        points: Iterable[(Point, Point)],
                        StartPoint: Point,
                        StartDirection: Double,
                        StartFinishLine: LineSegment,
                        StartLapSign: Int,
                        lapLength: Double,
                        OuterBoundary: Polygon,
                        InnerBoundary: Polygon,
                        Sections: List[Polygon],
                        boundarySegments: List[LineSegment],
                        centerLine: List[(Double, LineSegment)]
                      ) extends Track {

    override def inBounds(car: Polygon): Boolean = Sections.exists(_.intersects(car.getBounds2D))

    override def lidar(p1: Point, p2: Point): Point = {
      val line = LineSlopeIntercept(p1, p2)
      val hits = boundarySegments
        .flatMap(_.intersect(line))
        .filter(p => Point.inSquare(p2, p1, p) || Point.inSquare(p, p1, p2))

      if (hits.isEmpty) return p2
      hits.minBy(p => Point.distance(p1, p))
    }

    override def StartGrid: List[Point] = {
      List(Point(-15, 0), Point(0,0), Point(15, 0), Point(-15, -40), Point(0,-40), Point(15, -40))
        .map(_ + StartPoint)
        .map(_.rotate(StartPoint, StartDirection))
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
      if (atStartOfLap(p2)) 1
      else if (atStartOfLap(p1)) -1
      else 0
      //val d2 = startLineSideCheck(p2, StartFinishLine)
      //if (d2 == 0) {
      //  val d1 = startLineSideCheck(p1, StartFinishLine)
      //  if (d1 == 0) 0
      //  else
      //}
      //else (d2 / Math.abs(d2)) * StartLapSign
    }

    override def lineOfSight(p1: Point, p2: Point): Boolean = {
      val s1 = getSection(p1)
      val s2 = getSection(p2)
      if (s1.isDefined && s2.isDefined) lineOfSight(p1, s1.get, p2, s2.get)
      else false
    }

    private def  lineOfSight(p1: Point, s1: Int, p2: Point, s2: Int): Boolean = {
      (s1, s2) match {
        case (x, y) if x == y => true
        case (x, y) if Math.abs(x - y) == 1 => true
        case (x, y) if Math.max(x, y) == Sections.indices.last && Math.min(x, y) == 0 => true
        case _ =>
          val pm = Point.midPoint(p1, p2)
          val pms = getSection(pm)
          if (pms.isDefined) lineOfSight(p1, s1, pm, pms.get) && lineOfSight(pm, pms.get, p2, s2)
          else false
      }
    }

    private def getSection(p: Point): Option[Int] = {
      Sections.indexWhere(_.contains(p.x, p.y)) match {
        case x if x > -1 => Some(x)
        case _ => None
      }
    }

    private def atStartOfLap(p: Point): Boolean = {
      startLineSideCheck(p, StartFinishLine) match {
        case 0 => false
        case d => (d / Math.abs(d)) == StartLapSign
      }
    }
  }
}

trait Track {
  def points: Iterable[(Point, Point)]
  def StartPoint: Point
  def StartGrid: List[Point]
  def StartDirection: Double
  def OuterBoundary: Polygon
  def InnerBoundary: Polygon
  def Sections: List[Polygon]
  def inBounds(car: Polygon): Boolean
  def lidar(p1: Point, p2: Point): Point
  def distance(p: Point): Option[(Double, Point)]
  def lapLength: Double
  def lapChange(p1: Point, p2: Point): Int
  def boundarySegments: List[LineSegment]
  def centerLine: List[(Double, LineSegment)]
  def lineOfSight(p1: Point, p2: Point): Boolean
}