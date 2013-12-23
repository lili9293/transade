package deburnat.transade.gui.north

import swing._
import Swing.VStrut
import BorderPanel.Position._

import java.awt.Desktop.getDesktop
import javax.swing.border.TitledBorder

import deburnat.transade.gui.admins.GuiAdmin._
import deburnat.transade.gui.components.{VBoxPanel, TransOptionPane, LButton, PopupMenu}
import TransOptionPane.{_warn, warn}

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 9/3/13
 * Time: 5:06 AM
 *
 * This class is used to provide information about the application.
 */
protected[north] class InfoPopupMenu extends PopupMenu(
  new BorderPanel{
    private class _CheckBox(label: String) extends CheckBox(view.read(label))
    private val (_manual, download) = (new _CheckBox(manual), new _CheckBox("download"))

    border = new TitledBorder(view.read("info"))

    layout(new VBoxPanel{
      contents += _manual
      contents += download
    }) = Center

    layout(new BorderPanel{
      layout(VStrut(10)) = North
      val _coreAdmin = coreAdmin //if the core admin changes this class will automatically be reset

      layout(new LButton("view", { //onClick
        val(s1, s2) = (_manual.selected, download.selected)

        if(s1) getDesktop.browse(_coreAdmin.manualFile.toURI) //show the manual
        if(s2){ //download the schemas
          if(coreAdmin.goodToGo){
            val result = view.read("warndownload", _coreAdmin.downloadSchemas)
            output(result.replace(br, " "))
            _warn(result)
          }else warn("goodtogo")
        }
        if(!(s1 || s2)) warn("item") //no item selected
      })) = East
    }) = South

    preferredSize = new Dimension(200, 100)
  }
)
