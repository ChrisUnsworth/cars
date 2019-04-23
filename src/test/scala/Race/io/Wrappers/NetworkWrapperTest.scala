package Race.io.Wrappers

import Race.NeuralNetwork.Network
import org.scalatest.{FunSuite, Matchers}

class NetworkWrapperTest extends FunSuite with Matchers {

  test ("test byte round trip") {
    val network1 = Network.random(3, 2, 5, 6)

    val bytes = NetworkWrapper(network1).asBytes()

    val network2 = NetworkWrapper.fromBytes(bytes)

    network2.dimensions shouldBe network1.dimensions
    network2.weights.toList shouldBe network1.weights.toList
  }

  test ("test text round trip") {
    val network1 = Network.random(3, 2, 5, 6)

    val text = NetworkWrapper(network1).asText()

    val network2 = NetworkWrapper.fromText(text)

    network2.dimensions shouldBe network1.dimensions
    network2.weights.toList shouldBe network1.weights.toList
  }

}
