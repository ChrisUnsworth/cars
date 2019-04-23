package Race.evolution

import Race.common._
import Race.NeuralNetwork.Network.NetworkImpl

object TestRun {
  def run(car: CarModel, track: Track, driver: NetworkImpl, timeLimit: Int): TestRunResult = {
    val states = (1 to timeLimit).scanLeft(Some(car.initialise(track)): Option[CarState])
    { case (previous, _) =>
        previous match {
          case Some(s: CarState) if track.inBounds(s.position) =>
            val lidarLengths = s.lidar.map { case (p1, p2) => Point.distance(p1, track.lidar(p1, p2))}

            val output = driver.evaluate(lidarLengths.toArray ++ Array(s.speed))
            Some(car.drive(output.head, output(1), s))
          case _ => None
        }
    }.flatten

    val (time, distance) = evaluateRun(states, track)

    TestRunResult(states, time, distance)
  }

  def evaluateRun(states: IndexedSeq[CarState], track: Track): (Int, Double) = {
    val lapCount = states
      .tails
      .filter(_.size > 1)
      .map(l => track.lapChange(l.head.centerOfMass, l.tail.head.centerOfMass))

    if (lapCount.exists(_ < 0)) return (states.size, 0)

    (states.size, lapCount.sum * track.lapLength + track.distance(states.last.centerOfMass).map(_._1).getOrElse(0.0))
  }

  case class TestRunResult(states: IndexedSeq[CarState], time: Int, distance: Double) extends CarRoute {
    override def stateAt(i: Int): CarState = {
      val idx = i % 550
      if (states.size > idx) states(idx) else states.last
    }

    override def stateCount: Int = states.size
  }
}
