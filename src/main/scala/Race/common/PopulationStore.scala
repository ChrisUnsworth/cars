package Race.common

trait PopulationStore {
  def store(population: Population): Int
  def store(fileName: String, data: String): Unit
  def store(fileName: String, data: Array[Byte]): Unit
  def store(gen: Int, fileName: String, data: String): Unit
  def store(gen: Int, fileName: String, data: Array[Byte]): Unit
  def generationCount: Int
  def generation(i: Int): Population
}
