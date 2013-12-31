package deburnat.transade.gui.center

import swing.{ScrollPane, CheckBox, event}
import event.{Event, MouseClicked}

import xml.Node
import collection.mutable.Map
import deburnat.transade.gui.admins.GuiAdmin._
import deburnat.transade.gui.components.{TransOptionPane, VBoxPanel}
import TransOptionPane._

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

/**
 * This case class is used by CenterPane to update the split pane once a file selected.
 * The CoreAdmin.setFile method has to be invoked prior to the CenterPane's invocation.
 * @param xmlFilePath The path of the (TRANSADé).xml file.
 * @param transfers The [transfer] nodes, that the file is containing.                    
 * @param compLists allCheckBox -> the "all" checkbox and help saving the chosen file xmlFilePath.
 *                  boxPane -> the scroll pane used to contain the dynamic check boxes.
 *                  treePane -> the scroll pane used to contain the dynamic trees.
 * @param templateParams ids -> the checked transfers ids during the process of saving the selected template.
 *                       sep -> separator between 2 ids.
 * @param setPageLastParams See TrasTabbedPane.setPageLastParams
 * @param deleteTemplate See gui.north.NorthPanel.deleteButton.doClick
 */
protected[center] case class CheckBoxPanel(
  xmlFilePath: String, transfers: Seq[Node],
  compLists: (TransCheckBox, ScrollPane, ScrollPane),
  templateParams: (String, String),
  setPageLastParams: String => Unit, deleteTemplate: ()=> Unit
){
  /* The list of all dynamic check boxes within the page i of the TabbedPage
   * in which this case class is invoked.
   */
  val nodeCheckBoxMap = Map[Node, CheckBox]() //used in the TransTabbedPane class

  try{
    var ids = templateParams._1
    val allCheckBox = compLists._1

    //update the left split pane component
    compLists._2.contents = new VBoxPanel{ //boxPanes
      //the loop
      transfers.foreach(node => { //transfer node
        val iD = (node \_id).text
        val checkBox = new CheckBox(iD){
          if(ids.contains(iD)){
            selected = true
            ids = ids.replace(iD, "")
            //remove to id from the list -> it should be possible at the end to know
            //which transfer nodes are no longer available.
          }

          tooltip = tRead("checkboxclick")
          listenTo(mouse.clicks, allCheckBox)
          reactions += {
            case e: MouseClicked => if(e.peer.getButton != 1) compLists._3.contents = TransTree(node)
            //update the right split pane component on right click (and on wheel click)

            case AllClickedEvent(state) => this.selected = state
          }
        }//checkBox - END

        contents += checkBox
        nodeCheckBoxMap(node) = checkBox
      }) //foreach - END
    } //BoxPanel - END

    /* Update the "all" checkbox tooltip & save the current file.
     * All the "all" check boxes are register as mouse.click listener during their creation in
     * the CenterPane object
     */
    allCheckBox.tooltip = xmlFilePath
    allCheckBox.reactions += {case e: MouseClicked =>
      val mouseButton = e.peer.getButton //WARNING do not remove this statement.
      //For some reason it doesn't work properly without it
      if(mouseButton == 1) allCheckBox.publish(AllClickedEvent(allCheckBox.selected))
    }
    setPageLastParams(xmlFilePath) //update the page title

    //Some of the transfer node saved during the creation of the template are no longer available
    if(!ids.matches("^(%s)*$".format(templateParams._2))) _warn( //"".matches("^()*$") = true
      view.read("warntemplatefile", xmlFilePath) + br +
      view.read("warntemplatemissing", br + ids.replaceAll(templateParams._2+"$", "").replace(templateParams._2, c+" "))
    )
  }catch{case e: NullPointerException =>
    if(setPageLastParams == null) 'DO_NOTHING
      //caused by TransTabbedPane if during the ini of the app one of the previously saved files is no longer available
    else if(deleteTemplate == null) warn("schema")
      //caused by the TransFileChooser if the file hasn't been set or a file with a wrong schema has been selected
    else if(_confirm(view.read("confirmtemplateerror", xmlFilePath))) deleteTemplate
      //caused by the TemplatesComboBox if either the xml file path retrieved from the template stored file
      //is no longer up to date or the file itself no longer uses the TRANSADé format.
  }
}

/**
 * This event is fired as soon as an All-checkbox is clicked.
 * @param state The state of the clicked All-checkbox.
 */
protected[center] case class AllClickedEvent(state: Boolean) extends Event