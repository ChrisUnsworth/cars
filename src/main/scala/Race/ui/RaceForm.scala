package Race.ui

import java.awt.BorderLayout
import java.awt.event.{ActionEvent, ActionListener}

import Race.common.{CarRoute, Track, TrainedSet}
import javax.swing.event.{ChangeEvent, ChangeListener}
import javax.swing._

class RaceForm(data: TrainedSet) extends JFrame with ChangeListener with ActionListener {

  val slider = new JSlider(1, data.generations, data.generations)
  val textBox = new JTextField("Generation 50")
  slider.addChangeListener(this)

  val resetButton = new JButton("Reset")
  resetButton.addActionListener(this)
  resetButton.setSize(200, 20)

  val otherPanel = new JPanel()
  otherPanel.setLayout(new BorderLayout())
  otherPanel.add(resetButton, BorderLayout.EAST)
  otherPanel.add(textBox, BorderLayout.CENTER)

  val panel = new RacePanel(data)

  setTitle("Super Awesome Race Cars")
  getContentPane.add(panel, BorderLayout.CENTER)
  getContentPane.add(slider, BorderLayout.SOUTH)
  getContentPane.add(otherPanel, BorderLayout.NORTH)
  setLocationRelativeTo(null)
  pack()
  setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
  setSize(1100, 850)
  setVisible(true)

  override def stateChanged(e: ChangeEvent): Unit = {
    textBox.setText(s"Generation ${slider.getValue}")
    panel.setGeneration(slider.getValue)
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    resetButton.setEnabled(false)
    panel.reset(resetButton)
  }
}
