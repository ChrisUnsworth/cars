package Race.evolution.Mutators

import Race.common.{Chromosome, GenePacker}

trait BinaryMutator {
  def mutate(c1: Chromosome, c2: Chromosome, p: GenePacker): Chromosome
}
