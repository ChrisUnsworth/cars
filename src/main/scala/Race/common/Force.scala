package Race.common

object Force {
  // 0 angle up the screen thus negative on the y-axis

  def vector(magnitude: Double, rad: Double): (Double, Double) = (Math.sin(rad) * magnitude, Math.cos(rad) * -magnitude)


}
