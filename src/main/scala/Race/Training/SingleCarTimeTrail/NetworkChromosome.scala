package Race.Training.SingleCarTimeTrail

import Race.common.{CarState, Chromosome, Network}

case class NetworkChromosome(root: Int, network: Network, run: IndexedSeq[CarState], fitness: Double) extends Chromosome {

  override def size: Int = network
    .dimensions
    .tails
    .filter(_.size > 1)
    .map(l => (l.head + 1) * l(1))
    .sum

  override def genes: Iterator[Double] = network.weights
}
