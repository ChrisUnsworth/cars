package Race.NeuralNetwork

import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.linear.RealMatrixChangingVisitor

trait ActivationFunction {
  def derivative(x: Double): Double
  def function(x: Double): Double

  def functionVisitor: RealMatrixChangingVisitor = Visitor(function)
  def derivativeVisitor: UnivariateFunction = UF(derivative)



  case class Visitor(function: Double => Double) extends RealMatrixChangingVisitor {
    override def start(rows: Int, columns: Int, startRow: Int, endRow: Int, startColumn: Int, endColumn: Int): Unit = {}
    override def visit(row: Int, column: Int, value: Double): Double = function(value)
    override def end(): Double = 0.0
  }

  case class UF(function: Double => Double) extends UnivariateFunction {
    override def value(x: Double): Double = function(x)
  }
}
