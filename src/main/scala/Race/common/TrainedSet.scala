package Race.common

object TrainedSet {

  def apply(track: Track, data: List[List[CarRoute]]): TrainedSet = TrainedSetImpl(track, data)

  case class TrainedSetImpl(track: Track, data: List[List[CarRoute]]) extends TrainedSet {
    override def generations: Int = data.size
    override def generation(num: Int): List[CarRoute] = data(num - 1)
  }
}

trait TrainedSet {
  def generations: Int
  def track: Track
  def generation(num: Int): List[CarRoute]
}
