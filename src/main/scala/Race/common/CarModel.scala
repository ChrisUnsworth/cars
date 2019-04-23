package Race.common

trait CarModel {
  def drive(acceleration: Double, steer: Double, state: CarState): CarState
  def initialise(track: Track): CarState
  def initialise(gridPosition: Int, track: Track): CarState
}
