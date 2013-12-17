package deburnat.transade.gui.components

import swing.Component
import javax.swing.JPopupMenu

/**
 * An algorithm for data transfer.
 * Project name: deburnat
 * Date: 9/2/13
 * Time: 11:24 PM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[gui] class PopupMenu(component: Component) extends Component{
  override lazy val peer = new JPopupMenu
  peer.add(component.peer)

  def show(invoker: Component){
    peer.show(invoker.peer, - component.preferredSize.width, invoker.preferredSize.height)
  }
}
