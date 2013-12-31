package deburnat.transade.gui.north

import swing.{Publisher, Point, event}
import event._
import javax.swing.{JFileChooser, filechooser}
import filechooser.FileNameExtensionFilter
import deburnat.transade.gui.admins.GuiAdmin.{xml, _xml, view, tRead}

import java.io.File
import deburnat.transade.gui.components.MonoTextField

/**
 * An algorithm for data transfer.
 * Project name: transade
 * Date: 10/2/13
 * Time: 1:00 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */

/**
 * This is an extender of the scala.swing.MonoTextField class.
 * By additionally extending the scala.swing.Publisher trait it inherits the ability to be listened
 * by other components such as the TabbedPane component below.
 * @param templates see TemplatesComboBox.scala
 */
protected[gui] class TransFileChooser(templates: TemplatesComboBox)
extends MonoTextField with Publisher{
  /**
   * This method is used to open the file chooser.
   * @param path The current path
   * @param point The location where the file chooser should appear.
   * @return The newly chosen path otherwise the path prior to the method invocation.
   */
  private def openFileChooserAt(path: String, point: Point): String = {
    val file = new File(path)

    /* An extension of the javax.swing.JFileChooser was chosen over the scala.swing.FileChooser
     * because it was the only way to obtain a file choose whose location can be changed at will.
     * The original JFileChooser automatically sets the chooser component in the middle of its
     * parent component.
     */
    object MoveableFileChooser extends JFileChooser(if(file.exists) file else new File("")){
      override protected def createDialog(parent: java.awt.Component) = {
        val dialog = super.createDialog(parent)
        dialog.setLocation(point) //dialog.setResizable(false)
        dialog
      }
      setFileSelectionMode(JFileChooser.FILES_ONLY)
      setFileFilter(new FileNameExtensionFilter(_xml+" "+ view.read("files"), xml)) //only .xml files can be chosen
    }

    if(MoveableFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
      //Set the TemplatesComboBox to its default value because the TransFileChooser is currently being used
      templates.reset
      val path = MoveableFileChooser.getSelectedFile.getAbsolutePath
      publish(TransFileChosenEvent(path))
      foreground = on
      path
    }else path

    /* Here is the scala version of the FileChooser
     * val fileChooser = new FileChooser(new File("./")){
     *  fileSelectionMode = FileChooser.SelectionMode.FilesOnly
     *  fileFilter = new FileNameExtensionFilter(".xml files", "xml")
     *  //peer.setLocation(point) //This statement doesn't work.
     * }
     * if(fileChooser.showOpenDialog(null) == FileChooser.Result.Approve)
     * ...
     */
  }

  //the component itself
  tooltip = tRead("filechooser")
  val vText = view.read("textfilechooser")
  text = vText //default settings
  foreground = off

  def empty = text.trim == vText && foreground == off

  listenTo(keys, mouse.clicks, templates)
  reactions += {
    case e: MouseClicked =>
      val mButton = e.peer.getButton
      if(mButton == 1) text = openFileChooserAt(text, e.peer.getLocationOnScreen) //left click
      else{ //right click (and wheel click)
        if(empty) text = ""
        requestFocus
      }
      foreground = on

    case KeyPressed(_, Key.Enter,_,_) =>
      if(text.endsWith(_xml) && new File(text).exists) publish(TransFileChosenEvent(text))
      else text = openFileChooserAt(text, locationOnScreen) //src.bounds.getLocation

    case e: TemplateSelectedEvent =>
      text = vText
      foreground = off
      //empty the TransFileChooser because the TemplatesCombobox is currently being used

    case e: FocusLost => if(text.trim.isEmpty){
      text = vText
      foreground = off
    }

    case e: FocusGained => if(empty){
      text = ""
      foreground = on
    }
  }
}

/**
 * This event is used to pass the chosen file using the file chooser to the tabbed pane.
 * @param xmlFilePath The chosen file xmlFilePath.
 */
protected[gui] case class TransFileChosenEvent(xmlFilePath: String) extends Event

