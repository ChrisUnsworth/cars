package Race.Training.SingleCarTimeTrail

import Race.NeuralNetwork.Network
import Race.common._

import scala.util.Random

class SimpleCarDriverPacker(car: CarModel, track: Track, dimensions: List[Int], timeLimit: Int) extends GenePacker{

  private var _nextRootId = 1

  def nextRootId: Int = {
    val id = _nextRootId
    _nextRootId = _nextRootId + 1
    id
  }

  override def pack(root: Int, genes: Iterator[Double]): Chromosome = buildChromosome(root, Network(dimensions, genes))

  override def random(rand: Random): Chromosome = buildChromosome(nextRootId, Network.random(dimensions: _*))

  private def buildChromosome(root: Int, network: Network): Chromosome = {
    val run = TimedRun.testRun(network, track, car, timeLimit)
    NetworkChromosome(root, network, run, TimedRun.distance(run, track))
  }


}
