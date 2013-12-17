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
 * An algorithm for data transfer.
 * Project name: deburnat
 * Date: 9/1/13
 * Time: 12:58 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[gui] class TemplatesComboBox extends Component with ActionListener with Publisher{
  private lazy val _templates = getTemplates
  private lazy val templates: ArrayBuffer[String] = new ArrayBuffer() ++= _templates

  override lazy val peer = new JComboBox(_templates)
  //http://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html#listeners
  tooltip = tRead("templatechoose")


  /**
   * Insertion
   * @param item
   */
  def +=(item: String) = { //sorted insertion
    //the control wheter or not the item is empty is done in the LoadPanel.scala
    templates += item
    peer.insertItemAt(item, templates.sorted.indexOf(item))
    //peer.addItem(item)
  }

  /**
   * Removal
   * @param item
   */
  def -=(item: String) = {
    //the control whether the item is empty or not is done in the NorthPanel.scala
    templates -= item
    peer.removeItem(item)
  }

  def contains(item: String) = templates.contains(item)
  def item = peer.getSelectedItem.asInstanceOf[String]
  def reset{peer.setSelectedIndex(0)} //used by the TransFileChooser and th NorthPanel


  peer.addActionListener(this) //listenTo
  def actionPerformed(e: ActionEvent){ //reactions
    val template = e.getSource.asInstanceOf[JComboBox[String]].getSelectedItem.asInstanceOf[String]
    if(template.trim.nonEmpty) publish(TemplateSelectedEvent(new XmlReader(tDir.format(template))))
      //received by TransFileChooser, TransTabbedPane & LoadPanel
  }
}

/**
 *
 * @param reader
 */
protected[gui] case class TemplateSelectedEvent(reader: XmlReader) extends Event

