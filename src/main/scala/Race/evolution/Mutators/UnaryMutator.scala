package Race.evolution.Mutators

import Race.common.{Chromosome, GenePacker}

trait UnaryMutator {
  def mutate(c: Chromosome, p: GenePacker): Chromosome
}
