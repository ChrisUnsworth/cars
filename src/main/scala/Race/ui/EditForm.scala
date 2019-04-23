package Race.ui

import java.awt.BorderLayout
import java.awt.event.{ActionEvent, ActionListener}
import java.io.{File, FileOutputStream}
import java.nio.charset.StandardCharsets

import collection.JavaConverters._
import Race.Tracks.Dictionary
import Race.common.Point
import Race.io.FileSystem
import Race.io.Wrappers.TrackWrapper
import javax.swing._

import scala.io.Source

class EditForm(fs: FileSystem) extends JFrame with ActionListener {


  private var trackName = ""
  val _menuBar = new JMenuBar
  val menu = new JMenu("File")
  val menuLoadItem = new JMenuItem("Load")
  menuLoadItem.addActionListener(this)
  val menuSaveItem = new JMenuItem("Save")
  menuSaveItem.addActionListener(this)
  menu.add(menuLoadItem)
  menu.add(menuSaveItem)

  _menuBar.add(menu)

  val trackPanel = new EditTrackPanel(Dictionary.SimpleRingPoints)

  setJMenuBar(_menuBar)
  setTitle("Track editor")
  getContentPane.add(trackPanel, BorderLayout.CENTER)
  setLocationRelativeTo(null)
  pack()
  setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
  setSize(1100, 850)
  setVisible(true)

  override def actionPerformed(e: ActionEvent): Unit = {
    e.getSource match {
      case x if x == menuLoadItem => load()
      case x if x == menuSaveItem => save()
    }

  }

  def oldLoad(): Unit = {
    val fileChooser = new JFileChooser()
    fileChooser.setCurrentDirectory(new File("""C:\test\Tracks"""))
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      val file: File = fileChooser.getSelectedFile
      val source = Source.fromFile(file, "UTF-8")
      val lineReg = """\(Point\((\d+), (\d+)\), Point\((\d+), (\d+)\)\),""".r
      val points = source
        .getLines()
        .flatMap { line =>
          line match {
            case lineReg(x1, y1, x2, y2) => Some((Point(x1.toInt, y1.toInt), Point(x2.toInt, y2.toInt)))
            case _ => None
          }
        }
      trackPanel.setTrack(points.toList)
      source.close()
    }
  }

  def load(): Unit = {
    val names = fs.trackList().toArray
    val chosenName = JOptionPane.showInputDialog(
      this,
      "Tracks:",
      "Load Track",
      JOptionPane.QUESTION_MESSAGE,
      null,
      names.asInstanceOf[Array[AnyRef]],
      names.head)

    if (chosenName != null && chosenName.toString.nonEmpty) {
      trackName = chosenName.toString
      val track = fs.readTrackFromText(trackName)
      trackPanel.setTrack(track.points.toList)
    }
  }

  def save(): Unit = {

    var name: String = null
    var overwrite = false
    val names = fs.trackList().toSet

    do {
      name = JOptionPane.showInputDialog(
        this,
        "Enter track name",
        "Save",

        JOptionPane.QUESTION_MESSAGE
      )

      if (name == null) return
      if (names.contains(name)){
        val selectedOption = JOptionPane.showConfirmDialog(null,
          "Track name already exists, overwrite?",
          "Overwrite",
          JOptionPane.YES_NO_OPTION)
        if (selectedOption == JOptionPane.YES_OPTION) overwrite = true
        else name = null
      }
    } while (name == null)

    fs.writeAsText(name, TrackWrapper(trackPanel.track))
  }
}
