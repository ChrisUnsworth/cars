package Race.common

import scala.util.Random

trait GenePacker {
  def pack(root: Int, genes: Iterator[Double]): Chromosome
  def random(rand: Random): Chromosome
}
