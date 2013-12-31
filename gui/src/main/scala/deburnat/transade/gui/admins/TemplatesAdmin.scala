package deburnat.transade.gui.admins

import collection.mutable.ArrayBuffer
import java.io.File
import deburnat.transade.gui.admins.GuiAdmin._

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
 * This object represents the templates administrator.
 */
protected[gui] object TemplatesAdmin {

  val (tDir, tRoot, tDate, tPath, tMode, tShow, tTrans, tSep, ph) = (//template related attributes
    templatesDir + sep + "%s" + _xml, "template", "date",
    "xmlfilepath", "mode", "showreport", "transferids", _sep+hash+cc,
    (0 until 40).map(_ => " ").mkString
  )

  /**
   * The method is used to get a list of all the templates currently available.
   * Outside of its class, this method is only
   * invoked by the TemplateComboBox during its initialisation.
   * @return
   */
  def getTemplates: ArrayBuffer[String] = //the default (empty) template always has to be first
    ArrayBuffer(ph) ++ new File(templatesDir).listFiles
    .filter{_.getName.endsWith(_xml)}.map{_.getName.replaceAll(_xml+"$", "")}

}
