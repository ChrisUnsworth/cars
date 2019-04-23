package Race.common

import java.awt

object Point {
  implicit def awt2Point(p: awt.Point): Point = apply(p)
  implicit def point2awt(p: Point): awt.Point = new awt.Point(p.x, p.y)

  def apply(p: awt.Point): Point = PointImp(p.x, p.y)
  def apply(x: Int, y: Int): Point = PointImp(x, y)

  def midPoint(p: (Point, Point)): Point = midPoint(p._1, p._2)
  def midPoint(p1: Point, p2: Point): Point = PointImp((p1.x + p2.x) / 2, (p1.y + p2.y) / 2)

  def distance(p1: Point, p2: Point): Double =
    Math.sqrt(Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(Math.abs(p1.y - p2.y), 2))

  def inSquare(p: Point, p1: Point, p2: Point): Boolean = {
    if ((p.x > p1.x) && (p.x > p2.x)) return false
    if ((p.x < p1.x) && (p.x < p2.x)) return false
    if ((p.y > p1.y) && (p.y > p2.y)) return false
    if ((p.y < p1.y) && (p.y < p2.y)) return false
    true
  }

  case class PointImp(x: Int, y: Int) extends Point {
    override def +(other: Point): Point = PointImp(x + other.x, y + other.y)
    override def -(other: Point): Point = PointImp(x - other.x, y - other.y)
    override def rotate(origin: Point, rad: Double): Point = (this - origin).rotate(rad) + this
    override def rotate(rad: Double): Point = {
      PointImp(Math.round((x * Math.cos(rad)) - (y * Math.sin(rad))).toInt, Math.round((y * Math.cos(rad)) + (x * Math.sin(rad))).toInt)
    }

    // from the the x axis
    override def angleFromOrigin(): Double = {
      if (x == 0) return if (y > 0) Math.PI * 0.5 else Math.PI * -0.5

      val angle = Math.atan(y/x)

      if (x > 0) return angle

      if (y < 0) -Math.PI + angle else Math.PI + angle
    }

    override def distanceFromOrigin(): Double = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))
  }
}

trait Point {
  def x: Int
  def y: Int

  def +(other: Point): Point
  def -(other: Point): Point
  def rotate(rad: Double): Point
  def rotate(origin: Point, rad: Double): Point

  def angleFromOrigin(): Double
  def distanceFromOrigin(): Double
}