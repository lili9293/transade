package deburnat.transade.gui.admins

import scala.collection.mutable.ArrayBuffer
import java.io.File
import deburnat.transade.gui.admins.GuiAdmin._

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 8/30/13
 * Time: 11:20 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[gui] object TemplatesAdmin {

  val (tDir, tRoot, tDate, tPath, tMode, tShow, tTrans, tSep) = (//template related attributes
    templatesDir + sep + "%s" + _xml, "template", "date",
    "xmlfilepath", "mode", "showreport", "transferids", _sep+hash+cc
  )
  private val ph = (0 until 40).map(i => " ").mkString

  /**
   * The method is used to get a list of all the templates currently available.
   * Outside of its class, this method is only
   * invoked by the TemplateComboBox during its initialisation.
   * @return
   */
  def getTemplates: Array[String] = (
    ArrayBuffer(ph) ++ new File(templatesDir).listFiles
      .filter(file => file.getName.endsWith(_xml))
      .map(file => file.getName.replaceAll(_xml+"$", ""))
  ).toArray
}
