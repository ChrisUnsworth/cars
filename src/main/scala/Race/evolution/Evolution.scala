package Race.evolution

import Race.common.{Chromosome, GenePacker, Population}
import Race.evolution.Evolution.PopulationImpl
import Race.evolution.Mutators.MutatorFactory

import scala.util.Random

object Evolution {

  case class PopulationImpl(chromosomes: IndexedSeq[Chromosome]) extends Population {
    override def size: Int = chromosomes.size
    override def fittest: Chromosome = chromosomes.maxBy(_.fitness)
  }
}


class Evolution(packer: GenePacker, size: Int, fitnessMap: (Population, Option[IndexedSeq[Double]]) => IndexedSeq[Double] = (p, _) => p.chromosomes.map(_.fitness)) {

  val cull: Int = Math.round(size.toFloat * 0.6f)

  val maxChildren: Int = Math.round(size.toFloat * 0.5f)

  val rand = new Random()

  var currentPopulation = PopulationImpl((1 to size).map(_ => packer.random(rand)))
  var fitness = fitnessMap(currentPopulation, None)

  def topPopulation(n: Int): Population = PopulationImpl(topChromosomes(n))

  def topChromosomes(n: Int): IndexedSeq[Chromosome] = {
    currentPopulation
      .chromosomes
      .zipWithIndex
      .sortBy { case (_, i) => -fitness(i)}
      .map { case (c, _) => c}
      .take(n)
  }

  def evolve(): Unit = {
    val survivors = currentPopulation
      .chromosomes
      .zipWithIndex
      .sortBy { case (_, i) => fitness(i)}
      .map { case (c, _) => c}
      .drop(cull)

    val groups = survivors
      .groupBy(_.root)
      .filter(_._2.tail.nonEmpty)
      .values
      .toSeq
      .sortBy(s => -s.map(_.fitness).sum)

    val children = groups.flatMap(breed).take(Math.round(size.toFloat * 0.3f))

    val mutantCount = size - survivors.size - children.size

    val mutants = (1 to mutantCount)
        .map { i =>
          val s = survivors(i % survivors.size)
          val mutator = MutatorFactory.unary(rand, rand.nextDouble() * 0.5, rand.nextDouble() * 0.5)
          mutator.mutate(s, packer)
        }

    currentPopulation = PopulationImpl(survivors ++ children ++ mutants)
    fitness = fitnessMap(currentPopulation, Some(fitness))
  }

  private def breed(group: IndexedSeq[Chromosome]): IndexedSeq[Chromosome] = {
    val binaryCut = MutatorFactory.binaryCut(rand, 0.5)

    group
      .grouped(2)
      .filter(_.size == 2)
      .map { l =>
        val (c1, c2) = (l.head, l(1))
        binaryCut.mutate(c1, c2, packer)
      }
      .toIndexedSeq

  }
}