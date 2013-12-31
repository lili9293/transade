package deburnat.transade.gui.center

import swing.Component
import javax.swing.tree.{DefaultTreeCellRenderer, DefaultMutableTreeNode}
import javax.swing.event.{TreeSelectionEvent, TreeSelectionListener}
import javax.swing.JTree

import xml.Node
import collection.mutable.Map
import deburnat.transade.gui.admins.GuiAdmin._

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 9/2/13
 * Time: 4:13 AM
 *
 * This case class is used to create the tree presentable on the GUI from the given [transfer] node.
 * @param transNode The given [transfer] node.
 */
protected[center] case class TransTree(transNode: Node) extends Component{

  /**
   * This method is used to restructure the node strings.
   * @param node The given node.
   * @param tbn the tab sequence to remove.
   * @return The restructured node string.
   */
  private def replace(node: Node, tbn: String) = node.mkString.replaceAll(tbn+"<(/?)", "<$1")

  /**
   * This class is an (modified) extended JTree object enabling
   * the presentation and identification of xml nodes.
   * @note Since the peer's scope is public, this class has to be public as well.
   * @param node_popupMap The tree node and the identifier repository.
   */
  class _TransTree(node_popupMap: (DefaultMutableTreeNode, Map[String, String])) extends JTree(node_popupMap._1)
  with TreeSelectionListener{
    addTreeSelectionListener(this) //listenTo
    val popupMap = node_popupMap._2

    def valueChanged(e: TreeSelectionEvent) { //reactions
      TreeNodePopupMenu(popupMap(e.getPath.toString)).show(TransTree.this)
    }

    //the setCellRenderer method helps setting the icons
    //in this case the default icons (folder- & file cons) are simply being removed
    setCellRenderer(new DefaultTreeCellRenderer{
      setOpenIcon(null)
      setClosedIcon(null)
      setLeafIcon(null)
    })
  }

  override lazy val peer = new _TransTree({
    //the repository of the xml nodes to be presented once the adequate tree node click
    val popupMap = Map[String, String]()

    //the root tree node
    val labTransfer = transfer + " [%s=%s]".format(id, (transNode \_id).text)
    val transferTreeNode = new DefaultMutableTreeNode(labTransfer)
    popupMap("[%s]".format(labTransfer)) = replace(transNode, tb1)

    (transNode \\ source).foreach(sourceNode => {
      //the source tree node
      val labSource = source + " [%s=%s & %s=%s]".format(
        id, (sourceNode \_id).text, format, (sourceNode \_format).text
      )
      val sourceTreeNode = new DefaultMutableTreeNode(labSource)
      popupMap += "[%s, %s]".format(labTransfer, labSource) -> replace(sourceNode, tb2)

      //the source.definitions tree node
      val sourceDefsNode = (sourceNode \\ defs)(0)
      val labSourceDefs = defs + " [%s]".format((sourceDefsNode \_def).map(defNode =>
          (defNode \_key).text +"="+ (defNode \_val).text
      ).mkString(" & "))
      sourceTreeNode.add(new DefaultMutableTreeNode(labSourceDefs, false)) //add the def to the source tree node
      popupMap += "[%s, %s, %s]".format(labTransfer, labSource, labSourceDefs) -> replace(sourceDefsNode, tb3)

      //the target tree nodes
      (sourceNode \\ target).foreach(targetNode => {
        val labTarget = target + " [%s=%s]".format(format, (targetNode \_format).text)
        val targetTreeNode = new DefaultMutableTreeNode(labTarget)
        popupMap("[%s, %s, %s]".format(labTransfer, labSource, labTarget)) = replace(targetNode, tb3)

        //the target.definitions tree node
        val targetDefsNode = (targetNode \\defs)(0)
        val labTargetDefs = defs + " [%s]".format((targetDefsNode \_def).map(defNode =>
          (defNode \_key).text +"="+ (defNode \_val).text
        ).mkString(" & "))
        targetTreeNode.add(new DefaultMutableTreeNode(labTargetDefs, false)) //add the def to the target tree ode
        popupMap("[%s, %s, %s, %s]".format(labTransfer, labSource, labTarget, labTargetDefs)) =
          replace(targetDefsNode, tb4)

        //the parse tree nodes
        (targetNode \\ parse).foreach{parseNode =>
          val row = new StringBuilder((parseNode \_sName).text) //source name as an attribute
          if(row.mkString.isEmpty) row.append((parseNode \ sName).text) //source name as a child node

          val parseLabel = parse + " [%s=%s & %s=%s]".format(tName, (parseNode \_tName).text, sName, "%s")
          val labParse = if(row.mkString.nonEmpty) parseLabel.format(row) else parseLabel.format(view.read("treenodeclick"))

          targetTreeNode.add(new DefaultMutableTreeNode(labParse, false)) //add the parse to the target tree node
          popupMap("[%s, %s, %s, %s]".format(labTransfer, labSource, labTarget, labParse)) =
            replace(parseNode, tb4)
        }

        sourceTreeNode.add(targetTreeNode) //add the target to the source tree node
      })

      transferTreeNode.add(sourceTreeNode) //add the source to the root tree node
    })

    (transferTreeNode, popupMap) //return
  })

}
