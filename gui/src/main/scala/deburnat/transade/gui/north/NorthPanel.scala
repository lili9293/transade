package deburnat.transade.gui.north

import swing._
import BorderPanel.Position.{Center, East}

import java.io.File

import deburnat.transade.gui.admins.GuiAdmin._
import deburnat.transade.gui.admins.TemplatesAdmin._
import deburnat.transade.gui.components.{LButton, TransOptionPane}
import TransOptionPane._

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 8/27/13
 * Time: 3:04 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[transade] class NorthPanel extends BorderPanel{
  //these objects are all accessed by the MainFrame itself to enable its reset
  val templates= new TemplatesComboBox
  val fileChooser = new TransFileChooser(templates)
  val deleteButton = new LButton("delete", { //onClick method
    val item = templates.item
    if(item.nonEmpty){
      if(confirm("templatedelete")) delete(item) //no else
    }else warn("item")
  }){
    tooltip = tRead("delete")
    def doClick(){delete(templates.item)} //at this point the item can't be empty so need for a check
  }

  /**
   * This method is used to permanently remove the given item from
   * the templates.
   * @param item
   */
  private def delete(item: String){
    val file = new File(tDir.format(item))
    if(file.exists)
      if(file.delete){
        templates -= item
        warn("itemsuccess")
      }else warn("itemdeletenot") //the template couldn't be deleted.
    else warn("itemdelete")
    //although the item is still listed, the template (the file) as already been deleted.
  }


  layout(new BoxPanel(Orientation.Horizontal){ //a BorderPanel at the place of a BoxPanel can also do the trick
    contents += fileChooser
    contents += Swing.HStrut(50)
    contents += templates
    contents += Swing.HStrut(5)
    contents += deleteButton
    border = Swing.EmptyBorder(0, 10, 0, 300)
  }) = Center

  layout(new BoxPanel(Orientation.Horizontal){
    contents += new Separator(Orientation.Vertical)
    contents += Swing.HStrut(10)
    //the info button
    val info = new InfoPopupMenu
    contents += new LButton("info", {info.show(this.contents(2))}) //onClick
    contents += Swing.HStrut(10)
    //the settings button
    val settings = new SettingsPopupMenu
    contents += new LButton("settings", {settings.show(this.contents(4))}) //onClick
    contents += Swing.HStrut(10)
  }) = East

  border = Swing.EmptyBorder(5, 5, 5, 5)
}


