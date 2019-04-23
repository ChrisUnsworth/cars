package Race.NeuralNetwork

import Race.common
import org.apache.commons.math3.analysis.function.Sigmoid
import org.apache.commons.math3.linear.{MatrixUtils, RealMatrix, RealVector}

import scala.util.Random

object Network {

  def apply(other: common.Network): NetworkImpl = apply(other.dimensions, other.weights)

  def apply(dimensions: List[Int], weights: Iterator[Double]): NetworkImpl = {
    val matrices = buildMatrices(dimensions, weights.next())
    NetworkImpl(matrices, matrices.indices.map(_ => tanh).toList)
  }

  def random(dimensions: Int*): NetworkImpl = random(dimensions.toList, 1, -5, None)

  def random(dimensions: List[Int], variance: Double, min: Double, activations: Option[List[ActivationFunction]]): NetworkImpl = {
    val rand = new Random()
    val weights = buildMatrices(dimensions, rand.nextDouble() * variance + min)

    val active = activations.getOrElse(weights.indices.map(_ => tanh).toList)
    NetworkImpl(weights, active)
  }


  private def buildMatrices(dimensions: List[Int], values: => Double): Array[RealMatrix] = {
    val matrices = dimensions
      .tails
      .filter(_.size > 1)
      .map { l =>
        val c = l.head + 1
        val r = l.tail.head
        val arrays = Array.fill(r, c)(values)
        MatrixUtils.createRealMatrix(arrays)
      }

    matrices.toArray
  }

  def train(network: NetworkImpl, inputs: List[Array[Double]], outputs: List[Array[Double]], lr: Double, maxEpochs: Int): List[Double] = {

    var meanSquareErrors = List[Double]()
    for (epoch <- 1 to maxEpochs){
      inputs.zip(outputs).foreach { case (input, y) => backwardPropagation(network, input, y, lr) }

      if (epoch % 10 == 0){
        val mse = inputs
          .map(network.evaluate)
          .zip(outputs)
          .map { case (actual, expected) => actual.zip(expected).map { case (a, e) => Math.pow(e - a, 2) }.sum }
          .sum / inputs.size.toDouble
        meanSquareErrors = mse :: meanSquareErrors
      }
    }

    meanSquareErrors
  }

  def backwardPropagation(network: NetworkImpl, input: Array[Double], Y: Array[Double], lr: Double): NetworkImpl =
    backwardPropagation(network, input, MatrixUtils.createRealVector(Y), lr)

  def backwardPropagation(network: NetworkImpl, input: Array[Double], y: RealVector, lr: Double): NetworkImpl = {
    val activations: Array[RealVector] = network.activations(input).map(_.getColumnVector(0))
    val output = activations.last

    var error: List[RealVector] = List(y.subtract(output))
    var delta: List[RealVector] = List(activationDerivative(error.head, network.activations.last))

    for ((m, (a, i)) <- network.layers.drop(1).reverse.zip(network.activations.zipWithIndex.reverse)){
      error = m.transpose().multiply(toColumnMatrix(delta.head)).getColumnVector(0) :: error
      delta = dropOne(error.head).ebeMultiply(activationDerivative(activations(i), a)) :: delta
    }

    for ((d, i) <- delta.zipWithIndex) {
      val newWeights = toColumnMatrix(d).multiply(toRowMatrix(activations(i))).scalarMultiply(lr)
      val withBiasZero = MatrixUtils.createRealMatrix(newWeights.getData.map(a => 0d +: a))
      network.layers(i) = network.layers(i).add(withBiasZero)
    }

    network
  }

  def dropOne(v: RealVector): RealVector = MatrixUtils.createRealVector(v.toArray.drop(1))

  def toColumnMatrix(v: RealVector): RealMatrix = MatrixUtils.createRealMatrix(v.toArray.map(d => Array(d)))
  def toRowMatrix(v: RealVector): RealMatrix = MatrixUtils.createRealMatrix(Array(v.toArray))

  def activationDerivative(v: RealVector, a: ActivationFunction): RealVector = v.map(a.derivativeVisitor)

  class WeightIterator(val network: NetworkImpl) extends Iterator[Double] {

    var m = 0
    var i = 0
    var j = 0

    override def hasNext: Boolean = m < network.layers.length

    override def next(): Double = {
      val result = network.layers(m).getEntry(i, j)

      j = j + 1
      if (j >= network.layers(m).getColumnDimension) {
        j = 0
        i = i + 1
        if (i >= network.layers(m).getRowDimension) { i = 0; m = m + 1 }
      }

      result
    }
  }

  case class NetworkImpl(layers: Array[RealMatrix], activations: List[ActivationFunction]) extends common.Network {

    def activations(input: Array[Double]): Array[RealMatrix] = {
      layers.zip(activations)
        .scanLeft(MatrixUtils.createColumnRealMatrix(input)) {
          case (in: RealMatrix, (ma, activation)) =>
            val withBias = MatrixUtils.createRealMatrix(Array.fill(in.getColumnDimension)(1d) +: in.getData)
            val result = ma.multiply(withBias)
            result.walkInOptimizedOrder(activation.functionVisitor)
            result
        }
    }

    def matrixEvaluate(input: Array[Double]): RealMatrix = {
      layers.zip(activations)
        .foldLeft(MatrixUtils.createColumnRealMatrix(input)) {
          case (in: RealMatrix, (ma, activation)) =>
            val withBias = MatrixUtils.createRealMatrix(Array.fill(in.getColumnDimension)(1d) +: in.getData)
            val result = ma.multiply(withBias)
            result.walkInOptimizedOrder(activation.functionVisitor)
            result
        }
    }

    override def evaluate(input: Array[Double]): Array[Double] = matrixEvaluate(input: Array[Double]).getColumn(0)

    override def dimensions: List[Int] = (layers.head.getColumnDimension - 1) :: layers.map(_.getRowDimension).toList

    override def weights: Iterator[Double] = new WeightIterator(this)


  }

  object tanh extends ActivationFunction {
    override def derivative(x: Double): Double = 1 - Math.pow(x, 2)
    override def function(x: Double): Double = Math.tanh(x)
  }

  object sigmoid extends ActivationFunction {
    val sig = new Sigmoid()
    override def derivative(x: Double): Double = x * (1 - x)
    override def function(x: Double): Double = sig.value(x)
  }


}
