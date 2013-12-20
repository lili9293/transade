package deburnat.transade.gui.north

import swing._
import Swing.VStrut
import BorderPanel.Position._
import Orientation._

import java.awt.Desktop.getDesktop
import javax.swing.border.TitledBorder

import deburnat.transade.gui.admins.GuiAdmin._
import deburnat.transade.gui.components.{TransOptionPane, LButton, PopupMenu}
import TransOptionPane.{_warn, warn}

/**
 * An algorithm for data transfer.
 * Project name: deburnat
 * Date: 9/3/13
 * Time: 5:06 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[north] class InfoPopupMenu extends PopupMenu(
  new BorderPanel{
    private class _CheckBox(label: String) extends CheckBox(view.read(label))
    private val (download, transManual, guiManual) = (
      new _CheckBox("download"), new _CheckBox("manualstransade"), new _CheckBox("manualsgui")
    )

    border = new TitledBorder(view.read("info"))

    layout(new BorderPanel{
      border = new TitledBorder(view.read(schemas))
      layout(download) = Center
    }) = North

    layout(new BoxPanel(Vertical){
      border = new TitledBorder(view.read(manuals))
      contents += transManual
      contents += guiManual
    }) = Center

    layout(new BorderPanel{
      layout(VStrut(10)) = North
      val coreAdmin = deburnat.transade.gui.admins.GuiAdmin.coreAdmin
      val (desktop, htmls) = (getDesktop, coreAdmin.htmls)
      layout(new LButton("view", { //onClick
        val(s1, s2, s3) = (download.selected, transManual.selected, guiManual.selected)
        if(s1){
          if(coreAdmin.goodToGo){
            val result = view.read("warndownload", coreAdmin.downloadSchemas)
            output(result.replace(br, " "))
            _warn(result)
          }else warn("goodtogo")
        }
        //TODO think about copying htmls before showing
        if(s2) desktop.browse(htmls(0).toURI)
        if(s3) desktop.browse(htmls(1).toURI)
        if(!(s1 || s2 || s3)) warn("item")
      })) = East
    }) = South

    preferredSize = new Dimension(200, 175)
  }
)
