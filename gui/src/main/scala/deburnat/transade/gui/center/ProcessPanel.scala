package deburnat.transade.gui.center

import deburnat.transade.gui.{north, components, admins}

import swing.{BorderPanel, CheckBox, ComboBox, Swing, event}
import event.{FocusGained, FocusLost, MouseClicked}
import BorderPanel.Position._
import north.{TemplatesComboBox, TemplateSelectedEvent}
import components.{HBoxPanel, TransOptionPane, MonoTextField, LButton}
import TransOptionPane.warn

import scala.xml.Node
import collection.mutable.Map
import admins.GuiAdmin._
import admins.TemplatesAdmin.{tMode, tShow}
import deburnat.transade.Mode._

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
 * This class is used to process all (TRANSADé).xml files.
 * @param tabbedPane see the TransTabbedPane class.
 * @param nodeCheckBoxMapMap The collection of all maps (all pages) made of all [transfer] nodes and their checkbox.
 * @param templates see the gui.north.TemplatesComboBox class.
 */
protected[center] class ProcessPanel(
  tabbedPane: TransTabbedPane, nodeCheckBoxMapMap: Map[Int, Map[Node, CheckBox]],
  templates: TemplatesComboBox
) extends BorderPanel{
  private val modes = Map[String, String]()
  list.foreach{l => modes(view.read(l)) = l}

  //a BoxPanel at the place of a BorderPanel can also do the trick
  layout(new BorderPanel{
    val (modeComboBox, showCheckBox) = (
      new ComboBox(modes.keys.toSeq){selection.item = view.read(list(0))},
      new CheckBox(view.read("openreport")){
        selected = true
        tooltip = tRead("openreport")
      }
    )

    //WEST
    layout(new HBoxPanel{
      contents += Swing.HStrut(13) //to enable the alignment on the gui
      contents += modeComboBox
      contents += Swing.HStrut(10)
      contents += showCheckBox
      contents += Swing.HStrut(50)
    }) = West

    //CENTER: template TextField
    private val templateTextField = new MonoTextField{
      val _text = "templatetext"
      tooltip = tRead(_text)
      val vText = view.read(_text)

      def empty = (text.trim == vText && foreground == off) || text.trim.isEmpty
      def reset{text = vText; foreground = off} //default settings

      reset

      listenTo(mouse.clicks, this)
      reactions += {
        case e: MouseClicked =>
          if(e.peer.getButton == 1) text = ""
          else{ //right click (and wheel click) to keep the current word in the TextField
            if(empty) text = ""
            requestFocus
          }
          foreground = on

        case e: FocusLost => if(text.trim == "") reset
        case e: FocusGained => if(empty){text = ""; foreground = on}
      }
    }
    layout(templateTextField) = Center

    //EAST: RunButton
    //val page = tabbedPane.selected.page
    val idx = tabbedPane.selectedIdx
    layout(new LButton("run",
      try{ //ONCLICK
        if(coreAdmin.goodToGo){
          //First make sure that the templateTextField is adequately set
          if(templateTextField.empty) templateTextField.text = ""

          //Second run the play/run-button
          val mode = modeComboBox.selection.item
          RunButton.onClick(
            nodeCheckBoxMapMap(idx), templates,
            (mode, modes(mode)), showCheckBox.selected,
            templateTextField, templateTextField.reset,
            tabbedPane.getPath, tabbedPane.setComputationDone
          )
        }else warn("goodtogo")
      }catch{case e: NoSuchElementException => //this exception is thrown if the button is click before a file is selected
        templateTextField.reset
        warn("file")
      }
    ){border = Swing.EmptyBorder(0, 5, 0, 0)}) = East


    listenTo(templates) //react accordingly if a template is selected
    reactions += {
      case TemplateSelectedEvent(r) =>
        modeComboBox.selection.item = view.read(r.read(tMode))
        val show = r.read(tShow)
        showCheckBox.selected = if(show.matches("(true|false)")) show.toBoolean else true
    }

    border = Swing.EmptyBorder(5, 30, 5, 400)
  }) = Center

}



