package deburnat.transade.core.admins

import io.Source.fromFile
import sys.process.{ProcessLogger, Process}

import java.io.{FileWriter, IOException, File}

import CoreAdmin._

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 7/25/13
 * Time: 4:46 PM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[transade] object FileAdmin {
  /*
  * http://www.java-forums.org/advanced-java/5356-text-image-files-within-jar-files.html
  *   "Class.getREsourceAsStream()"
  * http://stackoverflow.com/questions/8334501/scala-how-to-read-file-in-jar
  * this.getClass.getResourceAsStream("/...")
  * */
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
   *
   * @param srcPath
   * @param destPath
   * @return
   */
  def copyFile(srcPath: String, destPath: String): File = save(destPath, fromFile(srcPath).mkString)


  private lazy val fileName = trans.read("filename")
  /**
   *
   * @return
   */
  protected[admins] def emptyDir(dirPath: String): Boolean = try{  //http://mindprod.com/jgloss/exec.html  check this out
    /* 29.08.2013 - 10:00
     * Actually both .xml and .pdf can't exist at the same time.
     * I damn made sure that it won't happen.
     * But as it always is no code is perfect.
     * About 30min ago i encountered an exception probably cause by the gui that somehow
     * lead to the creation of both format for one computation round.
     * Hence the current code structure.
     */
    val filePath = dirPath + sep + fileName

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


  //in the jars directory only .jar files are allowed
  private lazy val jars = new File(platform("jars", true)).listFiles
    .filter(file => file.getName.endsWith(jar)).map(file => file.getName)

  /**
   * Self explanatory
   * @param doc
   * @return
   */
  protected[admins] def emptyDocDir(doc: StringBuilder): Boolean = if(doc.mkString.trim.nonEmpty){
    val _doc = new File(doc.mkString)
    if(_doc.exists){
      val files = _doc.listFiles
      if(files.nonEmpty) files.map(file => if(jars.contains(file.getName)) true else file.delete)
        //all the files with the exception of .jar files are deleted (purpose =: gain in speed)
        .reduceLeft((r1, r2) => r1 && r2) //r1 && r2 =: if(r1 && r2) true else false
      else true
    }else{
      _doc.mkdir
      true
    }
  }else false

  /**
   *
   * @param bin
   * @return
   */
  protected[admins] def emptyBinDir(bin: StringBuilder): Boolean = if(bin.mkString.trim.nonEmpty){
    val _bin = new File(bin.mkString)
    if(_bin.exists){
      val files = _bin.listFiles
      if(files.nonEmpty) files.map(file => file.delete).reduceLeft((r1, r2) => r1 && r2)
      else true
    }else{
      _bin.mkdir
      true
    }
  }else false

}
