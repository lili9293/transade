package deburnat.transade.gui.center

import deburnat.transade.gui.{admins, north, components}

import swing._
import BorderPanel.Position._
import Orientation._
import javax.swing.{JTabbedPane, ImageIcon, SwingConstants}
import north.{TemplateSelectedEvent, TransFileChosenEvent, TemplatesComboBox, TransFileChooser}
import components.HBoxPanel

import collection.mutable.{ListBuffer, Map}
import xml.Node
import admins.GuiAdmin._
import admins.TemplatesAdmin._

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 1/1/14
 * Time: 12:00 AM
 */

//This is the TransTabbedPane class attributes repository.
private object TransTabbedPane{
  val (pathsSep, idxPathsSep, iconBlack, iconGreen, iconBlue) = (
    c+_c+cc+hash, hash+cc+_c+c,
    new ImageIcon(imgPath.format("nodeblack")), //no file selected
    new ImageIcon(imgPath.format("nodegreen")),//file selected and good to go
    new ImageIcon(imgPath.format("nodeblue")) //file as been computed
  )

  def loop[Dt](block: Int => Dt) = (0 to 9).map(block(_))
}


/**
 * This class represents the collection of all pages used to visualize the (TRANSADé).xml files.
 * @param nodeCheckBoxMapMap The collection of all maps (all pages) made of all [transfer] nodes and their checkbox.
 * @param xmlFilePaths The (TRANSADé).xml file path.
 * @param fileChooser see the gui.north.TransFileChooser class.
 * @param templates see the gui.north.TemplatesComboBox class.
 * @param deleteTemplate see the gui.north.NorthPanel.deleteButton.doClick method
 */
protected[center] class TransTabbedPane(
  nodeCheckBoxMapMap: Map[Int, Map[Node, CheckBox]], xmlFilePaths: String,
  fileChooser: TransFileChooser, templates: TemplatesComboBox, deleteTemplate: ()=>Unit
) extends Component{
  import TransTabbedPane._

  override lazy val peer = new JTabbedPane

  private val (boxPanes, treePanes, allCheckBoxes, _paths, paths, nr) = (
    ListBuffer[ScrollPane](), ListBuffer[ScrollPane](), ListBuffer[TransCheckBox](),
    xmlFilePaths.split(idxPathsSep), Map[Int, String](), "\\d+"
  )

  /***** TABBED PANE - START *****/
  peer.setMinimumSize(new Dimension(frameW, 200))
  _paths(0).split(pathsSep).foreach{path => //parse the loaded paths
    val _path = path.splitAt(1)
    try{paths(_path._1.toInt) = _path._2}catch{case e: NumberFormatException => }
    //if(_path._1.matches(nr)) paths(_path._1.toInt) = _path._2 //alternative
  }

  loop[Unit]{i =>
    addPage{new HBoxPanel{contents += new SplitPane(Vertical){
      continuousLayout = true
      oneTouchExpandable = true
      resizeWeight = 0.35

      //the left component is made of a fix checkbox and several dynamic others
      val (allCheckBox, boxPane, treePane) = (
        new TransCheckBox(view.read("all")), //the reaction is inserted in the CheckBoxPanel case class
        new ScrollPane, new ScrollPane
      )
      leftComponent = new BorderPanel{
        layout(allCheckBox) = North
        layout(boxPane) = Center
      }
      //the right component is basically a tree representing a chosen checkbox
      rightComponent = treePane

      try{
        val xmlFilePath = paths(i)
        nodeCheckBoxMapMap(i) = CheckBoxPanel(
          xmlFilePath, fileLoader.xml.getTransfers(xmlFilePath), (allCheckBox, boxPane, treePane),
          ("", ""), null, null //no template to delete
        ).nodeCheckBoxMap
      }catch{case e: NoSuchElementException => }

      allCheckBoxes += allCheckBox
      boxPanes += boxPane
      treePanes += treePane
    }}}

    try{ //DO NOT MERGE THE "TRY" BLOCKS
      val xmlFilePath = paths(i)
      if(nodeCheckBoxMapMap(i).nonEmpty){ //set xml file loaded
        peer.setIconAt(i, iconGreen)
        peer.setToolTipTextAt(i, xmlFilePath)
      }
    }catch{case e: NoSuchElementException => }
  }
  peer.setTabPlacement(SwingConstants.LEFT) //JTabbedPane.LEFT is for some reason unavailable
  if(_paths.length == 2) /*DO NOT MERGE*/ if(_paths(1).matches(nr)) peer.setSelectedIndex(_paths(1).toInt)

  listenTo(fileChooser, templates)
  reactions += {
    case e: TransFileChosenEvent =>
      val i = selectedIdx
      nodeCheckBoxMapMap(i) = CheckBoxPanel(
        e.xmlFilePath, fileLoader.xml.getTransfers(e.xmlFilePath),
        (allCheckBoxes(i), boxPanes(i), treePanes(i)), ("",""), setPageLastParams, null //no template to delete
      ).nodeCheckBoxMap

    case TemplateSelectedEvent(r) => //react accordingly if a template is selected
      val (i, xmlFilePath) = (selectedIdx, r.read(tPath))
      nodeCheckBoxMapMap(i) = CheckBoxPanel(
        xmlFilePath, fileLoader.xml.getTransfers(xmlFilePath), (allCheckBoxes(i), boxPanes(i), treePanes(i)),
        (r.read(tTrans), tSep), setPageLastParams, deleteTemplate
      ).nodeCheckBoxMap
  }
  /***** TABBED PANE - END *****/


  /***** METHODS - START *****/
  private def addPage(page: Component) = peer.addTab("", iconBlack, page.peer)

  /**
   * The method used to set the page last parameters.
   * @param toolTipText The path of the (TRANSADé).xml file.
   */
  private def setPageLastParams(toolTipText: String) = {
    val idx = selectedIdx
    peer.setIconAt(idx, iconGreen)
    peer.setToolTipTextAt(idx, toolTipText)
  }

  def selectedTitle = peer.getTitleAt(selectedIdx)
  def selectedIdx = peer.getSelectedIndex

  /**
   * The method is used to set the end lamp (blue icon)
   */
  def setComputationDone = peer.setIconAt(selectedIdx, iconBlue)

  def getPath(idx: Int): String = try{allCheckBoxes(idx).tooltip}catch{case e: IndexOutOfBoundsException => ""}
  def getPath: String = getPath(selectedIdx)

  def getPaths: String = loop[String]{i => i+allCheckBoxes(i).tooltip}.mkString(pathsSep) + idxPathsSep + selectedIdx
  /***** METHODS - END *****/
}


/**
 * This class is used to create the all checkbox.
 * @param label The checkbox label.
 */
protected[center] class TransCheckBox(label: String) extends CheckBox(label) with Publisher{
  listenTo(mouse.clicks)
}


