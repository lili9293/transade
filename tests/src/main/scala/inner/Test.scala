package inner

import java.awt.Dimension
import swing._
import swing.event._

object Test extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "My Frame"
    contents = new GridPanel(2, 2) {
      hGap = 3
      vGap = 3
      contents += new Button {
        text = "Press Me!"
        reactions += {
          case ButtonClicked(_) => text = "Hello Scala"
        }
      }

      contents += new TextField(50)
    }
    size = new Dimension(300, 80)
  }
}

