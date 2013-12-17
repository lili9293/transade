package deburnat.transade.gui.center

import scala.xml.Node
import scala.collection.mutable.Map
import scala.swing.Component

import javax.swing.tree.{DefaultTreeCellRenderer, DefaultMutableTreeNode}
import javax.swing.event.{TreeSelectionEvent, TreeSelectionListener}
import javax.swing.JTree

import deburnat.transade.gui.admins.GuiAdmin._

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 8/29/13
 * Time: 11:17 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[center] case class TransTree(transNode: Node) extends Component{

  private def replace(node: Node, tbn: String) = node.mkString.replaceAll(tbn+"<(/?)", "<$1")

  class _DTree(node_popupMap: (DefaultMutableTreeNode, Map[String, String])) extends JTree(node_popupMap._1)
  with TreeSelectionListener{
    addTreeSelectionListener(this)
    val popupMap = node_popupMap._2

    def valueChanged(e: TreeSelectionEvent) {
      TreeNodePopupMenu(popupMap(e.getPath.toString)).show(TransTree.this)
    }
  }


  override lazy val peer = new _DTree({
    val popupMap = Map[String, String]()
    /**
     * At this level it is imp
     */
    //the root node
    val labTransfer = transfer + " [%s=%s]".format(id, (transNode \_id).text)
    val transferTreeNode = new DefaultMutableTreeNode(labTransfer)
    popupMap("[%s]".format(labTransfer)) = replace(transNode, tb1)

    (transNode \\ source).foreach(sourceNode => {
      //the source node
      val labSource = source + " [%s=%s & %s=%s]".format(
        id, (sourceNode \_id).text, format, (sourceNode \_format).text
      )
      val sourceTreeNode = new DefaultMutableTreeNode(labSource)
      popupMap("[%s, %s]".format(labTransfer, labSource)) = replace(sourceNode, tb2)

      //the source.definitions node
      val sourceDefsNode = (sourceNode \\ defs)(0)
      val labSourceDefs = defs + " [%s]".format((sourceDefsNode \_def).map(defNode =>
          (defNode \_key).text +"="+ (defNode \_val).text
      ).mkString(" & "))
      sourceTreeNode.add(new DefaultMutableTreeNode(labSourceDefs, false))
      popupMap("[%s, %s, %s]".format(labTransfer, labSource, labSourceDefs)) = replace(sourceDefsNode, tb3)

      //the target nodes
      (sourceNode \\ target).foreach(targetNode => {
        val labTarget = target + " [%s=%s]".format(format, (targetNode \_format).text)
        val targetTreeNode = new DefaultMutableTreeNode(labTarget)
        popupMap("[%s, %s, %s]".format(labTransfer, labSource, labTarget)) = replace(targetNode, tb3)

        //the target.definitions node
        val targetDefsNode = (targetNode \\defs)(0)
        val labTargetDefs = defs + " [%s]".format((targetDefsNode \_def).map(defNode =>
            (defNode \_key).text +"="+ (defNode \_val).text
        ).mkString(" & "))
        targetTreeNode.add(new DefaultMutableTreeNode(labTargetDefs, false))
        popupMap("[%s, %s, %s, %s]".format(labTransfer, labSource, labTarget, labTargetDefs)) =
          replace(targetDefsNode, tb4)

        //the parse nodes
        (targetNode \\ parse).foreach(parseNode => {
          val row = new StringBuilder((parseNode \_sName).text) //source name as an attribute
          if(row.mkString.isEmpty) row.append((parseNode \ sName).text) //source name as a child node

          val parseLabel = parse + " [%s=%s & %s=%s]".format(tName, (parseNode \_tName).text, sName, "%s")
          val labParse = if(row.mkString.nonEmpty) parseLabel.format(row) else parseLabel.format(view.read("treenodeclick"))

          targetTreeNode.add(new DefaultMutableTreeNode(labParse, false))
          popupMap("[%s, %s, %s, %s]".format(labTransfer, labSource, labTarget, labParse)) =
            replace(parseNode, tb4)
        })

        sourceTreeNode.add(targetTreeNode)
      })

      transferTreeNode.add(sourceTreeNode) //add the source to the root node
    })

    (transferTreeNode, popupMap)
  }){
    setCellRenderer(new DefaultTreeCellRenderer{
      setOpenIcon(null)
      setClosedIcon(null)
      setLeafIcon(null)
    })
  }

}
