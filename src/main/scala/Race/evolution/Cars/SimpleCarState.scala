package Race.evolution.Cars

import java.awt.Polygon
import java.nio.ByteBuffer
import java.text.ParseException

import Race.common.{CarState, Point}
import Race.evolution.Cars.SimpleCar.{lidarOrigins, rotatedCar}

object SimpleCarState {

  private val regex = "x (.+) y (.+) d (.+) mx (.+) my (.+)".r

  def apply(text: String): SimpleCarState = {
    text match {
      case regex(x, y, d, mx, my) => SimpleCarState(Point(x.toInt, y.toInt), d.toDouble, (mx.toDouble, my.toDouble))
      case _ => throw new ParseException("Unable to parse given text as SimpleCarState: " + text, 0)
    }
  }

  def apply(bytes: Array[Byte]): SimpleCarState = apply(ByteBuffer.wrap(bytes))

  def apply(bb: ByteBuffer, offset: Int): SimpleCarState = {
    SimpleCarState(
      Point(bb.getInt(offset + 0), bb.getInt(offset + 4)),
      direction = bb.getDouble(offset + 8),
      momentum = (bb.getDouble(offset + 16), bb.getDouble(offset + 24)))
  }

  def apply(bb: ByteBuffer): SimpleCarState = {
    SimpleCarState(
      Point(bb.getInt(), bb.getInt()),
      direction = bb.getDouble(),
      momentum = (bb.getDouble(), bb.getDouble()))
  }

  def byteSize: Int = 32
}

case class SimpleCarState(centerOfMass: Point, direction: Double, momentum: (Double, Double)) extends CarState {

  override def position: Polygon = rotatedCar(centerOfMass, direction)

  override def lidar: List[(Point, Point)] = {
    lidarOrigins
      .map(_.rotate(direction))
      .map(p => (centerOfMass, p + centerOfMass))
  }

  override def speed: Double = Math.sqrt(Math.pow(momentum._1, 2) + Math.pow(momentum._2, 2))

  def asText(): String = {
    s"x ${centerOfMass.x} y ${centerOfMass.y} d $direction mx ${momentum._1} my ${momentum._2}"
  }

  def asBytes(): Array[Byte] = {
    val bb = ByteBuffer.allocate(SimpleCarState.byteSize)
    bb.putInt(centerOfMass.x)
    bb.putInt(centerOfMass.y)
    bb.putDouble(direction)
    bb.putDouble(momentum._1)
    bb.putDouble(momentum._2)
    bb.array()
  }
}
