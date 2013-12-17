package deburnat.transade.core.admins

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import deburnat.transade.core.readers.XmlReader

/**
 * An algorithm for data transfer.
 * Project name: deburnat
 * Date: 9/2/13
 * Time: 4:13 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[transade] trait Admin {
  /********** ATTRIBUTES - START **********/
  final val (
    system, _sep, sep, _xml, _html, _pdf, xml, html, pdf, jar,
    a, c, _c, cc, hash, textPh, br, brr, tb1, tab1
  ) = (
    sys.props, "~", File.separator, ".xml", ".html", ".pdf", "xml", "html", "pdf", ".jar",
    "\"", ",", ";", ",,", "#", "%TEXT%", "\n", "\n\n", "\t", "\t\t"
  )
  final val (
    transfer, schemas, manuals, source, target, id, _id, format, _format,
    defs, _def, _key, _val, parse, tName, _tName, sName, _sName
  ) = (
    "transfer", "schemas", "manuals", "source", "target", "id", "@id", "format", "@format",
    "definitions", "def", "@key", "@value", "parse", "targetname", "@targetname", "sourcename", "@sourcename"
  )
  final val (tb2, tb3, tb4, tb5, tb6, tb7, tb8, tb9) = (
    tb1+tb1,
    tb1+tb1+tb1,
    tb1+tb1+tb1+tb1,
    tb1+tb1+tb1+tb1+tb1,
    tb1+tb1+tb1+tb1+tb1+tb1,
    tb1+tb1+tb1+tb1+tb1+tb1+tb1,
    tb1+tb1+tb1+tb1+tb1+tb1+tb1+tb1,
    tb1+tb1+tb1+tb1+tb1+tb1+tb1+tb1+tb1
  )
  /**
   * root =: {module directory name}
   * raw resources path =: root~src~main~resources
   * jar resources path =: root~target~classes
   * @note The user directory
   * new java.io.File( "." ).getCanonicalPath =: absolute path or System.getProperty("user.dir")
   */
  private val resources = getCanPath("%starget%sclasses".format(resourcesRoot.trim + sep, sep)) + sep
  private val platform = new XmlReader(resources + "settings" + sep + "platform" + _xml)
  /********** ATTRIBUTES - END **********/


  /********** METHODS - START **********/
  protected def resourcesRoot: String

  /**
   * This is the most essential method for the "Admin".
   * It is used to get information from the "platform.xml" file, which is the root of the resources.
   * @param node
   * @return
   */
  def platform(node: String, isPath: Boolean): String =
    if(isPath) resources + platform.read(node).replace(_sep, sep) else platform.read(node)
  def platform(node: String): String = platform(node, true)

 /**
  * This method is used to check the validity of the directory.
  * @param dirPath The directory path
  * @return true only if the file denoted by this path is an unhidden directory.
  */
  def isDirPathValid(dirPath: String) = if(dirPath.nonEmpty){
    val dir = new File(dirPath)
    dir.isDirectory && !dir.isHidden
  }else false

  //self explanatory
  def getCanPath(path: String) = new File(path).getCanonicalPath
  def date(format: String) = new SimpleDateFormat(format).format(new Date)
  def date: String = date("yyyy.MM.dd - HH:mm:ss")
  /********** METHODS - END **********/
}
