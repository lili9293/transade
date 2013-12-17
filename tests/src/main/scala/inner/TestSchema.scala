package inner

object TestSchema {
  val regex = "^([ (]*\\$\\{.+\\}( *(!|=|>|<)=? *.*|[ )]*((&&|\\|\\|)[ (]*)?))+$"
  val conditions = Array(
    "${misaki} == haha",
    "${misaki} == true && (${amakoto} == 89 || ${take} == hihi)",
    "(${real} == koto || ${misaki} == true) && (${amakoto} == 89 || ${take} == hihi)"
  )

  lazy val matches = conditions.foreach{cond => println(cond + " =: " + cond.matches(regex))}

  def main(args: Array[String]){matches}
}
