package deburnat.transade.gui.components

import swing.{Swing, Label}
import swing.Alignment._
import swing.event.{MouseExited, MouseEntered, MouseClicked}
import javax.swing.ImageIcon
import java.awt.{Color, Cursor}

import deburnat.transade.gui.admins.GuiAdmin._

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 9/2/13
 * Time: 4:13 AM
 *
 * This class is used to create a text-less button-like label.
 * @param imgFileName The name of the corresponding -image and -"toolTip" node in the view.xml file.
 * @param onClick The method to invoke once the button/label is clicked.
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