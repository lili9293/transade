package deburnat.transade.core.storages

import collection.mutable.Map

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
 * This trait is the abstract form of a storage object.
 */
protected[core] trait IStorage {
  /********** parse related methods - start **********/
  /**
   * This method wraps the content of the [parse targetrow=""] attribute (the key).
   * @param content The target row's content (attribute content).
   * @return A string made of the wrapped key or
   *         the key itself if the storage isn't a target storage.
   */
  def wrapTargetRow(content: String): String

  /**
   * This method returns a query using the given key to save the given value in the loop map.
   * For security reasons this map is hidden.
   * @param key self explanatory
   * @param value self explanatory
   * @return The parsed string or an empty string if the storage isn't a target storage.
   */
  def setTupel(key: String, value: String): String

  /**
   * This method sets the "definitions" attribute.
   * @param definitions The map to be set: key -> value
   * @return The storage object itself.
   */
  def setDefs(definitions: Map[String, String]): IStorage

  /**
   * This method registers the storage it was invoked by.
   * Making it possible for each storage object to use unique attributes among its set of
   * storages.
   * @note It should only be used by a storage created using a [source] node.
   * @return The storage object itself.
   */
  def register: IStorage

  /**
   * This method registers and links the given target storage to its storage storage
   * @note It should only be used by a storage created using a [target] node.
   * @param source The storage created while parsing the [source] node.
   * @return The storage object itself.
   */
  def register(source: IStorage): IStorage
  /********** parse related methods - end **********/


  /********** query related methods - start **********/
  /**
   * This method returns the import query based on the chosen storage type.
   * It has to be the first query related method to be invoked.
   * @return A string.
   */
  def getImpQuery: String

  /**
   * This method returns the initiation query of the "try"-block.
   * It can only be used by a storage created using a [source] node.
   * No line break at the beginning, 2 line breaks at the end.
   * It has to be the second query related method to be invoked.
   * @note The line break before this statement is made in the method
   *       core.admin.TransadeXmlAdmin.buildClass.
   * @return A string.
   */
  def getTryQuery: String

  /**
   * This method returns the query containing all initialisations necessary
   * for the parsed node to connect.
   * No line break at the beginning, 2 line breaks at the end.
   * It should be invoked after the getTryQuery method.
   * @return A string.
   */
  def getConQuery: String

  /**
   * This method returns the query necessary to read the required part of the storage.
   * It can only be used by a storage created using a [source] node.
   * No line break at the beginning, 2 line breaks at the end.
   * It should be invoked after the getConQuery method.
   * @return A string.
   */
  def getReadQuery: String

  /**
   * This method returns the query necessary to write the values obtained from the
   * source storage in the target storage.
   * No line break at the beginning, 2 line breaks at the end.
   * It can only be used by a storage created using a [target] node.
   * It should be invoked after the getConQuery method.
   * @param cols The column names within the storage.
   * @param values The row-wise column values.
   * @return A string.
   */
  def getWriteQuery(cols: String, values: String): String

  /**
   * This method returns the query used to insert row by row the values of the source
   * storage in the target storage.
   * No line break at the beginning, one comment and a line break at the end.
   * It should be invoked after the getReadQuery method.
   * It can only be used by a storage created using a [source] node.
   * @param innerLoop A string provided by parsing all the target nodes residing within
   *                  the source node.
   * @return A string.
   */
  def getLoopQuery(innerLoop: String): String

  /**
   * This method returns the query required to disconnect all previously connected
   * attributes/instances.
   * No line break at the beginning, 2 line breaks at the end.
   * It should be invoked after the getWriteQuery / getLoopQuery method.
   * @return A string.
   */
  def getDisconQuery: String

  /**
   * This method returns the query closing the previously opened "try" statement query
   * (see the method getTryQuery).
   * No line break at the beginning or at the end.
   * The line break after this statement is made in the method
   * core.admin.TransadeXmlAdmin.buildClass.
   * It can only be used by a storage created using a [source] node.
   * @return A string value.
   */
  def getCatchQuery: String

  /**
   * This method returns the query of any potential additional methods.
   * 2 line breaks at the beginning, no line break at the end.
   * It is the last query related method to be invoked.
   * @return A string.
   */
  def getSupMethQuery: StringBuilder

  /**
   * This method is used to to copy all the .jar file names from each storage object
   * local .jar file names repository (ListBuffer object) to the global one (Map object).
   * @param map The global .jar file names repository.
   *            The choice of the Map over stuff as List or Array was to simplify the process
   *            ensuring the uniqueness of all the .jar file names.
   *            In the map the key is actually the .jar file name, whereas the value corresponding
   *            to the keep is fully irrelevant. Hence a default value of 1 for all.
   */
  def copyJarFileNamesTo(map: Map[String, Int])
  /********** query related methods - end **********/
}
