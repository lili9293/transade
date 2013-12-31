package deburnat.transade.gui.components

import javax.swing.{Icon, ImageIcon, JOptionPane}
import JOptionPane.{getRootFrame, showOptionDialog, CLOSED_OPTION, YES_OPTION, YES_NO_OPTION, QUESTION_MESSAGE}
import deburnat.transade.MainFrame.top.getCenter
import deburnat.transade.gui.admins.GuiAdmin._

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
 * This object provides 2 types of option panes:
 * - the warning option pane
 * - the confirmation option pane
 */
protected[transade] object TransOptionPane{

  private val (yes: AnyRef, _yes: AnyRef, no, confirm, cTitle, wTitle) = (
    cRead("yes"), wRead("yes"), cRead("no"), br+br+cRead(""), //"" =: confirm+""
    cRead("title"), wRead("title")
  )
  private val (cIcon, wIcon, options, _option) = (
    new ImageIcon(imgPath.format(co)), new ImageIcon(imgPath.format(w)),
    Array[AnyRef](yes, no), Array[AnyRef](_yes)
  )


  /**
   * This method is used to create a dialog in the middle of the CenterPane object.
   * The code is based on the implementation of the original method
   * javax.swing.JOptionPane.showOptionDialog.
   * @param message The message shown in the dialog window.
   * @param messageType self explanatory
   * @param optionType self explanatory
   * @param icon The icon shown in the dialog window.
   * @param options self explanatory
   * @param iniVal The initial value
   * @param title The title of the dialog.
   * @return
   */
  private def _showOptionDialog(
    message: AnyRef, title: String, optionType: Int, messageType: Int, icon: Icon,
    options: Array[AnyRef], iniVal: AnyRef
  ): Int = {
    val pane = new JOptionPane(message, messageType, optionType, icon, options, iniVal)
    pane.setInitialValue(iniVal)
    val parent = getRootFrame
    pane.setComponentOrientation(parent.getComponentOrientation)
    val dialog = pane.createDialog(parent, title)
    pane.selectInitialValue

    /* The purpose is to set all the warning- & confirm- pop-ups in the middle of the CenterPane.
     * Since it is the main area of the gui.
     */
    val (loc, size) = getCenter
    dialog.setLocation(
      loc.x + (size.width - pane.getWidth) / 2, loc.y + (size.height - pane.getHeight) / 2
    )
    dialog.setVisible(true)

    /***** RETURN *****/
    val selVal = pane.getValue
    if(selVal == null) CLOSED_OPTION
    (0 until options.length).foreach(i => if(options(i) == selVal) return i)
    CLOSED_OPTION
  }


  def __confirm(msg: String): Boolean = showOptionDialog(
    getRootFrame, msg + confirm, cTitle, YES_NO_OPTION, QUESTION_MESSAGE, cIcon, options, yes
  ) == YES_OPTION

  def _confirm(msg: String) = _showOptionDialog(
    msg + confirm, cTitle, YES_NO_OPTION, QUESTION_MESSAGE, cIcon, options, yes
  ) == YES_OPTION

  def confirm(label: String): Boolean = _confirm(cRead(label))

  def __warn(msg: String):Boolean = showOptionDialog(
    getRootFrame, msg, wTitle, YES_OPTION, QUESTION_MESSAGE, wIcon, _option, _yes
  ) == YES_OPTION

  def _warn(msg: String):Boolean = _showOptionDialog(
    msg, wTitle, YES_OPTION, QUESTION_MESSAGE, wIcon, _option, _yes
  ) == YES_OPTION

  def warn(label: String):Boolean = _warn(wRead(label))
}

