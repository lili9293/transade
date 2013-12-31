package deburnat.transade.core.conc

import actors.Actor
import collection.mutable.ListBuffer
import deburnat.transade.core.admins.CoreAdmin.br

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
 * This actor concurrently computes the [transfer] nodes or the .scala files.
 */
protected[conc] abstract class TransadeActor extends Actor{

  private val report = new ListBuffer[String]()
  protected final val len = 4 //used during the creation of the exception node

  protected val maxLen: Int //the limit of the act.loopWhile
  protected def startSubActors: Unit //this method is used to start all the sub actors
  /**
   *
   * @param source
   * @param e
   * @return
   */
  protected def getExceptionNode(source: String, e: Exception): String

  /**
   * This method is used to start a newly created actor.
   * @param actor The actor to be started.
   */
  protected final def startSubActor(actor: Actor){
    link(actor) //link the sub actor to its main actor
    actor.start
  }


  override def act{react{case Message.start =>
    val to = sender
    startSubActors

    /* The append of the report object could actually be done be the sub actor
     * returning the report or firing the exception.
     * The problem is however while being faster and relieving for this main actor,
     * it would increase the probability of data racing.
     * 2 sub actors appending the report object at the same time can only end bad.
     */
    loopWhile(report.length < maxLen){react{
      case report: String => this.report += report //allocate the processed reports
      case (source: String, e: Exception) => report += getExceptionNode(source, e)
    }}andThen to ! report.mkString(br) //return
  }}

}
