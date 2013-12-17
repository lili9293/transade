package deburnat.transade.gui.north

import swing.{Publisher, Point, event}
import event._

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

import deburnat.transade.gui.admins.GuiAdmin.{xml, _xml, view, tRead}
import deburnat.transade.gui.components.MonoTextField

/*
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 8/25/13
 * Time: 6:27 PM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 *
 *
 * This is a derivative object of type scala.swing.Component.
 * By additionally extending the scala.swing.Publisher trait it inherits the ability to be listened
 * by other components such as the TabbedPane component below.
 */
protected[gui] class TransFileChooser(templates: TemplatesComboBox)
extends MonoTextField with Publisher{
  /**
   *
   * @param path
   * @param point
   */
  def openFileChooserAt(path: String, point: Point): String = {
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
      setFileFilter(new FileNameExtensionFilter(_xml+" "+ view.read("files"), xml))
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

