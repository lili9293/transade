package deburnat.transade

import collection.mutable.Map
import swing._
import BorderPanel.Position._

import javax.swing.WindowConstants

import deburnat.transade.gui.admins.GuiAdmin
import GuiAdmin._
import deburnat.transade.gui.center.CenterPane
import deburnat.transade.gui.north.NorthPanel
import deburnat.transade.gui.components.TransOptionPane.confirm

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 8/25/13
 * Time: 8:50 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
object MainFrame extends SimpleSwingApplication{

  val top = new MainFrame {
    private var centerPane: CenterPane = null

    //ON SET
    private def setContent(_preferredSize: Dimension){
      contents = new BorderPanel{
        val north = new NorthPanel
        centerPane = new CenterPane(xmlFilePaths, north.fileChooser, north.templates, north.deleteButton.doClick)
        layout(north) = North
        layout(centerPane) = Center
        preferredSize = _preferredSize //new Dimension(frameW, frameH)
      }
    }

    //ON UPDATE
    def resetContent(labVals: Map[String, Any]): Boolean = {
      labVals(labXmlFilePaths) = centerPane.getPaths
      if(updateConfig(labVals)){
        setContent(new GuiAdmin().getDim(size.width, size.height))
        true
      }else false
    }

    //ON CLOSE
    peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    override def closeOperation = if(confirm("close")){ //this method is invoked once the close button is clicked
      val (loc, _size) = (location, size) //in this case location = locationOnScreen
      updateConfig(Map( //COOKIES
        labXmlFilePaths -> centerPane.getPaths, labLocX -> loc.x, labLocY -> loc.y,
        labWidth -> _size.width, labHeight -> _size.height
      ))
      super.closeOperation
    }

    /**
     * ON SHOW DIALOG
     * getCenter is a method instead of a val to make sure
     * the current center values are available at all time
     * @return
     */
    def getCenter = (centerPane.locationOnScreen, centerPane.size)


    //SETTINGS OF THE MAIN FRAME
    println(view)
    title = view.read("title")
    location = new Point(frameX, frameY)
    setContent(new Dimension(frameW, frameH))
    minimumSize = new Dimension(defW, defH)
    iconImage = toolkit.getImage(imgPath.format("add")) //TODO choose a logo
  }

  //warn if the configuration parameters aren't set properly
  //This check has to take place after the main frame is created
  isApiGoodToGo


  //This method is automatically invoked right before the application is shut down by the ".quit" method
  //override def shutdown{}
}
