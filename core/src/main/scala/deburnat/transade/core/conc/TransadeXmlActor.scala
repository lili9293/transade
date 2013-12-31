package deburnat.transade.core.conc

import actors.Actor
import deburnat.transade.core.admins.{CoreAdmin, TransadeXmlAdmin}
import CoreAdmin.{br, tab1, bug}
import deburnat.transade.core.readers.Reader.read

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 10/1/13
 * Time: 1:27 AM
 *
 * This actor concurrently computes the (transade) .xml files.
 * @param nodes 1 =: [transfer] nodes, 2 =: [references] node
 * @param preview see the method core.loaders.XmlFileLoader.compute.
 * @param output see the class transade.FileLoader.
 */
protected[conc] final class TransadeXmlActor(
  nodes: TransadeNodes, preview: Boolean, output: String => Unit
) extends TransadeActor{

  val maxLen = nodes.transfers.length

  /**
   * This actor computes one (transade) .xml file at the time.
   * @param actor The main actor.
   * @param admin The application administrator for the (transade) .xml files.
   * @param i The given [transfer] node index.
   */
  private class XmlActor(actor: TransadeXmlActor, admin: TransadeXmlAdmin, i: Int) extends Actor{
    override def exceptionHandler = {
      case e: Exception => actor ! (nodes.transfers(i).label, e)
    }
    override def act{
      actor ! admin.run(nodes.transfers(i)) //report =: transfer process
    }
  }

  protected def startSubActors{
    val admin = new TransadeXmlAdmin(nodes.ref, preview, output)
    (0 until maxLen).foreach(i => startSubActor(new XmlActor(this, admin, i)))
  }

  protected def getExceptionNode(source: String, e: Exception): String = CoreAdmin.getExceptionNode(
    tab1, source, e,
    bug.read("node", 4)+br+read(e.getMessage, 4)
    /* TEMPLATE: move the comment signs to get the xml object
      <node exception={e.getClass.getSimpleName}> //replace the label (node) by the source's value
      {bug.read("node", 4)+br+read(e.getMessage, 4)}
      </node>
    */
  )



	
}








