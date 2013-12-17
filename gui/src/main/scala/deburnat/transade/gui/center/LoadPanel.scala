package deburnat.transade.gui.center

import collection.mutable.Map
import swing.{Orientation, BorderPanel, CheckBox, ComboBox, BoxPanel, Swing, event}
import event.{FocusGained, FocusLost, MouseClicked}
import Orientation._
import BorderPanel.Position._
import scala.xml.Node

import deburnat.transade.gui.admins.GuiAdmin._
import deburnat.transade.gui.admins.TemplatesAdmin.{tMode, tShow}
import deburnat.transade.Mode._
import deburnat.transade.gui.north.{TemplatesComboBox, TemplateSelectedEvent}
import deburnat.transade.gui.components.{TransOptionPane, MonoTextField, LButton}
import TransOptionPane.warn

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 8/28/13
 * Time: 8:56 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[center] class LoadPanel(
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
    layout(new BoxPanel(Horizontal){
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



