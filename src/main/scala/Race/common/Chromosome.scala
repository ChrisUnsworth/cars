package Race.common

trait Chromosome {
  def root: Int
  def size: Int
  def fitness: Double
  def genes: Iterator[Double]
}
