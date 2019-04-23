package Race.evolution

import Race.NeuralNetwork.Network
import Race.NeuralNetwork.Network.{NetworkImpl, backwardPropagation, sigmoid, tanh}
import org.scalatest.{FunSuite, Matchers}

class NetworkTest extends FunSuite with Matchers {

  test ("super simple network") {

    val network = Network.random(3, 2)

    val result = network.evaluate(Array(1, 2, 1))

    result should have length 2
    result.head should be > -1d
    result.head should be < 1d
    result(1) should be > -1d
    result(1) should be < 1d
  }

  test ("train And") {
    val network1 = Network.random(2, 3, 3, 1)
    val net = network1.copy(network1.layers, List(tanh, sigmoid, sigmoid))

    val X = List(Array(-1d, -1d), Array(-1d, 1d), Array(1d, -1d), Array(1d, 1d))
    val Y = List(Array(0d), Array(0d), Array(0d), Array(1d))

    val r = Network.train(net, X, Y, 0.3, 5000)

    r.tails
      .filter(_.size >= 2)
      .foreach(t => t.head > t(1) shouldBe true)

    X.zip(Y)
      .foreach { case (x, y) => Math.round(net.evaluate(x).head) shouldBe y.head}
  }

  test ("back propagate And") {
    var net = Network.random(List(2, 3, 3, 1), 1, 0, Some(List(tanh, sigmoid, sigmoid)))

    val X = List(Array(0d, 0d), Array(0d, 1d), Array(1d, 0d), Array(1d, 1d))
    val Y = List(Array(0d), Array(0d), Array(0d), Array(1d))

    val lr = 0.3

    val mse = X
      .map(net.evaluate)
      .zip(Y)
      .map { case (actual, expected) => actual.zip(expected).map { case (a, e) => Math.pow(e - a, 2) }.sum }
      .sum / X.size.toDouble

    var r = List[Double](mse)

    for (_ <- 1 to 300){
      net = X.zip(Y).foldLeft(net) { case (n, (input, y)) => backwardPropagation(n, input, y, lr) }

      val newMse = X
        .map(net.evaluate)
        .zip(Y)
        .map { case (actual, expected) => actual.zip(expected).map { case (a, e) => Math.pow(e - a, 2) }.sum / actual.length }
        .sum / X.size.toDouble

      //newMse < r.head shouldBe true

      r = newMse :: r
    }

    X.zip(Y)
      .foreach { case (x, y) => Math.round(net.evaluate(x).head) shouldBe y.head}
  }

  test ("activations") {

    val network = Network.random(3, 3, 2)

    val input = Array(1d, 2d, 1d)

    val activations = network.activations(input)

    activations.size shouldBe 3

    activations.head.getColumn(0) shouldBe input

    activations.last.getColumn(0) shouldBe network.evaluate(input)
  }

  test ("random") {
    val network = Network.random(3, 2, 5, 6)

    network.layers.size shouldBe 3

    val result = network.evaluate(Array(1,2,3))

    result.length shouldBe 6
  }


  test ("get dimensions") {
    val dimensions = List(3, 2, 5, 6)

    val network = Network.random(dimensions: _*)

    network.dimensions shouldBe dimensions
  }

  test {"weights"} {
    val network = Network.random(3, 2, 5, 6)
    val weights = network.weights.toList

    weights.size shouldBe 4*2 + 3*5 + 6*6
  }

  test ("copy constructor"){

    val network1 = Network.random(3, 2, 5, 6)

    val network2 = Network(network1)

    val input = Array(1.0,2.0,3.0)

    val result1 = network1.evaluate(input)

    result1.length shouldBe 6
    result1.sum should not be 0.0

    network2.evaluate(input) shouldBe result1

  }
}
