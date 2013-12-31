package deburnat.transade.gui.center

import swing.{ScrollPane, Dimension}
import deburnat.transade.gui.components.{MonoNotEditableTextArea, PopupMenu}

import deburnat.transade.gui.admins.GuiAdmin.br

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 1/1/14
 * Time: 12:00 AM
 *
 * This case class is used to present/show xml nodes via a pop up.
 * @param text The text representing an xml node.
 */
protected[center] case class TreeNodePopupMenu(text: String) extends PopupMenu(new ScrollPane{
  val h = text.split(br).length * 20 //20 as line height is an experimental value
  val prefH = if(h > 600) 600 else h //preferred height (the maximum is 600)
  preferredSize = new Dimension(500, prefH)

  //since the TextArea object doesn't recognize tabs, each tab is replaced by 4 empty characters
  viewportView = new MonoNotEditableTextArea(text.replace("\t", "    "))
})
