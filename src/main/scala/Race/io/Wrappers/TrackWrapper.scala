package Race.io.Wrappers

import Race.common.{Point, Track}
import Race.io.{ReadObject, WriteObject}
import java.nio.ByteBuffer

object TrackWrapper extends ReadObject[Track] {

  override def extension: String = "track"

  override def folder: String = "Tracks"


  override def fromText(test: String): Track = {
    val lineReg = """\(Point\((\d+), (\d+)\), Point\((\d+), (\d+)\)\),""".r
    val points = test
      .lines
      .flatMap { line =>
        line match {
          case lineReg(x1, y1, x2, y2) => Some((Point(x1.toInt, y1.toInt), Point(x2.toInt, y2.toInt)))
          case _ => None
        }
      }
    Track(points.toIterable)
  }

  override def fromBytes(data: Array[Byte]): Track = {
    val bb = ByteBuffer.wrap(data)
    val points = for (i <- data.indices by 16) yield (Point(bb.getInt(i), bb.getInt(i + 4)), Point(bb.getInt(i + 8), bb.getInt(i + 12)))
    Track(points)
  }

  def apply(points: Iterable[(Point, Point)]): WriteObject[Track] = apply(Track(points))
  def apply(track: Track): WriteObject[Track] = TrackWrapperImpl(track)

  case class TrackWrapperImpl(track: Track) extends WriteObject[Track] {

    override def extension: String = TrackWrapper.extension
    override def folder: String = TrackWrapper.folder
    override def asText(): String = {
      val sb = new StringBuilder
      val eol = sys.props("line.separator")
      for ((p1, p2) <- track.points){
        val line = s"(Point(${p1.x}, ${p1.y}), Point(${p2.x}, ${p2.y})),$eol"
        sb.append(line)
      }

      sb.toString()
    }

    override def asBytes(): Array[Byte] = {
      val points = track.points
      val bb = ByteBuffer.allocate(points.size * 4 * 4)
      for (((p1, p2), idx) <- points.zipWithIndex){
        val offset = idx * 16
        bb.putInt(offset,      p1.x)
        bb.putInt(offset + 4,  p1.y)
        bb.putInt(offset + 8,  p2.x)
        bb.putInt(offset + 12, p2.y)
      }

      bb.array()
    }
  }

}
