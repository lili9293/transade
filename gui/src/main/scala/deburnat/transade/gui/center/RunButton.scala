package deburnat.transade.gui.center

import deburnat.transade.{core, Mode, gui}

import swing._
import java.awt.Desktop.getDesktop
import gui.admins.GuiAdmin._
import gui.admins.TemplatesAdmin._
import gui.components.TransOptionPane._
import gui.north.TemplatesComboBox

import collection.mutable.{ListBuffer, Map}
import scala.xml.Node //do not remove the "scala" package name
import core.admins.FileAdmin.save
import core.readers.Reader._

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
 * This object contains the method used once the "run" button is clicked.
 */
protected[center] object RunButton{
  /**
   * This method used once the "run" button is clicked.
   * @param nodeCheckBoxMap A map made of all [transfer] nodes and their checkbox residing on a given page.
   * @param templates see the gui.north.TemplatesComboBox class
   * @param mode The set computation mode.
   * @param showReport The decision about whether to show the report at the end of the process or not.
   * @param templateTextField The template text field object.
   * @param resetTemplateTextField The method used to reset the template text field object.
   * @param xmlFilePath The (TRANSADé).xml file path.
   * @param setComputationDone see the TransTabbedPane.setComputationDone method.
   */
  def onClick(
    nodeCheckBoxMap: Map[Node, CheckBox], templates: TemplatesComboBox,
    mode: (String, String), showReport: Boolean, //(view text, view label)
    templateTextField: TextField, resetTemplateTextField: => Unit,
    xmlFilePath: String, setComputationDone: => Unit
  ){
    try{
      val (ids, templateName) = (ListBuffer[String](), templateTextField.text.trim.replaceAll("\\W+", "_"))
      //dynCheckBoxes -> NoSuchElementException

      /* Filter the selected CheckBoxes:
       * IF active page as already been set THEN dynCheckBoxes.length = allTransfers.length
       * ELSE dynCheckBoxes.length = 0
       */
      val selectedTransfers = nodeCheckBoxMap.filter(_._2.selected).map{kv =>
        ids += kv._2.text
        kv._1
      }.toSeq

      if(selectedTransfers.nonEmpty){
        val msg = "%s: %s%s: %s".format(
          cRead("runfile"), xmlFilePath+brr,
          cRead("runmode"), mode._1+brr
        ) + (if(templateName.nonEmpty) "%s: %s (%s)%s".format(
          cRead("template"), templateName,
          if(templates.contains(templateName)) cRead("templateexit") else cRead("templatenew"),
          //the line above checks whether or not the given template already exist with the same name
          brr
        ) else "") + "%s:%s%s".format(
          cRead("ids"), br,
          read(ids.mkString(" & "))
        )

        if(_confirm(msg)){
          //Step1: run the computation
          val files = fileLoader.xml.compute(selectedTransfers, Mode.toMode(mode._2))

          //Step 2: handle the new template
          if(templateName.nonEmpty){ //the template is adequately set in the ProcessPanel case class
            /*
            <template name={templateName}>
              <date>{date}</date> //tDate
              <xmlfilepath>{loadedFile.xmlFilePath}</xmlfilepath> //tPath
              <mode>{mode._2}</mode> //tMode
              <showreport>{showReport}</showreport> //tShow
              <transferids>{ids.mkString(tSep)}</transferids> //tTrans
            </template>
            */
            val content =
            "<%s name=%s> %s<%s>%s</%s> %s<%s>%s</%s> %s<%s>%s</%s> %s<%s>%s</%s> %s<%s>%s</%s> %s</%s>".format(
              tRoot, a+templateName+a, br+tab1,
              tDate, date, tDate, br+tab1,
              tPath, xmlFilePath, tPath, br+tab1,
              tMode, mode._2, tMode, br+tab1,
              tShow, showReport, tShow, br+tab1,
              tTrans, ids.mkString(tSep), tTrans, br,
              tRoot
            )
            if(save(tDir.format(templateName), content).isFile){
              resetTemplateTextField //set the template TextField to its default state
              templates += templateName //update the template ComboBox
            }
          }

          //Step 3: do the rest: mark the node as computed, open the report file is necessary
          setComputationDone
          if(showReport) getDesktop.open(files._1)
        } //no else
      }else warn("node") //no node selected at all
    }catch{
      case e: NoSuchElementException => warn("file") //no file selected at all
      //case e: NullPointerException => warn("file") //so far there's no occurrence probability for this case
    }
  }

}
