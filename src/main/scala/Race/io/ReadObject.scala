package Race.io

trait ReadObject[T] {
  def extension: String
  def folder:String
  def fromText(test: String): T
  def fromBytes(data: Array[Byte]): T
}
