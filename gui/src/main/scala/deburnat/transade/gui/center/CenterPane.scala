package deburnat.transade.gui.center

import swing._
import Orientation.Horizontal
import swing.BorderPanel.Position._
import collection.mutable.Map
import xml.Node

import javax.swing.border.TitledBorder

import deburnat.transade.gui.admins.GuiAdmin._
import deburnat.transade.gui.north.{TransFileChooser, TemplatesComboBox}

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 8/27/13
 * Time: 3:17 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[transade] class CenterPane(
  xmlFilePaths: String, fileChooser: TransFileChooser, templates: TemplatesComboBox, deleteTemplate: ()=> Unit
)extends SplitPane(Horizontal){ //horizontal divider
  /* The map of all dynamic check box lists and that of their respective files.
   * Each list belonging to one well defined page of the TabbedPane below.
   * These maps have to be accessible to the "play" LButton below.
   */
  private val nodeCheckBoxMapMap = Map[Int, Map[Node, CheckBox]]()
  private val tabbedPane = new TransTabbedPane(nodeCheckBoxMapMap, xmlFilePaths, fileChooser, templates, deleteTemplate) //the only TabbedPane in the gui
  def getPaths = tabbedPane.getPaths

  //setting the split pane
  continuousLayout = true
  oneTouchExpandable = true
  resizeWeight = .75
  topComponent = new BorderPanel{
    layout(tabbedPane) = Center
    //the mode ComboBox, the template TextField and the compute Button
    layout(new LoadPanel(tabbedPane, nodeCheckBoxMapMap, templates)) = South
  }
  bottomComponent = new ScrollPane{ //the output area
    border = new TitledBorder(oRead("title"))
    viewportView = outputLabel
  }
}
