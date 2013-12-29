package deburnat.transade.core.admins

import io.Source.fromFile
import sys.process.{ProcessLogger, Process}

import java.io.{FileWriter, IOException, File}

import CoreAdmin._

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 7/25/13
 * Time: 4:46 PM
 */
protected[transade] object FileAdmin {
  /* TODO How to read file in jar
   * http://www.java-forums.org/advanced-java/5356-text-image-files-within-jar-files.html
   *   "Class.getResourceAsStream()"
   * http://stackoverflow.com/questions/8334501/scala-how-to-read-file-in-jar
   * this.getClass.getResourceAsStream("/...")
   */

  /*
   * This object represents all the file/directory residing in the {application}/doc directory,
   * that aren't allowed to be deleted.
   * In the inner jars directory only .jar files are allowed. The lazy prefix is optional.
   */
  private lazy val docFlags =
    new File(platform("jars")).listFiles
    .filter(file => file.getName.endsWith(jar)).map(file => file.getName) ++ Array(schemas)

  /**
   * This method is used to create a new file or save data in a new file.
   * @param path The path of the file to be created.
   * @param data The content of the file to be created.
   * @return A file object representing the newly created file otherwise null.
   */
  def save(path: String, data: String): File = {
    val _data = data.trim
    if(path.nonEmpty && _data.nonEmpty) try{
      val file = new File(path)
      //create all necessary folders in case anyone of them is missing
      //if not, delete if possible the old version of the same file.
      val dir = file.getParentFile
      if (!dir.exists) dir.mkdirs else if(file.exists) file.delete

      val writer = new FileWriter(file)
      writer.write(_data)
      writer.close

      file
    }catch {case e: IOException => null} else null
  }

  /**
   * This method is used to duplicate  given file.
   * @param srcPath The path of the file that needs to be copied.
   * @param destPath The destination file path.
   * @return see the save method.
   */
  def copyFile(srcPath: String, destPath: String): File = save(destPath, fromFile(srcPath).mkString)


  /**
   * This method is used to adequately clean the application directory.
   * To be more specific, it deletes all the report.xml and or report.pdf available.
   * @param dirPath The application directory path.
   * @return True if everything goes as planed otherwise false.
   */
  protected[admins] def emptyDir(dirPath: String): Boolean = try{
    val filePath = dirPath + sep + reportFileName

    //first delete the possible existing .xml file
    val file1 = new File(filePath + _xml)
    val r1 = if(file1.exists) file1.delete else true

    //second handle the possible existing .pdf file
    val file2 = new File(filePath + _pdf)
    val r2 = if(file2.exists){
      if(file2.delete) true
      else if(osIsKnown){ //the file is currently open. Is has to be closed first.
        //The process opening pdf files is either AcroRd32.exe or AcroRd64.exe
        Runtime.getRuntime.exec(cliKillAcro.format(32))
        Runtime.getRuntime.exec(cliKillAcro.format(64))
        /* Scala alternative: for once scala is the chatty one :)
         * //simply kill the Adobe process, nothing more
         * Process(cliKillAcro.format(32)) !! ProcessLogger(o => "", e => "")
         * Process(cliKillAcro.format(64)) !! ProcessLogger(o => "", e => "")
         */
        var (i, r2) = (0, file2.delete)
        while(i < 10 && !r2){//print(i +" ") //give the OS enough time to shut down the process
          Thread.sleep(50)
          i += 1
          r2 = file2.delete
        }//the thread will wait at most 0.25s
        r2
      }else false //It won't be possible to delete the file
    }else true

    r1 && r2 //both have to be true to consider it as a success
  }catch{case e: IOException => false} //the file couldn't be deleted

  /**
   * This method is used to adequately clean the the {application}/doc directory.
   * It creates the {application}/doc directory if it doesn't exist.
   * @param doc The doc directory path.
   * @return True if everything goes as planed otherwise false.
   */
  protected[admins] def emptyDocDir(doc: StringBuilder): Boolean = if(doc.mkString.trim.nonEmpty){
    val _doc = new File(doc.mkString)
    if(_doc.exists){
      val files = _doc.listFiles
      if(files.nonEmpty) files.map(file => if(docFlags.contains(file.getName)) true else file.delete)
        //all the files are deleted with the exception of
        // - the .jar files (purpose =: gain in speed)
        // - manuals and schemas (purpose =: avoid repetition)
        .reduceLeft((r1, r2) => r1 && r2) //r1 && r2 =: if(r1 && r2) true else false
      else true
    }else{ //create the {application}/doc directory if it doesn't exist
      _doc.mkdir
      true
    }
  }else false

  /**
   * This method is used to adequately clean the the {application}/bin directory.
   * It creates the {application}/bin directory if it doesn't exist.
   * @param bin The bin directory path.
   * @return True if everything goes as planed otherwise false.
   */
  protected[admins] def emptyBinDir(bin: StringBuilder): Boolean = if(bin.mkString.trim.nonEmpty){
    val _bin = new File(bin.mkString)
    if(_bin.exists){
      val files = _bin.listFiles
      if(files.nonEmpty) files.map(file => file.delete).reduceLeft((r1, r2) => r1 && r2)
      else true
    }else{ //create the {application}/bin directory if it doesn't exist
      _bin.mkdir
      true
    }
  }else false

}
