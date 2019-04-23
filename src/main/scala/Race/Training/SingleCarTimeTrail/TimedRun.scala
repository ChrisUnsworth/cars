package Race.Training.SingleCarTimeTrail

import java.awt.{Polygon, Rectangle}

import Race.common._

object TimedRun {
  def testRun(driver: Network, track: Track, car: CarModel, timeLimit: Int): IndexedSeq[CarState] = {
    (1 to timeLimit).scanLeft(Some(car.initialise(track)): Option[CarState])
    { case (previous, _) =>
      previous match {
        case Some(s) if track.inBounds(s.position) =>
          val lidarLengths = s.lidar
            .map { case (p1, p2) => Point.distance(p1, track.lidar(p1, p2))}
            .map(v => scale(v, 400d))

          val p = s.position
          val currentSection = track
            .Sections
            .tails
            .find(_.head.intersects(p.getBounds2D))

          val nextSection = currentSection match {
            case Some(l) if l.tail.nonEmpty => l(1)
            case Some(l) if l.size == 1 => track.Sections.head
            case _ => throw new RuntimeException("Unable to find track section for car")
          }

          val nextCenter = Point(nextSection.xpoints.sum / nextSection.npoints, nextSection.ypoints.sum / nextSection.npoints)
          val relativeNextCenter = (nextCenter - s.centerOfMass).rotate(-1 * s.direction)

          val inputs = lidarLengths.toArray ++ Array(s.speed / 10, relativeNextCenter.x / 10d, relativeNextCenter.y / 10d, s.direction)

          val output = driver.evaluate(inputs)
          val newState = car.drive(output.head, output(1), s)
          if (validTransition(s, newState, track)) Some(newState) else None
        case _ => None
      }
    }.flatten
  }

  def validTransition(p1: CarState, p2: CarState, track: Track): Boolean = {
    val p1Rect = p1.position.getBounds2D
    val p2Rect = p2.position.getBounds2D
    val sIdx1 = track.Sections.indexWhere(_.intersects(p1Rect))
    val sIdx2 = track.Sections.indexWhere(_.intersects(p2Rect))

    (sIdx1, sIdx2) match {
      case (a, b) if a == b => true
      case (a, b) if a+1 == b => true
      case (a, b) if a+1 == track.Sections.size && b == 0 => true
      case (a, b) if a > b => validTransition(p1, p2, ((a+1 until track.Sections.size) ++ (0 until b)).map(i => track.Sections(i)))
      case (a, b) if a+1 < b => validTransition(p1, p2, (a+1 until b).map(i => track.Sections(i)))
    }
  }

  def validTransition(p1: CarState, p2: CarState, sections: IndexedSeq[Polygon]): Boolean = {
    val (p1x, p1y, p2x, p2y) = (p1.centerOfMass.x, p1.centerOfMass.y, p2.centerOfMass.x, p2.centerOfMass.y)
    val rect = new Rectangle(Math.min(p1x, p2x), Math.min(p1y, p2y), Math.abs(p1x - p2x), Math.abs(p1y - p2y)).getBounds2D
    sections.forall(_.intersects(rect))
  }

  def scale(v: Double, max: Double): Double = Math.min(v, max) / max

  def distance(run: IndexedSeq[CarState], track: Track): Double = {
    val trackDistances = run
      .tails
      .filter(_.size > 1)
      .map { l =>
        val (p1, p2) = (l.head.centerOfMass, l(1).centerOfMass)
        val carDistance = Point.distance(p1, p2)
        val trackDelta =  track.distance(p2).map(_._1).getOrElse(0.0) - track.distance(p1).map(_._1).getOrElse(0.0)

        if (carDistance < Math.abs(trackDelta)) carDistance else trackDelta
      }

    trackDistances.sum
  }
}
