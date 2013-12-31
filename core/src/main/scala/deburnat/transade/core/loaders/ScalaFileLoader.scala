package deburnat.transade.core.loaders

import xml.{Elem, XML}
import XML.loadFile
import collection.mutable.{ListBuffer, Map}
import java.io.File
import deburnat.transade.core.admins.{PdfCreator, CoreAdmin}
import CoreAdmin._
import deburnat.transade.core.conc.Concurrency

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
 * This class is used to load .scala files originating from the interpretation
 * of an .xml file using the transade template.
 * These files are called: (translade).scala files
 * @param admin The application admin responsible for the core.
 */
protected[transade] final class ScalaFileLoader(admin: CoreAdmin){

  /**
   * This method is used to process (translade).scala files.
   * For this method to work at all the admin has to be good to go.
   * @param paths The list of files: scala file -> (imports) .xml file
   * @return see the last compute method
   */
  def compute(paths: Map[String, String]): File = if(admin.goodToGo){
    val _paths = Map[String, Elem]()
    paths.foreach{path => 
      try{
        val (_path, impRoot) = (getCanPath(path._1), loadFile(getCanPath(path._2)))
        if(_path.endsWith(_sc) && impRoot.label.equals(imps)) _paths += _path -> impRoot
      }catch{case e: Exception =>} //org.xml.sax.SAXParseException, FileNotFoundException
      /* simply skip the current path if:
       * - an exception occurs or
       * - the path supposed to be the path of a .scala file isn't one or
       * - the .xml file root's label isn't "imports".
       */
    }

    _compute(_paths, admin.output)
  }else null


  /**
   * This method is used to process (translade).scala files.
   * For this method to work at all the admin has to be good to go.
   * @param scalaFilepaths The list of .scala files.
   *                       The (imports) .xml files must
   *                       - be in the same directory as that of their respective .scala file and
   *                       - have the name of this .scala file as root file name
   *                         and the term "_imports" as suffix.
   *                         E.g.: Test1.scala, Test1_imports.xml
   * @return see the last compute method
   */
  def compute(scalaFilepaths: ListBuffer[String]): File = if(admin.goodToGo){
    val _paths = Map[String, Elem]()
    scalaFilepaths.foreach{path => 
      try{
        val (_path, impRoot) = (getCanPath(path), loadFile(getCanPath(path.replace(_sc, _imps+_xml))))
        if(_path.endsWith(_sc) && impRoot.label.equals(imps)) _paths += _path -> impRoot
      }catch{case e: Exception =>} //org.xml.sax.SAXParseException, FileNotFoundException
      /* simply skip the current path if:
       * - an exception occurs or
       * - the path supposed to be the path of a .scala file isn't one or
       * - the .xml file root's label isn't "imports".
       */

    } 

    _compute(_paths, admin.output)
  }else null


  /**
   * This method is actually the one
   * - processing and executing the .scala file;
   * - saving and returning the report file.
   * For this method to work at all the paths list has to contain at least one path..
   * @param paths The filtered paths object.
   * @param output see the class core.admin.CoreAdmin.
   * @return the report file is everything has been executed flawlessly
   *         otherwise null
   */
  private def _compute(paths: Map[String, Elem], output: String => Unit) = if(paths.nonEmpty){
    val reportFile = PdfCreator.saveReport(
      "<%s>%s</%s>".format(root, br+Concurrency.compute(paths, output)+br, root), //report
      paths.keys.mkString(hash)
    )
    
    output(view.read("outputreport") + ": " + reportFile.getCanonicalPath)
    emptyBinDir //empty the bin directory
    
    reportFile
  }else null
}
