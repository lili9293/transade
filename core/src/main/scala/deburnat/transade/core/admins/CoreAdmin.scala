package deburnat.transade.core.admins

import collection.mutable.{ListBuffer, Map}
import sys.process.{ProcessLogger, Process}

import java.io.{IOException, File}

import deburnat.transade.core.readers.{XmlReader, Reader}
import Reader.read
import FileAdmin._

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 7/25/13
 * Time: 6:08 AM
 */

/**
 * The core administrator object.
 */
protected[transade] object CoreAdmin extends Admin{

  /**
   * This case class is used as a repository by the setLanguage method.
   * @param bug The bug.xml reader.
   * @param transfer The transfer.xml reader.
   * @param view The view.xml reader.
   */
  private case class LangReaders(bug: XmlReader, transfer: XmlReader, view: XmlReader){
    val isSet = bug.root != null && transfer.root != null && view.root != null
  }

  protected val resourcesRoot = "core"

  /********** ATTRIBUTES - START **********/
  private var langRds: LangReaders = null
  private val (
    os, cliProc, scalaBin, langDir, schemaDir, dirPath, docPath, binPath, manualPath
  ) = (
    system("os.name").toLowerCase,
    platform("processor"), platform("scalabin"), platform("languages"), platform(schemas),
    new StringBuilder, new StringBuilder, new StringBuilder, new StringBuilder
  )
  private val schemata = Map(
    xml -> new File(schemaDir+xml).listFiles,
    "xsd" -> new File(schemaDir+"xsd").listFiles,
    "samples" -> new File(schemaDir+"samples").listFiles
  )

  //cli =: command line interpreter aka shell
  protected[admins] val (osIsKnown, cliSuffix, cliKillAcro) = if(os.startsWith("windows")) (
    true, "bat", "taskkill /im AcroRd%s.exe" //cliStart =: "cmd.exe /c start " old code TODO remove
  )else if(os.startsWith("linux") || os.startsWith("unix")) (
    true, "sh", "kill -9 AcroRd%s.exe"
  )else (false, "", "")
  protected[admins] val reportFileName = _platform("reportfilename") //used in PdfCreator & FileAdmin

  val (root, timePh, sc, _sc, imp, _imp, imps, _imps, proc, con, des, df, tabPh, st, _st) = (
    "report", "%TIME%", "scala", ".scala", "import", "_import", "imports", "_imports", "process",
    "connection", "description", "default", "%TAB%", "Storage", "storage"
  )
  //anyDt =: the default data type
  val (leftPh, rightPh, anyDt, boolDt, numDt, strDt, tab2, tab3, tab4, tab5) = (
    "${", "}", "ANY", "BOOLEAN", "NUMBER", "STRING",
    tab1+tab1,
    tab1+tab1+tab1,
    tab1+tab1+tab1+tab1,
    tab1+tab1+tab1+tab1+tab1
  )
  lazy val osBitness: Int = { //this attribute determines the bitness of the OS
    val (arch, _arch) = (
      System.getenv("PROCESSOR_ARCHITECTURE"), System.getenv("PROCESSOR_ARCHITEW6432")
    )
    if(arch.endsWith("64") || (_arch != null && _arch.endsWith("64"))) 64 else 32
  }
  /********** ATTRIBUTES - END **********/


  /********** METHODS - START **********/
  /***** language - start *****/
  /**
   * This method is used to set the language readers.
   * @param lang The language to be used.
   * @return true only if
   *         - the language is recognized by the Admin and
   *         - the bug.xml, transfer.xml and view.xml files are available;
   *         otherwise false.
   */
  private def setLanguage(lang: String): Boolean = {
    var set = false
    val langPath = langDir + (
      if(lang.nonEmpty && isDirPathValid(langDir + lang)){
        set = true
        lang
      }else "english"
    ) + sep
    val xmlLangPath = langPath + "%s" + _xml
    manualPath.clear
    manualPath ++= langPath + manual + _html

    langRds = LangReaders(
      new XmlReader(xmlLangPath.format("bug")),
      new XmlReader(xmlLangPath.format(transfer)),
      new XmlReader(xmlLangPath.format("view"))
    )

    set && langRds.isSet
  }

  //the use of "def" instead of "val" is to ensure the access alt all time to the current readers.
  def bug = langRds.bug
  def trans = langRds.transfer
  def view = langRds.view //required for the output
  /**
   * No break at the beginning or at the end.
   * @param label The given transfer label.
   * @return
   */
  def echo(label: String): String = "println(%s)".format(a+trans.read(label)+a)
  /***** language - end *****/

  /***** directory path - start *****/
  /**
   * This method is uses to set the directory that is used during the whole computation
   * cycle of the tranfers.
   * @param path The directory path.
   *              If it is empty the loader will fetch the required value from the config file.
   * @return A boolean value: true is the path leeds to an unhidden directory
   *         or false otherwise.
   */
  private def setDirPath(path: String): Boolean = if(isDirPathValid(path)){
    val _path = getCanPath(path) //get the canonical path
    dirPath.clear; binPath.clear; docPath.clear //clear the previously set paths

    dirPath.append(_path + sep) //set the folder paths
    binPath.append(_path + sep + "bin")
    docPath.append(_path + sep + "doc")

    emptyDir(_path) && emptyBinDir && emptyDocDir //if necessary empty the main folder and its sub folder.
    /* It is known that unless all sub folders within a folder are empty
     * the File.delete method won't be able to totally empty the given folder.
     * The emptyDir - and emptySubDir implementations work around this fact by simply
     * deleting well defined files. Therefore their order of invocation is irrelevant.
     */
  }else false

  def emptyDocDir = FileAdmin.emptyDocDir(docPath)
  def emptyBinDir = FileAdmin.emptyBinDir(binPath)
  def getDirPath = dirPath.mkString
  def getDocPath = docPath.mkString
  def getDocPath_ = docPath.mkString + sep
  /***** directory path - end *****/

  /***** process - start *****/
  /**
   * This method is used to run the adequate "processor" file.
   * @param jars The jars required to compile the .scala class.
   * @param className The .scala class name (without the ".scala" suffix).
   * @return
   */
  protected[admins] def process(docPath: String, jars: String, className: String)
  : (Boolean, ListBuffer[String], ListBuffer[String]) = {
    val (out, err) = (new ListBuffer[String](), new ListBuffer[String]())
    val processed = try{
      Process("%s %s %s %s %s %s %s".format( //~= Runtime.getRuntime.exec(...) see http://mindprod.com/jgloss/exec.html
        cliProc + cliSuffix,
        //cliStart + cliProc + cliSuffix version before i started handling unix and discovered... TODO remove
        docPath, binPath, jars, className.replaceAll(_sc+"$", ""), scalaBin, cliSuffix
      )) !! ProcessLogger(o => out += o , e => err += e)
      true
    }catch{case e: IOException =>
      err.append(bug.read("compile"))
      false
    }

    (processed, out, err)
  }

  protected[admins] def process(jars: String, className: String)
  : (Boolean, ListBuffer[String], ListBuffer[String]) = process(docPath.mkString, jars, className)

  /**
   * This method is used to allocate the processed cli result in the report object.
   * @param report The "report" object
   * @param out The output results of the cli
   * @param err The errors produced by the cli
   * @param tab The distance from the left margin in the future report file (.xml file).
   */
  protected[admins] def reportProcess(
    report: StringBuilder, out: ListBuffer[String], err: ListBuffer[String], tab: String
  ){
    val (sep, o, e, len) = (" %s ".format(_sep), "output", "error", (tab+tab1).length)

    val _out = out.mkString(sep)
    if(_out.nonEmpty) report.append("%s<%s>%s</%s>".format( //report: <output>...</output>
      tab, o, br+read(_out, len)+br+tab, o
    )) else report.append("%s<%s/>".format(tab, o)) //report: <output/>

    val _err = err.mkString(sep)
    if(_err.nonEmpty) report.append("%s<%s>%s</%s>".format( //report: <error>...</error>
      br+tab, e, br+read(_err, len)+br+tab, e
    )) else report.append("%s<%s/>".format(br+tab, e)) //report: <error/>
  }
  /***** process - end *****/

  /***** others - start *****/
  /**
   * This method is used to get the exception node that will save be placed in the report.
   * @param label The node label
   * @param e The exception thrown
   * @param text The node text
   * @return A string object containing the exception node.
   */
  def getExceptionNode(tab: String, label: String, e:Exception, text: String) = "%s<%s %s=%s>%s%s%s</%s>".format(
    tab, label, "exception", a+e.getClass.getSimpleName+a, br, text+br, tab, label
    /* TEMPLATE
    <label exception={e.getClass.getSimpleName}>
      {text+br}
    </label>
    */
  )
  /***** others - end *****/
  /********** METHODS - END **********/
}


/**
 * This class represents the core administrator.
 * @param dirPath The path of the directory in which all the files are to be saved.
 * @param language The application language.
 * @param output This method object is used to keep the user informed about the computation progress.
 */
protected[transade] final class CoreAdmin(dirPath: String, language: String, val output: String => Unit){
  import CoreAdmin._

  val (goodToGo, view, languages, manualFile) = ( //the language has to come first
    setLanguage(language) && setDirPath(dirPath), CoreAdmin.view,
    new File(langDir).listFiles.map(f => f.getName), new File(manualPath.mkString)
  )

  /**
   * This method is used to download the download (actually copy) the schema directory.
   * @return A string object representing the new ../schema directory's path.
   */
  def downloadSchemas: String = {
    val _dir = getDocPath_ + schemas //the new schemas directory.

    schemata.map{schema =>
      val dir = _dir +sep+schema._1+sep //the new ../schemas/... directory
      schema._2.map{file => copyFile(file.getPath, dir+file.getName).isFile}
    }

    _dir
  }

}
