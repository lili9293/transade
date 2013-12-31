package deburnat.transade.gui.components

import swing.Component
import javax.swing.JPopupMenu

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
 * This class is an extender of the JPopupMenu class (see JPopupMenu.java specification).
 * This had to be done because until yet scala doesn't provide a PopupMenu class.
 * @param component The component to present/show.
 */
protected[gui] class PopupMenu(component: Component) extends Component{
  override lazy val peer = new JPopupMenu
  peer.add(component.peer)

  /**
   * This method is invoked to show the constructor component.
   * @param invoker The component from where the popup is invoked.
   */
  def show(invoker: Component){
    peer.show(invoker.peer, - component.preferredSize.width, invoker.preferredSize.height)
  }
}
