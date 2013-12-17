package inner

import deburnat.transade.Mode._
import deburnat.transade.FileLoader

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 8/23/13
 * Time: 9:50 PM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
object ApiLauncher {
  def main(args: Array[String]){
    val xmlLoader = new FileLoader("C:/Users/tap_tap_tap/Desktop/deburnat", "english").xml

    val done = xmlLoader.compute(
      xmlLoader.getTransfers("C:/Users/tap_tap_tap/Desktop/csvSample.xml"),
      BETA_COMPILE
    )

    if(done != null) println("Computation done = " + done.toString.replace(",", ", "))
    else println("NULL")

  }

}
