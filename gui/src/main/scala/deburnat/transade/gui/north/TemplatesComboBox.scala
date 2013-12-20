package deburnat.transade.gui.north

import swing.{Publisher, Component, event}
import event.Event
import collection.mutable.ArrayBuffer

import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.JComboBox

import deburnat.transade.gui.admins.TemplatesAdmin._
import deburnat.transade.gui.admins.GuiAdmin._
import deburnat.transade.core.readers.XmlReader

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 9/1/13
 * Time: 12:58 AM
 *
 *
 */
protected[gui] class TemplatesComboBox extends Component with ActionListener with Publisher{
  private lazy val _templates = getTemplates
  private lazy val templates: ArrayBuffer[String] = new ArrayBuffer() ++= _templates

  override lazy val peer = new JComboBox(_templates)
  //http://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html#listeners
  tooltip = tRead("templatechoose")


  /**
   * This method is used to alphabetically insert a new item in the current templates combo box.
   * @param item The item (template) to be inserted.
   */
  def +=(item: String) = {
    //the control whether or not the item is empty is done in the object gui.center.RunButton
    templates += item
    peer.insertItemAt(item, templates.sorted.indexOf(item)) //peer.addItem(item)
  }

  /**
   * This method is used to remove the given item from the current templates combo box.
   * @param item The item (template) to be removed.
   */
  def -=(item: String) = {
    //the control whether the item is empty or not is done in the class gui.north.NorthPanel
    templates -= item
    peer.removeItem(item)
    reset
  }

  //
  def contains(item: String) = templates.contains(item)
  def item = peer.getSelectedItem.asInstanceOf[String] //get the current selected item.
  def reset{peer.setSelectedIndex(0)} //used by the -= method and the class TransFileChooser


  peer.addActionListener(this) //listenTo
  def actionPerformed(e: ActionEvent){ //reactions
    val template = e.getSource.asInstanceOf[JComboBox[String]].getSelectedItem.asInstanceOf[String]
    if(template.trim.nonEmpty) publish(TemplateSelectedEvent(new XmlReader(tDir.format(template))))
      //received by TransFileChooser, TransTabbedPane & LoadPanel
  }
}

/**
 * This event is
 * - fired/published as soon as an item (template) in the template combo box is selected and
 * - handled/received by the TransferFileChooser, gui.center.LoadPanel and gui.center.TransTabbedPane.
 * @param reader The selected template (.xml) file reader
 */
protected[gui] case class TemplateSelectedEvent(reader: XmlReader) extends Event

