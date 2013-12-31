package deburnat.transade.gui.north

import deburnat.transade.gui.{admins, components}

import swing._
import BorderPanel.Position.{Center, East}
import java.awt.Desktop.getDesktop
import components.{HBoxPanel, LButton, TransOptionPane}
import TransOptionPane._

import java.io.File
import admins.GuiAdmin._
import admins.TemplatesAdmin._

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
 * This class represents the north panel of the application. It is made of the loader fields,
 * the info and settings buttons.
 */
protected[transade] class NorthPanel extends BorderPanel{
  //these objects are all accessed by the MainFrame itself to enable its reset
  val templates= new TemplatesComboBox
  val fileChooser = new TransFileChooser(templates)
  val deleteButton = new LButton("delete", if(templates.nonEmpty){ //onClick block
    if(confirm("templatedelete")) delete(templates.item) //no else
  }else warn("item")){
    tooltip = tRead("delete")

    /**
     * This method is used to delete the currently selected template.
     * At this point the item can't be empty so need for a check
     */
    def doClick(){delete(templates.item)}
  }

  /**
   * This method is used to permanently remove the given item from
   * the templates.
   * @param item The template to delete.
   */
  private def delete(item: String){
    val file = new File(tDir.format(item))
    if(file.exists) //the file actually always be existent, but you never know
      if(file.delete){
        templates -= item
        warn("itemsuccess")
      }else warn("itemdeletenot") //the template couldn't be deleted.
    else warn("itemdelete")
    //although the item is still listed, the template (the file) as already been deleted.
  }


  layout(new HBoxPanel{ //a BorderPanel at the place of a BoxPanel can also do the trick
    contents += fileChooser
    contents += Swing.HStrut(50)
    contents += templates
    contents += Swing.HStrut(5)
    contents += deleteButton
    border = Swing.EmptyBorder(0, 10, 0, 300)
  }) = Center

  layout(new HBoxPanel{
    contents += new Separator(Orientation.Vertical)
    contents += Swing.HStrut(10)

    val admin = coreAdmin //the manual button
    contents += new LButton(manual, getDesktop.browse(admin.manualFile.toURI)) //show the manual
    contents += Swing.HStrut(10)

    val settings = new SettingsPopupMenu //the settings button
    contents += new LButton("settings", settings.show(this.contents(4))) //onClick
    contents += Swing.HStrut(10)
  }) = East

  border = Swing.EmptyBorder(5, 5, 5, 5)
}


