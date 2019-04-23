package Race.io.Wrappers

import java.nio.ByteBuffer

import Race.common.Network
import Race.io.{ReadObject, WriteObject}

object NetworkWrapper extends ReadObject[Network] {

  def apply(network: Network): WriteObject[Network] = NetworkWrapperImpl(network)

  override def extension: String = "network"

  override def folder: String = "Networks"

  override def fromText(text: String): Network = {
    val lines = text.lines

    val dimensions = lines.next().split(' ').map(_.toInt)
    val rows = dimensions.tail.sum
    val weights = (1 to rows)
      .flatMap(_ => lines.next().split(' ').map(_.toDouble))


    NetworkDto(dimensions.toList, weights.toList.iterator)
  }

  override def fromBytes(data: Array[Byte]): Network = {
    val bb = ByteBuffer.wrap(data)
    val dimensionCount = bb.getInt()
    val dimensions = (1 to dimensionCount).map(_ => bb.getInt())
    val db = bb.asDoubleBuffer()
    val weightsCount = db.remaining()
    val weights = (1 to weightsCount).map(_ => db.get())
    NetworkDto(dimensions.toList, weights.iterator)
  }

  case class NetworkDto(dimensions: List[Int], weights: Iterator[Double]) extends Network {
    override def evaluate(input: Array[Double]): Array[Double] = ???
  }

  case class NetworkWrapperImpl(network: Network) extends WriteObject[Network] {
    override def extension: String = NetworkWrapper.extension
    override def folder: String = NetworkWrapper.folder

    override def asText(): String = {
      val sb = new StringBuilder
      val eol = sys.props("line.separator")
      val dimensions = network.dimensions
      val weights = network.weights

      sb.append(dimensions.mkString(" ") + eol)

      for (tail <- dimensions.tails.filter(_.size > 1)){
        val cols = tail.head
        val rows = tail(1)
        for (_ <- 1 to rows){
          val line = (1 to cols).map(_ => weights.next()).mkString(" ") + eol
          sb.append(line)
        }
      }

      sb.toString()
    }

    override def asBytes(): Array[Byte] = {
      val dimensions = network.dimensions

      val size = 4 + dimensions.size * 4 + dimensions.tails.filter(_.size > 1).map(t => (t.head + 1) * t(1)).sum * 8

      val bb = ByteBuffer.allocate(size)
      bb.putInt(dimensions.size)

      for (d <- dimensions) bb.putInt(d)

      val weights = network.weights
      while (weights.hasNext) bb.putDouble(weights.next())

      bb.array()
    }
  }
}
