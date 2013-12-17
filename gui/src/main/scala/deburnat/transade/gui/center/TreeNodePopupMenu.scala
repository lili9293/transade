package deburnat.transade.gui.center

import scala.swing.{ScrollPane, Dimension}

import deburnat.transade.gui.components.{MonoNotEditableTextArea, PopupMenu}
import deburnat.transade.gui.admins.GuiAdmin.br

/**
 * An algorithm for data transfer.
 * Project name: deburnat
 * Date: 9/3/13
 * Time: 3:40 PM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[center] case class TreeNodePopupMenu(text: String) extends PopupMenu(new ScrollPane{
  val h = text.split(br).length * 20
  val prefH = if(h > 600) 600 else h

  viewportView = new MonoNotEditableTextArea(text.replace("\t", "    "))

  preferredSize = new Dimension(500, prefH)
})
