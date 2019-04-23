package Race.Training.SixCarRace

import Race.NeuralNetwork.Network
import Race.common._

import scala.util.Random

class SixCarRacePacker(car: CarModel, track: Track, dimensions: List[Int], timeLimit: Int) extends GenePacker{

  private var _nextRootId = 1

  def nextRootId: Int = {
    val id = _nextRootId
    _nextRootId = _nextRootId + 1
    id
  }

  override def pack(root: Int, genes: Iterator[Double]): Chromosome = buildChromosome(root, Network(dimensions, genes))

  override def random(rand: Random): Chromosome = buildChromosome(nextRootId, Network.random(dimensions: _*))

  private def buildChromosome(root: Int, network: Network): Chromosome = NetworkChromosome(root, network)
}
