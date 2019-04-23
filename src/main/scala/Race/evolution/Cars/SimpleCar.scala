package Race.evolution.Cars

import java.awt.Polygon

import Race.common._


object SimpleCar extends CarModel {

  def maxAccelerationDouble = 10
  def maxDeceleration: Double = 7

  def maxSteer: Double = Math.toRadians(15)

  override def drive(acceleration: Double, steer: Double, previousState: CarState): CarState = {
    val state = previousState.asInstanceOf[SimpleCarState]

    val cappedSteer = Math.max(-1d, Math.min(1d, steer))
    val newDirection =
      (cappedSteer * maxSteer) + state.direction match {
        case tooBig if tooBig > Math.PI => -Math.PI + (tooBig - Math.PI)
        case tooSmall if tooSmall < -Math.PI => Math.PI + (tooSmall + Math.PI)
        case fine => fine
      }

    val cappedAcc = Math.max(-1d, Math.min(1d, acceleration))
    val accForce = if (cappedAcc > 0) maxAccelerationDouble * cappedAcc else maxDeceleration * cappedAcc

    val (x, y) = Force.vector(accForce, newDirection)

    val newMomentum = ((state.momentum._1 / 2) + x, (state.momentum._2 / 2) + y)

    val newPosition = previousState.centerOfMass + Point(Math.round(newMomentum._1).toInt, Math.round(newMomentum._2).toInt)

    SimpleCarState(newPosition, newDirection, newMomentum)
  }

  override def initialise(track: Track): CarState = SimpleCarState(track.StartPoint, track.StartDirection, (0, 0))

  override def initialise(gridPosition: Int, track: Track): CarState = SimpleCarState(track.StartGrid(gridPosition), track.StartDirection, (0, 0))

  def rotatedCar(origin: Point, rad: Double): Polygon = {
    val points = bodyOrigins
      .map(_.rotate(rad))
      .map(_ + origin)

    new Polygon(points.map(_.x), points.map(_.y), points.length)
  }

  def lidarOrigins: List[Point] = List(Point(0, -1000), Point(-500, -1000), Point(500, -1000), Point(-1000, -700), Point(1000, -700), Point(-1000, 0), Point(1000, 0))
  def bodyOrigins: Array[Point] = Array(Point(-5, 10), Point(5, 10), Point(5, -10), Point(-5, -10))
}
