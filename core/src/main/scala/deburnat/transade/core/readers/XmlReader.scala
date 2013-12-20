package deburnat.transade.core.readers

import deburnat.transade.core.admins.CoreAdmin.textPh
import scala.xml.Elem

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *  
 * Date: 7/25/13
 * Time: 3:34 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 *
 * The class is used to read the texts residing within a given .xml file in a structured way.
 * @param filePath The .xml file path.
 */
protected[transade] class XmlReader(val filePath: String){

  val root: Elem = try{scala.xml.XML.loadFile(filePath)}catch{case e: Exception => null}

  /**
   * This method returns text written in the node.
   * @param label The label of the node to be read.
   * @return The text residing within the node otherwise an empty string.
   */
  def text(label: String): String = try{
    (root \ label)(0).text.trim
  }catch{case e: NullPointerException => ""}
  //TODO before deployment add IndexOutOfBoundsException
  //}catch{case e1: NullPointerException | e2: IndexOutOfBoundsException => ""}
  /* During the development only the NullPointerException should be caught. This has the purpose
   * of improving the code quality.
   * However right before the deployment the IndexOutOfBoundsException should be added to the
   * catch block to avoid the system shutting down unexpectedly.
   * This exception is fired if the label can't be found in the root map.
   */


  /**
   * This method returns text written in the node.
   * @param label see the other text method.
   * @param textPr The text supposed to replace the term "%TEXT%" in the text.
   * @return see the other text method.
   */
  def text(label: String, textPr: String): String = text(label).replace(textPh, textPr)

  /**
   * This method returns the text written in the node in a structured way.
   * @param label see the first text method.
   * @param n see the readers.Reader.read method
   * @param textPr see the second text method
   * @return see the readers.Reader.read method
   */
  def read(label: String, n: Int, textPr: String): String = Reader.read(text(label, textPr), n)

  /**
   * see the first read method
   */
  def read(label: String, textPr: String): String = Reader.read(text(label, textPr), 0)

  /**
   * see the first read method
   */
  def read(label: String, n: Int): String = read(label, n, "")

  /**
   * see the first read method
   */
  def read(label: String): String = read(label, 0, "")
}

