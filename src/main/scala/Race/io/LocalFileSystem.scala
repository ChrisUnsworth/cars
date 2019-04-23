package Race.io

import java.nio.file.{Files, Path, Paths}
import java.nio.charset.StandardCharsets

import scala.collection.JavaConverters._

import scala.io.Source

class LocalFileSystem(root: String = """C:\\test\\RaceData""") extends FileSystem {
  private val eol = sys.props("line.separator")

  override def readText(path: Path): String = Source.fromFile(Paths.get(root, path.toString).toString).getLines().mkString(eol)
  override def readBytes(path: Path): Array[Byte] = Files.readAllBytes(Paths.get(root, path.toString))
  override def writeText(path: Path, thing: String): Unit = writeBytes(path, thing.getBytes(StandardCharsets.UTF_8))
  override def writeBytes(path: Path, thing: Array[Byte]): Unit = {
    val fullPath = Paths.get(root, path.toString)
    Files.createDirectories(fullPath.getParent)
    Files.write(fullPath, thing)
  }

  override def getFiles(folder: Path): Iterable[Path] = getFiles(folder.toString)
  override def getFiles(folder: String): Iterable[Path] = {
    Files.newDirectoryStream(Paths.get(root, folder))
      .asScala
      .map(_.getFileName)
  }
}
