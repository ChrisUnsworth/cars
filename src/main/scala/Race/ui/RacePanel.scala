package Race.ui

import java.awt.event.{ActionEvent, ActionListener}
import java.awt.{Color, Graphics, Graphics2D}

import Race.common.{CarRoute, TrainedSet}
import Race.ui.Models.Track
import javax.swing.{JButton, JPanel, SwingWorker, Timer}

import scala.collection.immutable

class RacePanel(data: TrainedSet) extends JPanel with ActionListener {
  var tic = 0
  private var generationData: immutable.Seq[CarRoute] = _

  private var generation = 0
  private var requestedGen = data.generations

  private var loadDataWorker: SwingWorker[immutable.Seq[CarRoute], Void] = _

  val track = Track(data.track)

  var loadTrackTread: Thread = null

  val timer = new Timer(100, this)

  def reset(button: JButton): Unit = {
    if (generation == requestedGen){
      tic = 0
      button.setEnabled(true)
      return
    }
    generationData = null
    generation = requestedGen
    loadDataWorker = new SwingWorker[immutable.Seq[CarRoute], Void]() {
      override def doInBackground(): immutable.Seq[CarRoute] = data.generation(generation)
      override def done(): Unit = button.setEnabled(true)
    }

    loadDataWorker.execute()
  }


  def setGeneration(gen: Int): Unit = {
    requestedGen = gen
  }

  override def actionPerformed(e: ActionEvent): Unit =  {
    if (e.getSource == timer) {
      timer.stop()
      tic = tic + 1
      revalidate()
      repaint()
    }
  }

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    paintTrack(g.asInstanceOf[Graphics2D])
    paintCars(g.asInstanceOf[Graphics2D])

    if (!timer.isRunning) timer.start()
  }

  private def paintTrack(g: Graphics2D): Unit = {
    g.setColor(Color.BLACK)
    g.draw(track.OuterBoundary)
    g.draw(track.InnerBoundary)
  }

  private def paintCars(g: Graphics2D): Unit = {
    if (generationData == null) {
      if (loadDataWorker != null && loadDataWorker.isDone) generationData = loadDataWorker.get()
      else return
      loadDataWorker = null
      tic = 0
    }

    for ((car , i)<- generationData/*.filter(_.stateCount > tic + 30)*/.zipWithIndex) {
      val state = car.stateAt(tic)

      val p = state.position
      g.setColor(Color.BLACK)
      g.draw(p)
      g.setColor(carColour(i))
      g.fill(p)
    }
  }

  private def carColour(idx: Int): Color = {
    idx % 6 match {
      case 0 => Color.GREEN
      case 1 => Color.RED
      case 2 => Color.BLUE
      case 3 => Color.ORANGE
      case 4 => Color.WHITE
      case 5 => Color.BLACK
    }
  }
}
