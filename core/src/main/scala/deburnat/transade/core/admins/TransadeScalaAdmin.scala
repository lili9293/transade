package deburnat.transade.core.admins

import xml.Elem

import java.io.File

import deburnat.transade.core.readers.Reader.read
import CoreAdmin.{a, _c, imp, sc, _sc, proc, br, tab1, tab2, timePh, bug, date, process, reportProcess}

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 9/30/13
 * Time: 6:00 AM
 */
protected[core] object TransadeScalaAdmin {

  /**
   * This method is used to compile and execute the given .scala file path.
   * @param scalaPath The path of the scala class.
   * @param impRoot The root of the (imports) .xml file.
   * @param output see the class CoreAdmin.
   * @return A string object containing the computation report.
   */
  def run(scalaPath: String, impRoot: Elem, output: String => Unit): String = {
    val scalaFile = new File(scalaPath)
    val scalaFileName = scalaFile.getName.replaceAll(_sc+"$", "")
    val (processed, out, err) = process(
      scalaFile.getParentFile.getCanonicalPath, //docPath = parent path
      (impRoot \\ imp).map{_.text}.mkString(_c), //jars
      scalaFileName
    )
    val (report, start) = (
      new StringBuilder("%s<%s %s=%s %s>%s".format( //report: <process>
        tab1, proc, sc, a+scalaFileName+a, timePh, br+read(scalaPath, 4)+br
      )),
      date
    )

    if(processed) reportProcess(report, out, err, tab2)
    else report.append(bug.read("os", 4)) //unknown operating system

    report.append("%s</%s>".format(br+tab1, proc)) //report: </process>

    report.mkString.replace(timePh, "start=%s end=%s".format(a+start+a, a+date+a))
  }

}
