package Race.common

import java.awt.Polygon

object CarState {

  def simpleState(p: Point, rad: Double): CarState = SimpleStateImpl(p, rad)

  private def rotatedCar(origin: Point, rad: Double): Polygon = {
    val points = Array(Point(-5, 10), Point(5, 10), Point(5, -10), Point(-5, -10))
      .map(_.rotate(rad))
      .map(_ + origin)

    new Polygon(points.map(_.x), points.map(_.y), points.length)
  }

  def standardLidarOrigins: List[Point] =
    List(
      Point(0, -1000),
      Point(-500, -1000),
      Point(500, -1000),
      Point(-1000, -700),
      Point(1000, -700)
    )

  case class SimpleStateImpl(centerOfMass: Point, direction: Double) extends CarState {
    override def position: Polygon = rotatedCar(centerOfMass, direction)
    override def lidar: List[(Point, Point)] = {
      standardLidarOrigins
        .map(_.rotate(direction))
        .map(p => (centerOfMass, p + centerOfMass))
    }

    override def speed: Double = 0
  }
}


trait CarState {
  def centerOfMass: Point
  def position: Polygon
  def lidar: List[(Point, Point)]
  def speed: Double
  def direction: Double
}