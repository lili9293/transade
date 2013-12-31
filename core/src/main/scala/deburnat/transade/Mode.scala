package deburnat.transade

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
 * This enumerator object is used to set the mode of the XmlFileLoader object.
 */
object Mode extends Enumeration{
  /* All the values of this enum have to be used with the "lazy" prefix because
   * the "view" has to be initialised first.
   */
  type Mode = Value

  //the texts (labels) below are used in the gui
  private val (prev, beta, user) = ("modepreview", "modebetacompile", "modeusercompile")
  lazy val list = List(prev, beta, user) //used in the gui

  //for users with an average understanding of the Scala and Java programming languages
  lazy val PREVIEW = Value(prev)

  /* For users with an average understanding of the Scala and Java programming languages
   * and the command line interpreter language of the given operating system.
   */
  lazy val BETA_COMPILE = Value(beta)

  //for all sort of users.
  lazy val USER_COMPILE = Value(user)


  /**
   * This method is used to convert a String into it's corresponding Mode.
   * @param str The value to convert.
   * @return The adequate Mode otherwise Mode.PREVIEW
   */
  def toMode(str: String): Mode =
    if(str == beta) BETA_COMPILE else if(str == user) USER_COMPILE else PREVIEW
}

