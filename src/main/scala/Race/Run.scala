package Race

import Race.evolution.Cars.{AntiGravCar, SimpleCar}
import Race.Tracks.Dictionary
import Race.Training.SingleCarTimeTrail.{SimpleCarDriverPacker, SimpleCarOneTrack}
import Race.Training.SixCarRace
import Race.Training.ThreeCarRace
import Race.io.LocalFileSystem
import Race.evolution.{PopulationFileStore, PopulationMemoryStore}
import Race.ui.{EditForm, RaceForm}

object Run {
  def main(args: Array[String]): Unit = {
    BigCTrack()
  }

  def edit(): Unit = {
    new EditForm(new LocalFileSystem())
  }

  def trainSimpleCarAndRing(): Unit = {
    val track = Dictionary.SimpleRing
    val car = SimpleCar
    val timeLimit = 200
    val packer = new SimpleCarDriverPacker(car, track, List(SimpleCar.lidarOrigins.size + 4, 10, 10, 2), timeLimit)
    val store = new PopulationMemoryStore()
    val trainedSet = new SimpleCarOneTrack().run(car, track, packer, 200, 50, store, 50)
    new RaceForm(trainedSet)
  }

  def Side8(): Unit = {
    val track = Dictionary.SideEight
    val fs = new LocalFileSystem()
    val car = SimpleCar
    val timeLimit = 500
    val populationSize = 500
    val generations = 100
    val packer = new SimpleCarDriverPacker(car, track, List(SimpleCar.lidarOrigins.size + 4, 10, 10, 2), timeLimit)
    val folder = "SetStoreSide8"
    val store = new PopulationFileStore(folder, packer, fs)
    val trainedSet = new SimpleCarOneTrack().run(car, track, packer, timeLimit, populationSize, store, generations)
    new RaceForm(trainedSet)
  }

  def CTrack(): Unit = {
    val track = Dictionary.CTrack
    val fs = new LocalFileSystem()
    val car = SimpleCar
    val timeLimit = 500
    val populationSize = 1000
    val generations = 100
    val packer = new SimpleCarDriverPacker(car, track, List(SimpleCar.lidarOrigins.size + 4, 10, 10, 2), timeLimit)
    val folder = "SetStoreCTrack"
    val store = new PopulationFileStore(folder, packer, fs)
    val trainedSet = new SimpleCarOneTrack().run(car, track, packer, timeLimit, populationSize, store, generations)
    new RaceForm(trainedSet)
  }

  def BigCTrack(): Unit = {
    val track = Dictionary.BigC
    val fs = new LocalFileSystem()
    val car = SimpleCar
    val timeLimit = 500
    val populationSize = 1000
    val generations = 100
    val packer = new SimpleCarDriverPacker(car, track, List(SimpleCar.lidarOrigins.size + 4, 10, 10, 2), timeLimit)
    val folder = "SetStoreBigC"
    val store = new PopulationFileStore(folder, packer, fs)
    val trainedSet = new SimpleCarOneTrack().run(car, track, packer, timeLimit, populationSize, store, generations)
    new RaceForm(trainedSet)
  }

  def Side8AG(): Unit = {
    val track = Dictionary.SideEight
    val fs = new LocalFileSystem()
    val car = AntiGravCar
    val timeLimit = 500
    val populationSize = 600
    val generations = 200
    val packer = new SimpleCarDriverPacker(car, track, List(SimpleCar.lidarOrigins.size + 4, 12, 12, 2), timeLimit)
    val folder = "SetStore8AG"
    val store = new PopulationFileStore(folder, packer, fs)
    val trainedSet = new SimpleCarOneTrack().run(car, track, packer, timeLimit, populationSize, store, generations)
    new RaceForm(trainedSet)
  }

  def SimpleRingRace(): Unit = {
    val track = Dictionary.SideEight
    val car = SimpleCar
    val timeLimit = 300
    val dimensions = List(SixCarRace.SixCarRace.inputCount(6, SimpleCar.lidarOrigins.size), 20, 15, 10, 2)
    val generations = 300
    val populationSize = 900
    val packer = new SixCarRace.SixCarRacePacker(car, track, dimensions, timeLimit)
    val folder = "SimpleRingRace"
    val fs = new LocalFileSystem()
    val store = new PopulationFileStore(folder, packer, fs)
    val trainedSet = SixCarRace.Run.start(
      car = car,
      track = track,
      packer = packer,
      timeLimit = timeLimit,
      populationSize = populationSize,
      store = store,
      generations = generations)
    new RaceForm(trainedSet)
  }

  def SideEightThreeRace(): Unit = {
    val track = Dictionary.SideEight
    val car = SimpleCar
    val timeLimit = 200
    val dimensions = List(ThreeCarRace.ThreeCarRace.inputCount(SimpleCar.lidarOrigins.size), 15, 15, 2)
    val generations = 500
    val populationSize = 1500
    val packer = new ThreeCarRace.ThreeCarRacePacker(car, track, dimensions, timeLimit)
    val folder = "SideEightThreeRace"
    val fs = new LocalFileSystem()
    val store = new PopulationFileStore(folder, packer, fs)
    val trainedSet = ThreeCarRace.Run.start(
      car = car,
      track = track,
      packer = packer,
      timeLimit = timeLimit,
      populationSize = populationSize,
      store = store,
      generations = generations)
    new RaceForm(trainedSet)
  }
}
