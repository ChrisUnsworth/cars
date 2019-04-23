package Race.common

trait CarRoute {
  def stateAt(i: Int): CarState
  def stateCount: Int
}
