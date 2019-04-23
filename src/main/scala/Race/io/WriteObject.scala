package Race.io

trait WriteObject[T] {
  def extension: String
  def folder: String
  def asText(): String
  def asBytes(): Array[Byte]
}