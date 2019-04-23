package Race.Training.SixCarRace

import Race.common._
import Race.evolution.Evolution

import scala.util.Random

object Run {
  def start(car: CarModel, track: Track, packer: GenePacker, timeLimit: Int, populationSize: Int, store: PopulationStore, generations: Int): TrainedSet = {

    val evolution = new Evolution(packer, populationSize, fitnessMap(track, car, timeLimit))
    store.store(evolution.topPopulation(6))

    for (_ <- 1 to generations) {
      evolution.evolve()
      val g = store.store(evolution.topPopulation(6))
      val maxDistance = evolution.fitness.max
      store.store(g, "MaxDistance.txt", s"max fitness: $maxDistance")
    }

    PopulationStoreWrapper(store.generationCount, track, store, car, timeLimit)
  }

  def fitnessMap(track: Track, car: CarModel, timeLimit: Int)(population: Population, previous: Option[IndexedSeq[Double]]): IndexedSeq[Double] = {

    val groups = Random.shuffle(population.chromosomes.indices.toList).grouped(6)

    val result = Array.fill(population.size)(0d)

    for (group <- groups.filter(_.size == 6)) {
      val drivers = group
        .map(i => population.chromosomes(i))
        .map { case nc: NetworkChromosome => nc.network }
      val standings = SixCarRace.raceSeason(drivers, track, car, timeLimit)

      group.indices.foreach(i => result(group(i)) = standings(i))
    }

    result
  }

  case class CarRouteImpl(states: IndexedSeq[CarState]) extends CarRoute {
    override def stateAt(i: Int): CarState = if (i < states.size) states(i) else states.last

    override def stateCount: Int = states.size
  }

  case class PopulationStoreWrapper(generations: Int, track: Track, store: PopulationStore, car: CarModel, timeLimit: Int) extends TrainedSet {

    override def generation(num: Int): List[CarRoute] = {
      val drivers = store
        .generation(num-1)
        .chromosomes
        .map { case nc: NetworkChromosome => nc.network }
        .toList

      val results: IndexedSeq[IndexedSeq[Option[CarState]]] = SixCarRace.runRace(drivers, track, car, timeLimit)

      val runs = drivers.indices.map(i => results.map(_(i)))

      runs.map(r => CarRouteImpl(r.flatten)).toList
    }
  }

}
