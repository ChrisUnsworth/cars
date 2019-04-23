package Race.io

import java.nio.file.{Path, Paths}

import Race.common.{Network, Track}
import Race.io.Wrappers.{NetworkWrapper, TrackWrapper}

import scala.util.matching.Regex

object FileSystem {
  def local(root: String): FileSystem = new LocalFileSystem(root)
}

trait FileSystem {

  def readTrackFromText(name: String): Track = readFromText(name, TrackWrapper)
  def readTrackFromBytes(name: String): Track = readFromBytes(name, TrackWrapper)
  def writeAsText(name: String, thing: Track): Unit = writeAsText(name, TrackWrapper(thing))
  def writeAsBytes(name: String, thing: Track): Unit = writeAsBytes(name, TrackWrapper(thing))
  def trackList(): Iterable[String] = fileList(TrackWrapper)

  def readNetworkFromText(name: String): Network = readFromText(name, NetworkWrapper)
  def readNetworkFromBytes(name: String): Network = readFromBytes(name, NetworkWrapper)
  def writeAsText(name: String, thing: Network): Unit = writeAsText(name, NetworkWrapper(thing))
  def writeAsBytes(name: String, thing: Network): Unit = writeAsBytes(name, NetworkWrapper(thing))
  def networkList(): Iterable[String] = fileList(NetworkWrapper)

  private def fileList[T](thing: ReadObject[T]): Iterable[String] = {
    val text = getTextRegex(thing)
    val byte = getByteRegex(thing)
    getFiles(thing.folder)
      .flatMap { p =>
        p.getFileName.toString match {
          case text(fn) => Some(fn)
          case byte(fn) => Some(fn)
          case _ => None
        }
      }
  }

  private def getTextRegex[T](reader: ReadObject[T]): Regex = s"""(.*)\\.${reader.extension}""".r
  private def getByteRegex[T](reader: ReadObject[T]): Regex = s"""(.*)\\.${reader.extension}b""".r

  def readFromText[T](name: String, thing: ReadObject[T]): T = thing.fromText(readText(Paths.get(thing.folder, s"$name.${thing.extension}")))
  def readFromBytes[T](name: String, thing: ReadObject[T]): T = thing.fromBytes(readBytes(Paths.get(thing.folder, s"$name.${thing.extension}b")))
  def writeAsText[T](name: String, thing: WriteObject[T]): Unit = writeText(Paths.get(thing.folder, s"$name.${thing.extension}"), thing.asText())
  def writeAsBytes[T](name: String, thing: WriteObject[T]): Unit = writeBytes(Paths.get(thing.folder, s"$name.${thing.extension}b"), thing.asBytes())

  def readText(path: Path): String
  def readBytes(path: Path): Array[Byte]
  def writeText(path: Path, thing: String): Unit
  def writeBytes(path: Path, thing: Array[Byte]): Unit

  def getFiles(folder: String): Iterable[Path]
  def getFiles(folder: Path): Iterable[Path]
}
