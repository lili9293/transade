package deburnat.transade.gui.center

import swing._
import Orientation.Horizontal
import BorderPanel.Position._
import javax.swing.border.TitledBorder

import collection.mutable.Map
import xml.Node
import deburnat.transade.gui.admins.GuiAdmin._
import deburnat.transade.gui.north.{TransFileChooser, TemplatesComboBox}

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
 * This class represents the center and south panes.
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
    layout(new ProcessPanel(tabbedPane, nodeCheckBoxMapMap, templates)) = South
  }
  bottomComponent = new ScrollPane{ //the output area
    border = new TitledBorder(oRead("title"))
    viewportView = outputLabel
  }
}
