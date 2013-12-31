package deburnat.transade.gui.components

import swing.{TextArea, TextField}
import java.awt.Color

import deburnat.transade.gui.admins.GuiAdmin.monoFont

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 9/2/13
 * Time: 4:13 AM
 */

/**
 * This is an extender of the TextField class using the MONOSPACED font.
 * @param _text The default text.
 */
protected[gui] class MonoTextField(_text: String) extends TextField(_text){
  def this() = this("")
  font = monoFont
  protected val (off, on) = (Color.LIGHT_GRAY, Color.BLACK)

  def text_ = text.trim
}

/**
 * This is a not editable extender of the TextArea class using the MONOSPACED font.
 * @param _text The default text.
 */
protected[gui] class MonoNotEditableTextArea(_text: String) extends TextArea(_text){
  editable = false
  font = monoFont
  background = new Color(240, 240, 240) //Panel-like background

  def text_ = text.trim
}