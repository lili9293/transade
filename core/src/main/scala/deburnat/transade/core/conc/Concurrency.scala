package deburnat.transade.core.conc

import xml.{Elem, Node}
import collection.mutable.Map

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 1/1/14
 * Time: 12:00 AM
 */

/**
 * This case object is the concurrency wrapper of the application
 */
protected[transade] case object Concurrency{
  /**
   * This method is used to start a TransadeActor and to harvest its results (report).
   * @param actor The actor to be started.
   * @return A string object containing the full report of the operation.
   */
  private def getReport(actor: TransadeActor): String = {
    actor.start
    actor !? Message.start match{case report: String => report}
  }

  /**
   * This method is used to start a TransadeXmlActor and to harvest its results (report).
   * @param nodes The first generation children of the [transade] node to be computed
   * @param preview This object determines whether or not the parsed classes are to be compiled & run.
   * @param output see the class core.admins.CoreAdmin.
   * @return see the method getReport.
   */
  def compute(nodes: TransadeNodes, preview: Boolean, output: String => Unit): String =
    getReport(new TransadeXmlActor(nodes, preview, output))

  /**
   * This method is used to start a TransadeScalaActor and to harvest its results (report).
   * @param paths see the class core.loaders.ScalaFileLoader.
   * @param output see the class core.admins.CoreAdmin.
   * @return see the method getReport.
   */
  def compute(paths: Map[String, Elem], output: String => Unit): String =
    getReport(new TransadeScalaActor(paths, output))

}


protected[conc] case object Message{
  val start = 'StartComputing //symbol used to start all TransadeActors
}

/**
 * This case class contains a set of the first generation children of a give [transade] node.
 * @param ref The unique [references] node.
 * @param transfers The [transfer] nodes.
 */
protected[core] case class TransadeNodes(ref: Node, transfers: Seq[Node])