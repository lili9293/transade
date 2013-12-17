package deburnat.transade.core.conc

import actors.Actor

import deburnat.transade.core.admins.{CoreAdmin, TransadeXmlAdmin}
import CoreAdmin.{br, tab1, bug}
import deburnat.transade.core.readers.Reader.read

/**
 * @author Patrick Meppe, tapmeppe@gmail.com
 *
 *
 * @param nodes 1 =: transfer node's list, 2 =: references
 */
protected[conc] final class TransadeXmlActor(
  nodes: TransadeNodes, preview: Boolean, output: String => Unit
) extends TransadeActor{

  val maxLen = nodes.transfers.length

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








