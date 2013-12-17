package deburnat.transade.core.storages

import collection.mutable.Map

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 7/26/13
 * Time: 12:39 AM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[core] trait IStorage {
  /********** parse related methods - start **********/
  /**
   * This method wraps the content of the "target row"-attribute (the key) in the "parse"-node.
   * @param key: target row's content (attribute's content).
   * @return a string made of the wrapped key or
   *         the key itself if the storage isn't a target storage.
   */
  def wrapTargetRow(key: String): String

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
   * @param definitions The map to be set
   * @return Itself
   */
  def setDefs(definitions: Map[String, String]): IStorage

  /**
   * The method registers the storage it was invoked by.
   * Making it possible for each IStorage object to use unique attributes among its set of
   * IStorages.
   * It should only be used by a storage created using a "source" node.
   * @return itself
   */
  def register: IStorage

  /**
   * This method registers and links the given target storage to its storage storage
   * It should only be used by a storage created using a "target" node.
   * @param source The storage created while parsing the "source" node.
   * @return Itself
   */
  def register(source: IStorage): IStorage
  /********** parse related methods - end **********/


  /********** query related methods - start **********/
  /**
   * This method returns the import query based on the chosen storage type.
   * It is the first query related method to be invoked.
   * @return A string.
   */
  def getImpQuery: String

  /**
   * This method returns the initiation query of the "try"-block.
   * It can only be used by a storage created using a "source" node.
   * No line break at the beginning, 2 line breaks at the end.
   * It is the second query related method to be invoked.
   * @note The line break before this statement is made in the TransadeXmlAdmin.buildClass methode.
   * @return A string.
   */
  def getTryQuery: String

  /**
   * This method returns the query containing all initialisations necessary
   * for the parsed node to connect.
   * No line break at the beginning, 2 line breaks at the end.
   * It should be invoked after the "getTryQuery" method.
   * @return A string.
   */
  def getConQuery: String

  /**
   * This method returns the query necessary to read the required part of the storage.
   * It can only be used by a storage created using a "source" node.
   * No line break at the beginning, 2 line breaks at the end.
   * It should be invoked after the "getConQuery" method.
   * @return A string.
   */
  def getReadQuery: String

  /**
   * This method returns the query necessary to write the values obtained from the
   * source storage in the target storage.
   * No line break at the beginning, 2 line breaks at the end.
   * It can only be used by a storage created using a "target" node.
   * It should be invoked after the "getConQuery" method.
   * @param cols column names within the storage.
   * @param values row wise column values.
   * @return A string.
   */
  def getWriteQuery(cols: String, values: String): String

  /**
   * This method returns the query used to insert row by row the values of the source
   * storage in the target storage.
   * No line break at the beginning, one comment and a line break at the end.
   * It should be invoked after the "getReadQuery" method.
   * It can only be used by a storage created using a "source" node.
   * @param innerLoop A string provided by parsing all the target nodes residing within
   *                  the source node.
   * @return A string.
   */
  def getLoopQuery(innerLoop: String): String

  /**
   * This method returns the query required to disconnect all previously connected
   * attributes/instances.
   * No line break at the beginning, 2 line breaks at the end.
   * It should be invoked after the "getWriteQuery" / "getLoopQuery" method
   * @return A string.
   */
  def getDisconQuery: String

  /**
   * This method returns the query closing the previously opened "try" statement query
   * (see "getTryQuery").
   * No line break at the beginning or at the end.
   * The line break after this statement is made in the TransadeXmlAdmin.buildClass method.
   * It can only be used by a storage created using a "source" node.
   * @return a string value
   */
  def getCatchQuery: String

  /**
   * This method returns the query of any potential additional methods.
   * 2 line breaks at the beginning, no line break at the end.
   * It is the last query related method to be invoked.
   * @return
   */
  def getSupMethQuery: StringBuilder

  def copyJarFileNamesTo(map: Map[String, Int])
  /********** query related methods - end **********/
}
