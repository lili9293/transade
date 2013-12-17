package inner

import sys.process.{ProcessLogger, Process}
import java.lang.management.ManagementFactory

/**
 * An algorithm for data transfer.
 * Project name: deburnat
 * Date: 9/24/13
 * Time: 1:30 PM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
object Test2 {
  val system = sys.props
  def getProp(key: String) = system(key)

  def printVal(keys: String*){
    println(keys.map{key => system(key)}.mkString("\n"))
  }

  def main(args: Array[String]){
    val a = sys.props.keys
    val b = System.getProperty("java.library.path")
    val c = getProp("java.class.path")

    printVal("java.home", "java.library.path", "java.class.path", "sun.java.command")
    val man = ManagementFactory.getRuntimeMXBean
    //println(man.getInputArguments.toArray.mkString(" ") + "\n" + man.getClassPath)
    println(Test2.getClass.getProtectionDomain.getCodeSource.getLocation.toURI)

    //java.library.path
    //user.home

    //Determine if command is recognized / runnable / executable in cmd / bash
    //Determine if command exist in cmd / bash
    //check the existence of command in cmd / bash
  }
}
