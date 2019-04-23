package Race.evolution

import java.nio.charset.StandardCharsets
import java.nio.{ByteBuffer, DoubleBuffer}
import java.nio.file.{Path, Paths}

import Race.common.{Chromosome, GenePacker, Population, PopulationStore}
import Race.io.FileSystem

import scala.util.matching.Regex

class PopulationFileStore(folder: String, packer: GenePacker, fs: FileSystem) extends PopulationStore {

  var currentGeneration: Int = 0

  override def store(population: Population): Int = {
    population.chromosomes
        .zipWithIndex
        .foreach { case (c, i) =>
          val path = Paths.get(getFolder(currentGeneration).toString, fileName(i))
          val bb = ByteBuffer.allocate(8 * c.size)
          for (g <- c.genes) bb.putDouble(g)
          fs.writeBytes(path, bb.array())
        }

    val gen = currentGeneration
    currentGeneration = gen + 1
    gen
  }



  override def generationCount: Int = currentGeneration

  override def generation(i: Int): Population = {
    val files = fs.getFiles(getFolder(i))

    val numbers = files
      .flatMap { p =>
        p.getFileName.toString match {
          case fileNameRegex(idx) => Some(idx.toInt)
          case _ => None
        }
      }

    val chromosomes = numbers
      .toIndexedSeq
      .sorted
      .map(c => readChromasome(c, i))

    PopulationDto(chromosomes)
  }

  private def getFolder(generation: Int): Path = {
    Paths.get(folder, s"G_$generation")
  }

  private def fileName(c: Int): String = s"C_$c.chromosome"
  val fileNameRegex: Regex = s"""C_(\\d+).chromosome""".r

  private def readChromasome(idx: Int, generation: Int): Chromosome = {
    val path = Paths.get(getFolder(generation).toString, fileName(idx))
    val db = ByteBuffer.wrap(fs.readBytes(path)).asDoubleBuffer()
    packer.pack(-1, DoubleIterator(db))
  }

  case class PopulationDto(chromosomes: IndexedSeq[Chromosome]) extends Population {
    override def size: Int = chromosomes.size
    override def fittest: Chromosome = chromosomes.maxBy(_.fitness)
  }

  case class DoubleIterator(db: DoubleBuffer) extends Iterator[Double] {
    override def hasNext: Boolean = db.remaining() > 0
    override def next(): Double = db.get()
  }

  override def store(fileName: String, data: String): Unit = store(fileName, data.getBytes(StandardCharsets.UTF_8))
  override def store(fileName: String, data: Array[Byte]): Unit = fs.writeBytes(Paths.get(folder, fileName), data)

  override def store(gen: Int, fileName: String, data: String): Unit = store(gen, fileName, data.getBytes(StandardCharsets.UTF_8))
  override def store(gen: Int, fileName: String, data: Array[Byte]): Unit = fs.writeBytes(Paths.get(getFolder(gen).toString, fileName), data)
}
