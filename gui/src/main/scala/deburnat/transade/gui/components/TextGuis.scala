package deburnat.transade.gui.components

import swing.{TextArea, TextField}

import java.awt.Color

import deburnat.transade.gui.admins.GuiAdmin.monoFont

/**
 * An algorithm for data transfer.
 * Project name: deburnat
 * Date: 9/4/13
 * Time: 8:19 PM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[gui] class MonoTextField(_text: String) extends TextField(_text){
  def this() = this("")
  font = monoFont

  protected val (off, on) = (Color.LIGHT_GRAY, Color.BLACK)
}


protected[gui] class MonoNotEditableTextArea(_text: String) extends TextArea(_text){
  editable = false
  font = monoFont
  background = new Color(240, 240, 240)
}