package deburnat.transade.gui.north

import swing.{Publisher, Component, event}
import event.Event
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.JComboBox

import collection.mutable.ArrayBuffer
import deburnat.transade.{core, gui}
import core.readers.XmlReader
import gui.admins.TemplatesAdmin._
import gui.admins.GuiAdmin._

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 9/1/13
 * Time: 12:58 AM
 */

/**
 * This class is an extender of the javax.swing.JComboBox class (see JComboBox.java specification).
 * It provides a list of the currently saved templates and couple methods enabling their render.
 */
protected[gui] final class TemplatesComboBox extends Component with ActionListener with Publisher{
  private lazy val templates: ArrayBuffer[String] = getTemplates

  override lazy val peer = new JComboBox(templates.toArray) //
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

  //this method is used to determine whether or not the item default (empty)
  def nonEmpty: Boolean = {item != ph}

  //self explanatory
  def contains(item: String) = templates.contains(item)
  def item = peer.getSelectedItem.asInstanceOf[String] //get the current selected item.
  def reset{peer.setSelectedIndex(0)} //used by the -= method and the class TransFileChooser


  peer.addActionListener(this) //listenTo
  def actionPerformed(e: ActionEvent){ //reactions
    val template = e.getSource.asInstanceOf[JComboBox[String]].getSelectedItem.asInstanceOf[String]
    if(template.trim.nonEmpty) publish(TemplateSelectedEvent(new XmlReader(tDir.format(template))))
      //received by TransFileChooser, TransTabbedPane & ProcessPanel
  }
}

/**
 * This event is
 * - fired/published as soon as an item (template) in the template combo box is selected and
 * - handled/received by the TransferFileChooser, gui.center.ProcessPanel and gui.center.TransTabbedPane.
 * @param reader The selected (template).xml file reader
 */
protected[gui] case class TemplateSelectedEvent(reader: XmlReader) extends Event

