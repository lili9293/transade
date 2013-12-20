package deburnat.transade.core.admins

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import deburnat.transade.core.readers.XmlReader

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
 * This trait is the abstract form of the application main admins.
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
   *        new java.io.File( "." ).getCanonicalPath =: absolute path or
   *        System.getProperty("user.dir")
   */
  protected val resourcesRoot: String
  //resources as to use the "lazy" prefix to avoid an initialisation error
  private lazy val resources = getCanPath("%starget%sclasses".format(resourcesRoot.trim + sep, sep)) + sep
  private lazy val platform = new XmlReader(resources + "settings" + sep + "platform" + _xml)
  /********** ATTRIBUTES - END **********/


  /********** METHODS - START **********/
  /**
   * This is the most essential method for the Admin.
   * It is used to get information from the platform.xml file, which is the root of the resources.
   * @param node The node (actually the node's label) whose text/information is required.
   * @return A string object containing the node's (adapted) information.
   */
  def _platform(node: String): String = platform.read(node)
  def platform(node: String): String = resources + _platform(node).replace(_sep, sep)

 /**
  * This method is used to check the validity of a given directory.
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
