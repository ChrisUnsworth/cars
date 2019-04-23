package Race.common

trait Network {
  def evaluate(input: Array[Double]): Array[Double]
  def dimensions: List[Int]
  def weights: Iterator[Double]
}
