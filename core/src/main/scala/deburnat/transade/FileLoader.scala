package deburnat.transade

import deburnat.transade.core.loaders.{XmlFileLoader, ScalaFileLoader}
import deburnat.transade.core.admins.CoreAdmin

/**
 * An algorithm for data transfer.
 * Project name: transade
 * Date: 10/2/13
 * Time: 1:00 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 *
 * This class represents the application main file loader.
 * It is the .xml & .scala file loader wrapper.
 * This constructor is invoked if an admin of type CoreAdmin is already available.
 * It's reserved for the GUI.
 * @param admin The application administrator
 */
final class FileLoader(admin: CoreAdmin) {
  /**
   * This class represents the application main file loader.
   * It is the .xml & .scala file loader wrapper.
   * This constructor is invoked if an admin of type CoreAdmin isn't available yet.
   * @param dirPath The path of the directory in which all the files are to be saved.
   * @param language The application language.
   * @param output This method object is used to keep the user informed about the computation progress.
   */
  def this(dirPath: String, language: String, output: String => Unit) =
    this(new CoreAdmin(dirPath, language, output))

  /**
   * This class represents the application main file loader.
   * It is the .xml & .scala file loader wrapper.
   * This constructor is invoked if an admin of type CoreAdmin isn't available yet and
   * there is no need to inform the user about the computation progress.
   * @param dirPath The path of the directory in which all the files are to be saved.
   * @param language The application language.
   */
  def this(dirPath: String, language: String) = this(dirPath, language, _=>{})

  //xml =: the .xml file loader
  //scala =: the .scala file loader
  val (xml, scala) = (new XmlFileLoader(admin), new ScalaFileLoader(admin))
}
