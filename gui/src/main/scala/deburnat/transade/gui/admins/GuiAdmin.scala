package deburnat.transade.gui.admins

import collection.mutable.{ListBuffer, Map}
import swing.{Alignment, Dimension, Label}
import java.awt.{Font, Color}
import deburnat.transade.gui.components.TransOptionPane.warn

import actors.{Actor, TIMEOUT}
import math.max
import deburnat.transade.{core, FileLoader}
import core.admins._
import FileAdmin._
import core.readers.XmlReader

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
 * This object represents the GUI administrator.
 */
protected[transade] object GuiAdmin extends Admin{

	override protected val resourcesRoot = "gui"

  /********** ATTRIBUTES - START **********/
  private val configPath = platform("config")
  protected[admins] val templatesDir = platform("templates")
  private var reader = new XmlReader(configPath)
  
  val (labDirPath, labLang, labLocX, labLocY, labWidth, labHeight, labXmlFilePaths, monoFont, defW, defH) = (
    "dirpath", "language", "locationX", "locationY", "width", "height", "xmlfilepaths",
    Font.decode(Font.MONOSPACED + "-12"), 1100, 700
  )
  val (frameX, frameY, frameW, frameH, co, op, po, tt, w, imgPath, outputLabel) = (
    {
      val temp = reader.read(labLocX)
      if(temp.nonEmpty) temp.toInt else 0  
    },
    {
      val temp = reader.read(labLocY)
      if(temp.nonEmpty) temp.toInt else 0
    },
    {
      val temp = reader.read(labWidth)
      if(temp.nonEmpty) getW(temp.toInt) else defW
    },
    {
      val temp = reader.read(labHeight)
      if(temp.nonEmpty) getH(temp.toInt) else defH
    },
    "confirm", "output", "popup", "tooltip", "warn", platform("images") + "%s.png", new Label("<html>"){
      horizontalAlignment = Alignment.Left
      verticalAlignment = Alignment.Top
      foreground = Color.BLUE
      font = monoFont
    }
  )
  private val (_br, update) = ("<br>", ListBuffer[String]()) //this object is used to notify the user in case of an update
  val output = (s: String) => {//output method
    outputLabel.text += //update the output Label
      date+_br+
      s.replace(br, "").replaceAll(" {2,}", " ").split(hash).map(_.trim).mkString(_br) + //restructuring the text.
      _br+_br
  }
  private var (_dirPath, _language, _xmlFilePaths) = (
    reader.text(labDirPath).replace(_sep, sep), reader.read(labLang), reader.text(labXmlFilePaths)
  )
  private var _coreAdmin = new CoreAdmin(_dirPath, _language, output)
  private var (_fileLoader, _view: XmlReader) = (new FileLoader(_coreAdmin), _coreAdmin.view)

  /********** ATTRIBUTES - END **********/


  /********** METHODS - START **********/
  def dirPath = _dirPath
  def language = _language
  def xmlFilePaths = _xmlFilePaths
  def coreAdmin = _coreAdmin
  def fileLoader = _fileLoader
  def view = _view

  //
  private def getH(h: Int) = max(h-40, defH)
  private def getW(w: Int) = max(w-20, defW)

  /**
   * This method is used to update the "config.xml" file.
   * @param labVals 1 =: label (the key so to speak), 2 =: the new value
   * @return "true" if the update was successful otherwise "false".
   */
  def updateConfig(labVals: Map[String, Any]): Boolean = {
    //the check whether the set is empty or not is done in the north.SettingsPopupMenu
    var configTxt = reader.root.mkString

    labVals.foreach{labVal =>
      val label = labVal._1
      val (oldVal, newVal, output) = (
        (reader.root \ label).text, labVal._2,
        label == labDirPath || label == labLang //output this update
      )
      val oldNode = if(oldVal.isEmpty){
        if(output) update += label + " = " + newVal
        val temp = "<%s/>".format(label)
        //<label/> or <label></label>
        if(configTxt.contains(temp)) temp else "<%s></%s>".format(label, label)
      }else{
        if(output) update += label + ": " + oldVal + " => " + newVal
        "<%s>%s</%s>".format(label, oldVal, label)
      }

      //update the config text
      configTxt = configTxt.replace(oldNode, "<%s>%s</%s>".format(label, newVal, label))
    }

    save(configPath, configTxt).isFile //save the new text and return the saved status
  }

  def cRead(s: String) = _view.read(co+s)
  def oRead(s: String) = _view.read(op+s)
  def pRead(s: String) = _view.read(po+s)
  def tRead(s: String) = _view.read(tt+s)
  def wRead(s: String) = _view.read(w+s)
  /********** METHODS - END **********/


  /********** OTHERS - START **********/
  /**
   * This attribute is used to check whether all the configuration parameters have been set properly.
   * It is run once, in the background concurrently to the initialization of the main frame.
   */
  lazy val isApiGoodToGo: Unit = new Actor{def act{
    loop{reactWithin(1000){case TIMEOUT =>
      try{//react within 1s
        if(!_coreAdmin.goodToGo) warn("goodtogo") //warn if the configuration parameters aren't set
        exit
      }catch{case e: java.lang.ExceptionInInitializerError => }
      //thrown if the "warn" method is invoked before the initialization of thee main frame is done.
    }}
  }}.start

  output(oRead("welcome")) //this has to be done here to give enough time to the "view" object to be set.
  /********** OTHERS - END **********/
}

/**
 * This constructor is only invoked if the main frame is reset.
 */
protected[deburnat] class GuiAdmin {
  import GuiAdmin._

  //the new reader and the reset chain reaction
  reader = new XmlReader(configPath)
  _dirPath = reader.text(labDirPath).replace(_sep, sep)
  _language = reader.read(labLang)
  _xmlFilePaths = reader.text(labXmlFilePaths)
  _coreAdmin = new CoreAdmin(_dirPath, _language, output)
  _fileLoader = new FileLoader(_coreAdmin)
  _view = _coreAdmin.view
  
  //report the updates
  output(update.mkString(_br))
  update.clear

  def getDim(curW: Int, curH: Int) = new Dimension(getW(curW), getH(curH))
}
