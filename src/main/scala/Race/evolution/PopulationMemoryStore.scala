package Race.evolution

import Race.common.{Population, PopulationStore}

import scala.collection.mutable.ListBuffer

class PopulationMemoryStore extends PopulationStore {

  var generations: ListBuffer[Population] = ListBuffer[Population]()

  override def store(population: Population): Int = {
    generations.append(population)
    generations.size - 1
  }

  override def generationCount: Int = generations.size

  override def generation(i: Int): Population = generations(i)

  override def store(fileName: String, data: String): Unit = {}
  override def store(fileName: String, data: Array[Byte]): Unit = {}
  override def store(gen: Int, fileName: String, data: String): Unit = {}
  override def store(gen: Int, fileName: String, data: Array[Byte]): Unit = {}
}
