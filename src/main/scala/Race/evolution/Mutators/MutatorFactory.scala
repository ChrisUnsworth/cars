package Race.evolution.Mutators

import Race.common.{Chromosome, GenePacker}

import scala.util.Random

object MutatorFactory {

  def unary(rand: Random, changeRate: Double, magnitude: Double): UnaryMutator = UnaryMutatorImpl(rand, changeRate, magnitude)
  def binary(rand: Random, fitBias: Double, changeRate: Double, magnitude: Double): BinaryMutator = BinaryMutatorImpl(rand, fitBias, changeRate, magnitude)
  def binaryCut(rand: Random, fitBias: Double): BinaryMutator = BinaryCutMutator(rand, fitBias)
  def binaryMerge(rand: Random, magnitude: Double): BinaryMutator = BinaryMergeMutatorImpl(rand, magnitude)

  case class UnaryMutatorImpl(rand: Random, changeRate: Double, magnitude: Double) extends UnaryMutator {
    override def mutate(c: Chromosome, p: GenePacker): Chromosome = {
      val newGenes = c.genes
        .map { g =>
          rand.nextDouble() match {
            case p if p < changeRate => g + ((rand.nextDouble() * 2 - 1) * magnitude)
            case _ => g
          }
        }
      p.pack(c.root, newGenes)
    }
  }

  case class BinaryCutMutator(rand: Random, fitBias: Double) extends BinaryMutator {
    override def mutate(c1: Chromosome, c2: Chromosome, p: GenePacker): Chromosome = {
      val seedGenes = if (c1.fitness > c2.fitness) c1.genes.zip(c2.genes) else c2.genes.zip(c1.genes)
      val newGenes = seedGenes.map { case (g1, g2) => if (rand.nextDouble() < fitBias) g1 else g2 }

      p.pack(c1.root, newGenes)
    }
  }

  case class BinaryMutatorImpl(rand: Random, fitBias: Double, changeRate: Double, magnitude: Double) extends BinaryMutator {
    override def mutate(c1: Chromosome, c2: Chromosome, p: GenePacker): Chromosome = {
      val seedGenes = if (c1.fitness > c2.fitness) c1.genes.zip(c2.genes) else c2.genes.zip(c1.genes)
      val newGenes = seedGenes
        .map { case (g1, g2) =>
          val g = if (rand.nextDouble() < fitBias) g1 else g2
          rand.nextDouble() match {
            case p if p < changeRate => g + ((rand.nextDouble() * 2 - 1) * magnitude)
            case _ => g
          }
        }

      p.pack(c1.root, newGenes)
    }
  }

  case class BinaryMergeMutatorImpl(rand: Random, magnitude: Double) extends BinaryMutator {
    override def mutate(c1: Chromosome, c2: Chromosome, p: GenePacker): Chromosome = {
      val seedGenes = if (c1.fitness > c2.fitness) c1.genes.zip(c2.genes) else c2.genes.zip(c1.genes)
      val newGenes = seedGenes
        .map { case (g1, g2) =>
          if (g1 == g2) g1
          else {
            val range = Math.abs(g1 - g2) * magnitude
            val mean = (g1 + g2) / 2
            mean + ((rand.nextDouble() * 2 - 1) * range)
          }
        }

      p.pack(c1.root, newGenes)
    }
  }

}
