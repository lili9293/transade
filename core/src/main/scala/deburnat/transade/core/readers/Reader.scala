package deburnat.transade.core.readers

import deburnat.transade.core.admins.CoreAdmin._

/**
 *
 * @param lineLen
 */
class Reader(lineLen: Int){
  /**
   * This method returns the given text in a structured way.
   * @param text The given text.
   * @param n The length of the tab sequence residing between the left margin and the text.
   * @return A structured text.
   */
  def read(text: String, n: Int): String = {
    val tabs = (0 until n).map(_ => tb1).mkString
    val (seqs, output, rowLen) = (
      text.replace(br, "").replaceAll(" {2,}", " ").split(hash), //First reduce all the gaps to gaps of 1 character length
      new StringBuilder, lineLen - tabs.length
    )

    seqs.foreach(_t => {
      var t = _t.trim
      while(t.length > rowLen){
        val (left, right) = t.splitAt(rowLen)
        val (l, r) = (left.replaceAll(".+ ", ""), right.replaceAll(" .+", ""))

        if(2 * r.length < l.length){ //glue the prefix of the right sequence to the left one
          output.append(tabs + left + r + br) //left seq + prefix of the right seq
          t = right.replaceAll("^%s ".format(r), "") //right seq - its prefix
        }else{ //glue the suffix of the left sequence to the right one
          output.append(tabs + left.replaceAll(l+"$", "") + br) //left seq - it's suffix
          t = l + right //left seq suffix + right seq
        }
      }
      output.append(tabs + t + br)
    })

    tabs + output.mkString.trim
  }

  def read(text: String): String = read(text, 0)
}


protected[transade] object Reader extends Reader(87) //87 is an experimental value based on .pdf settings