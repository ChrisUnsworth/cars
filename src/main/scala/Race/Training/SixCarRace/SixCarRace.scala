package Race.Training.SixCarRace

import Race.common._

object SixCarRace {

  def inputCount(carCount: Int, lidarCount: Int): Int = lidarCount + (carCount * 2) + 2

  def raceSeason(allDrivers: List[Network], track: Track, car: CarModel, timeLimit: Int): IndexedSeq[Double] = {

    permutations(allDrivers)
      .map(d => race(d, track, car, timeLimit))
      .reduce((l1, l2) => l1.zip(l2).map { case (v1, v2) => v1 + v2 })
  }

  def permutations[T](list: List[T]): Iterable[List[T]] = list.indices.map { i => list.drop(i) ++ list.take(i) }

  def race(allDrivers: List[Network], track: Track, car: CarModel, timeLimit: Int): IndexedSeq[Double] = {

    val states = runRace(allDrivers, track, car, timeLimit)

    val di = distances(allDrivers.size, states, track)
    val zi = di.zipWithIndex
    val sb = zi.sortBy(_._1)
    val zp = sb.zip(points(allDrivers.size))
    val mp = zp.map { case ((d, i), p) => (i, d + p) }
    val si = mp.sortBy(_._1)
    val r = si.map(_._2)

    r
  }

  def points(n: Int): IndexedSeq[Int] = {
    (1 to n)
      .map(i => Math.pow(i, 1.6).toFloat)
      .map(_ * 100)
      .map(Math.round)
  }

  def distances(n: Int, race: IndexedSeq[IndexedSeq[Option[CarState]]], track: Track): IndexedSeq[Double] = {
    race
      .tails
      .filter(_.size > 1)
      .foldLeft(Array.fill(race.head.length)(0d)) { case (totals, states) =>
        (0 until n).foreach { i =>
          (states.head(i), states(1)(i)) match {
            case (Some(s1), Some(s2)) => totals(i) = totals(i) + distance(s1, s2, track)
            case _ =>
          }
        }
        totals
      }
  }

  def distance(s1: CarState, s2: CarState, track: Track): Double = {
    val (p1, p2) = (s1.centerOfMass, s2.centerOfMass)
    val carDistance = Point.distance(p1, p2)
    val trackDelta =  track.distance(p2).map(_._1).getOrElse(0.0) - track.distance(p1).map(_._1).getOrElse(0.0)

    if (carDistance < Math.abs(trackDelta)) carDistance else trackDelta
  }

  def runRace(allDrivers: List[Network], track: Track, car: CarModel, timeLimit: Int): IndexedSeq[IndexedSeq[Option[CarState]]] = {
    val initialStates: IndexedSeq[Option[CarState]] = (0 until 6).map(i => Some(car.initialise(i, track)))
    (1 to timeLimit).scanLeft(initialStates)
    { case (previous, _) =>
      previous.indices
        .map { i =>
          previous(i) match {
            case Some(s) =>
              val crashed = hasCrashed(s, previous.take(i) ++ previous.drop(i + 1))
              if (crashed || !track.inBounds(s.position)) None
              else {
                val newState = transition(i, allDrivers(i), car, previous, track)
                if (newState.exists(n => validTransition(s, n, track))) newState else None
              }
            case _ => None
          }
        }
    }
  }

  def hasCrashed(s: CarState, cars: IndexedSeq[Option[CarState]]): Boolean = {
    val rect = s.position.getBounds2D
    cars.flatten.exists(_.position.getBounds2D.intersects(rect))
  }

  def transition(i: Int, driver: Network, car: CarModel, states: IndexedSeq[Option[CarState]], track: Track): Option[CarState] = {
    val s = states(i).getOrElse(return None)
    val inputs = getInputValues(s, states.take(i) ++ states.drop(i + 1), track)
    val output = driver.evaluate(inputs)
    val newState = car.drive(output.head, output(1), s)
    Some(newState)
  }

  def getInputValues(s: CarState, cars: IndexedSeq[Option[CarState]], track: Track): Array[Double] = {
    val lidarLengths = s.lidar
      .map { case (p1, p2) => Point.distance(p1, track.lidar(p1, p2))}
      .map(v => scale(v, 400d))

    val p = s.position
    val currentSection = track
      .Sections
      .tails
      .find(_.head.intersects(p.getBounds2D))

    val nextSection = currentSection match {
      case Some(l) if l.size == 1 => track.Sections.head
      case Some(l) if l.size > 1 => l(1)
      case _ => throw new RuntimeException("Unable to find track section for car")
    }

    val points = Point(nextSection.xpoints.sum / nextSection.npoints, nextSection.ypoints.sum / nextSection.npoints) +: cars.map(_.map(_.centerOfMass).getOrElse(s.centerOfMass)).sortBy(o => Point.distance(s.centerOfMass, o))
    val relativePoints = points.map(p => (p - s.centerOfMass).rotate(-1 * s.direction))

    val flatPoints = relativePoints.flatMap(p => Array(p.x, p.y)).map(_ / 10d)

    lidarLengths.toArray ++ Array(s.speed / 10, s.direction) ++ flatPoints
  }

  def scale(v: Double, max: Double): Double = Math.min(v, max) / max

  def validTransition(p1: CarState, p2: CarState, track: Track): Boolean = {
    track.lineOfSight(p1.centerOfMass, p2.centerOfMass)
  }
}
