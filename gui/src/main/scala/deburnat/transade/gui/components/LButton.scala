package deburnat.transade.gui.components

import swing.{Swing, Label}
import swing.Alignment._
import swing.event.{MouseExited, MouseEntered, MouseClicked}

import javax.swing.ImageIcon
import java.awt.{Color, Cursor}

import deburnat.transade.gui.admins.GuiAdmin._

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 8/27/13
 * Time: 11:07 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 *
 *
 * This class is used to create a text-less button-like label.
 * @param imgFileName
 */
protected[gui] class LButton(imgFileName: String, onClick: => Unit) extends Label{
  private val (defCursor, handCursor) = (Cursor.getDefaultCursor, new Cursor(Cursor.HAND_CURSOR))

  icon = new ImageIcon(imgPath.format(imgFileName))
  tooltip = tRead(imgFileName)
  verticalAlignment = Center
  horizontalAlignment = Center

  //the event handlers
  listenTo(mouse.clicks)
  listenTo(mouse.moves)
  reactions += {
    case e: MouseEntered =>
      border = Swing.LineBorder(Color.LIGHT_GRAY)
      cursor = handCursor

    case e: MouseExited =>
      border = Swing.EmptyBorder(0)
      cursor = defCursor

    case e: MouseClicked => onClick
  }

}