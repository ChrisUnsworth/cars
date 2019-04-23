package Race.common

trait Population {
  def size: Int
  def fittest: Chromosome
  def chromosomes: IndexedSeq[Chromosome]
}
