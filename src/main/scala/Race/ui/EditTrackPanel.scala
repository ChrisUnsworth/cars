package Race.ui

import java.awt
import java.awt.event.{InputEvent, MouseEvent, MouseListener}
import java.awt.{Color, Graphics, Graphics2D, Polygon, Rectangle}

import Race.ui.Models.Track.polygonFromPoints
import Race.common.Point
import javax.swing.JPanel

import scala.collection.mutable.ListBuffer

class EditTrackPanel(seedTrack: Iterable[(Point, Point)]) extends JPanel with MouseListener {

  var track: ListBuffer[(Point, Point)] = ListBuffer() ++ seedTrack

  var _sections: Option[IndexedSeq[Polygon]] = None

  var moving: Option[UIPoint] = None

  addMouseListener(this)

  def sections: IndexedSeq[Polygon] = {
    if (_sections.isEmpty){
      val x = track.tails
        .filter(_.size > 1)
        .map { l => new Polygon(
          Array(l.head._1.x, l.tail.head._1.x, l.tail.head._2.x, l.head._2.x),
          Array(l.head._1.y, l.tail.head._1.y, l.tail.head._2.y, l.head._2.y),4)}
      val lastSection = new Polygon(
        Array(track.head._1.x, track.last._1.x, track.last._2.x, track.head._2.x),
        Array(track.head._1.y, track.last._1.y, track.last._2.y, track.head._2.y),4)
      _sections = Some(x.toIndexedSeq ++ List(lastSection))
    }

    _sections.get
  }

  def getTrack: List[(Point, Point)] = track.toList
  def setTrack(data: List[(Point, Point)]): Unit = {
    track = ListBuffer() ++ data
    clearCacheRePaint()
  }

  override def paintComponent(g: Graphics): Unit = {
    super.paintComponent(g)
    paintSections(g.asInstanceOf[Graphics2D])
    paintTrack(g.asInstanceOf[Graphics2D])
    drawPoints(g.asInstanceOf[Graphics2D])
  }


  private def paintSections(g: Graphics2D): Unit = {
    val sec = sections
    g.setColor(Color.GREEN)
    g.fill(sec.head)

    g.setColor(Color.LIGHT_GRAY)
    for(i <- 2 until sec.size by 2){
      g.fill(sec(i))
    }
  }


  private def paintTrack(g: Graphics2D): Unit = {
    g.setColor(Color.BLACK)
    g.draw(outerBoundary)
    g.draw(innerBoundary)
  }

  private def drawPoints(g: Graphics2D): Unit = {
    g.setColor(Color.BLACK)
    for (p <- track.flatMap(p => List(p._1, p._2))){
      val rect = asBox(p)
      g.fill(rect)
    }
  }

  def clearCacheRePaint(): Unit = {
    _sections = None
    repaint()
  }

  private def asBox(p: Point): Rectangle = new Rectangle(p.x - 2, p.y - 2, 4, 4)

  def outerBoundary: Polygon = polygonFromPoints(track.map(_._1))

  def innerBoundary: Polygon = polygonFromPoints(track.map(_._2))

  def closestPoint(p: awt.Point): (Double, UIPoint) = closestPoint(Point(p.x, p.y))
  def closestPoint(p: Point): (Double, UIPoint) = {
    val (d, idx, side) = track
      .zipWithIndex
      .flatMap { case ((p1, p2), i) => List((Point.distance(p1, p), i, 'o'), (Point.distance(p2, p), i, 'i')) }
      .minBy(_._1)

    (d, UIPoint(idx, side))
  }

  def getSection(p: awt.Point): Option[Int] = {
    sections
      .zipWithIndex
      .find { case (poly, _) =>  poly.contains(p)}
      .map(_._2)
  }

  def move(from: UIPoint, p: awt.Point): Unit = move(from, Point(p.x, p.y))
  def move(from: UIPoint, to: Point): Unit = {
    val oldEntry = track(from.idx)
    val newEntry = from.side match {
      case 'o' => (to, oldEntry._2)
      case 'i' => (oldEntry._1, to)
    }

    track.remove(from.idx)
    track.insert(from.idx, newEntry)
    clearCacheRePaint()
  }

  def delete(idx: Int): Unit = {
    track.remove(idx)
    clearCacheRePaint()
  }

  def add(idx: Int): Unit = {
    val poly = sections(idx)
    val p1 = Point((poly.xpoints(0) + poly.xpoints(1)) / 2, (poly.ypoints(0) + poly.ypoints(1)) / 2)
    val p2 = Point((poly.xpoints(2) + poly.xpoints(3)) / 2, (poly.ypoints(2) + poly.ypoints(3)) / 2)
    track.insert(idx + 1, (p1, p2))
    clearCacheRePaint()
  }

  override def mouseClicked(e: MouseEvent): Unit = {
    if ((e.getModifiers & InputEvent.CTRL_MASK) > 0){
      getSection(e.getPoint).foreach(add)
    } else if ((e.getModifiers & InputEvent.ALT_MASK) > 0){
      getSection(e.getPoint).foreach(delete)
    }
  }

  override def mousePressed(e: MouseEvent): Unit = {
    if ((e.getModifiers & InputEvent.SHIFT_MASK) > 0){
      val (d, uip) = closestPoint(e.getPoint)
      if (d < 20) moving = Some(uip)
    }
  }

  override def mouseReleased(e: MouseEvent): Unit = {
    if (moving.isDefined && (e.getModifiers & InputEvent.SHIFT_MASK) > 0){
      move(moving.get, e.getPoint)
    }

    moving = None
  }

  override def mouseEntered(e: MouseEvent): Unit = {}

  override def mouseExited(e: MouseEvent): Unit = {
    moving = None
  }

  case class UIPoint(idx: Int, side: Char)
}
