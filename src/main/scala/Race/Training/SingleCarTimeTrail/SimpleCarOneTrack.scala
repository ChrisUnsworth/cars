package Race.Training.SingleCarTimeTrail

import Race.common._
import Race.evolution.{Evolution, TestRun}

class SimpleCarOneTrack {

  def run(car: CarModel, track: Track, packer: GenePacker, timeLimit: Int, populationSize: Int, store: PopulationStore, generations: Int): TrainedSet = {

    val evolution = new Evolution(packer, populationSize)
    store.store(evolution.currentPopulation)

    for (i <- 1 to generations) {
      evolution.evolve()
      if (i % 10 == 1) {
        val g = store.store(evolution.currentPopulation)
        val maxDistance = evolution.currentPopulation.fittest.fitness
        store.store(g, "MaxDistance.txt", s"max distance: $maxDistance")
      }

    }

    val fittestCar = evolution.currentPopulation.fittest.asInstanceOf[NetworkChromosome]
    val result = TestRun.evaluateRun(fittestCar.run, track)

    PopulationStoreWrapper(store.generationCount, track, store)
  }

  case class CarRouteImpl(states: IndexedSeq[CarState]) extends CarRoute {
    override def stateAt(i: Int): CarState = if (i < states.size) states(i) else states.last
    override def stateCount: Int = states.size
  }

  case class PopulationStoreWrapper(generations: Int, track: Track, store: PopulationStore) extends TrainedSet {

    override def generation(num: Int): List[CarRoute] = {
      store
        .generation(num)
        .chromosomes
        .map(_.asInstanceOf[NetworkChromosome])
        .map(_.run)
        .map(r => CarRouteImpl(r))
        .toList
    }
  }
}
