package deburnat.transade.gui.north

import swing._
import Orientation.{Horizontal, Vertical}
import BorderPanel.Position.{North, Center, South, East}
import Swing.{EmptyBorder, VStrut}
import event.KeyReleased
import collection.mutable.Map

import javax.swing.ImageIcon
import javax.swing.border.TitledBorder

import deburnat.transade.gui.admins.GuiAdmin._
import deburnat.transade.gui.components.{MonoTextField, PopupMenu, LButton, TransOptionPane}
import TransOptionPane._

/**
 * An algorithm for data transfer.
 * Project name: deburnat
 * Date: 9/2/13
 * Time: 1:30 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[north] class SettingsPopupMenu extends PopupMenu(new BorderPanel{

  private val (w, checkLabel, _dirPath, _language, iconOk, iconNotOk) = (
    350, new Label, dirPath, language,
    new ImageIcon(imgPath.format("ok")), new ImageIcon(imgPath.format("notOk"))
  )
  private val dirTextField = new MonoTextField(_dirPath){
    //preferredSize = new Dimension(w, h)
    //maximumSize = new Dimension(w, h)
    def updateCheckLabel = if(isDirPathValid(text)) checkLabel.icon = iconOk
    else checkLabel.icon = iconNotOk

    tooltip = pRead("dirtooltip")
    updateCheckLabel
    listenTo(keys)
    reactions += {case e: KeyReleased => updateCheckLabel}
  }

  private val dirPanel = new BoxPanel(Horizontal){
    contents += dirTextField
    contents += checkLabel
    border = new TitledBorder(pRead("dirtitle"))
  }
  layout(dirPanel) = North

  //RADIO BUTTONS
  private val (radioGroup, radios) = (new ButtonGroup, coreAdmin.languages.map(s => new RadioButton(s)))
  private val default = if(_language.nonEmpty) radios.find(r => r.text == _language).get else new RadioButton("")
  radioGroup.buttons ++= radios
  radioGroup.select(default)

  private val radioPane = new ScrollPane{
    viewportView = new BoxPanel( //orientation ratio: not more than 3 items per lines
      if(coreAdmin.languages.length * 100 > w) Vertical else Horizontal
    ){
      contents ++= radios
      //preferredSize = new Dimension(w+zw, h+zh)
      //maximumSize = new Dimension(w+zw, h+zh)
      border = new TitledBorder(pRead("langtitle"))
    }
    border = EmptyBorder
  }
  layout(radioPane) = Center

  private val tickPanel = new BorderPanel{
    layout(VStrut(10)) = North

    var failureCounter = 0 //counts the number of unexpected failures occurring the update.
    layout(new LButton("update", { //onclick
      val labVals = Map[String, Any]()

      if(checkLabel.icon == iconOk && dirTextField.text != _dirPath)
        labVals += labDirPath -> dirTextField.text.replace(sep, _sep)
        //the file separators have to be removed before saving the directory

      try{
        val selected = radioGroup.selected.get
        if(selected != default) labVals(labLang) = selected.text //labVals += ((labLang, selected.text))
      }catch{case e: NoSuchElementException => } //thrown if the go button is click before setting the language

      if(labVals.nonEmpty){
        if(confirm("popupupdate")){
          if(deburnat.transade.MainFrame.top.resetContent(labVals)) failureCounter = 0
          else if(failureCounter < 3){
            warn("popupnoupdate")
            failureCounter += 1
          }else if(failureCounter == 3) warn("popupnoupdatecritical")
        }
      }else warn("popupnochange")
    })) = East
  }
  layout(tickPanel) = South

  border = new TitledBorder(pRead("title"))
  preferredSize = new Dimension(w,
    dirPanel.preferredSize.height + radioPane.preferredSize.height + tickPanel.preferredSize.height + 30
  )
  maximumSize = new Dimension(w, defH - 200)
})

/* Alternative
class DPopupMenu extends Component{
  override lazy val peer = new JPopupMenu

  def +=(item: Component){peer.add(item.peer)}
  def show(invoker: Component, x: Int, y: Int){peer.show(invoker.peer, x, y)}
}*/

