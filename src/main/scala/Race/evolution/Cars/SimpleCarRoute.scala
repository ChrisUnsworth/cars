package Race.evolution.Cars

import java.nio.ByteBuffer

import Race.common.{CarRoute, CarState}

object SimpleCarRoute {
  def apply(text: String): SimpleCarRoute = {
    val states = text.lines.map(s => SimpleCarState(s))
    SimpleCarRoute(states.toList)
  }

  def apply(bytes: Array[Byte]): SimpleCarRoute = {
    val bb = ByteBuffer.wrap(bytes).asReadOnlyBuffer()
    val stateCount = bytes.length / SimpleCarState.byteSize
    val states = (0 until stateCount).map(i => SimpleCarState(bb, i * SimpleCarState.byteSize))
    SimpleCarRoute(states.toList)
  }
}


case class SimpleCarRoute(states: List[SimpleCarState]) extends CarRoute {
  override def stateAt(i: Int): CarState = states(i)
  override def stateCount: Int = states.size

  def asText(): String = states.map(_.asText()).mkString(sys.props("line.separator"))

  def asBytes(): Array[Byte] = {
    val bb = ByteBuffer.allocate(stateCount * SimpleCarState.byteSize)
    for (s <- states) bb.put(s.asBytes(), 0, SimpleCarState.byteSize)
    bb.array()
  }
}