package deburnat.transade.core.readers

import deburnat.transade.core.admins.CoreAdmin.textPh
import scala.xml.Elem

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 7/25/13
 * Time: 3:34 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[transade] class XmlReader(val fileName: String){
  //Exception => IndexOutOfBoundsException | NullPointerException

  val root: Elem = try{scala.xml.XML.loadFile(fileName)}catch{case e: Exception => null}

  /**
   * This method returns text written in the node.
   * A NullPointerException is fired and caught in case the root itself is null.
   * @param label
   * @return
   */
  def text(label: String): String = try{(root \ label)(0).text.trim}catch{case e: NullPointerException => ""}

  /**
   *
   * @param label
   * @param textPr
   * @return
   */
  def text(label: String, textPr: String): String = text(label).replace(textPh, textPr)

  /**
   * sText =: structured text
   * @param label
   * @param n
   * @return
   */
  def read(label: String, n: Int, textPr: String): String = Reader.read(text(label, textPr), n)
  def read(label: String, textPr: String): String = Reader.read(text(label, textPr), 0)
  def read(label: String, n: Int): String = read(label, n, "")
  def read(label: String): String = read(label, 0, "")
}

