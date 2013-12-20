package deburnat.transade.core.storages._storages

import collection.mutable.ListBuffer
import deburnat.transade.core.admins.CoreAdmin.{br, tb3, tb4, a, c, cc}
import deburnat.transade.core.storages.AbsStorage

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 7/26/13
 * Time: 6:19 PM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[storages] final class SqlStorage extends AbsStorage{

  private val (_rs, _co, _qr, _st) = (
    "result", "connect", "query", "statement"
  )
  private lazy val (url, user, pw, table) = (
    getDef("url").replaceAll("^jdbc:mysql://", ""), getDef("username"), getDef("password"), getDef("table")
 )
  private lazy val (rs, co, qr, st) = (
    getAttr(_rs), getAttr(_co), getAttr(_qr), getAttr(_st)
  )

  /********** constructors - start **********/
  setAttr(_rs, _co, _qr, _st)

  private def left(s: String) = "%s.get%s(%s".format(rs, s, a)
  private val right = a+")"
  setPRs(tb4, left("String"), right, left("Boolean"), right, left("Double"), right, left("String"), right)
  //1=: tabPr, 2=: aLeftPr, 3=: aRightPr, 4=: bLeftPr, 5=: bRightPr, 6=: nLeftPr, 7=: nRightPr, 8=: sLeftPr, 9=: sRightPr
  /********** constructors - end **********/


  /********** overridden attributes & methods - start **********/
  /**
   * #####Instructions - start#####
   * --legend
   * The usage of all presented attributes and methods in this comment block is mandatory useless
   * it is stated otherwise.
   * ph = placeholder.
   * pr = place replacer.
   * source storage = a storage created using a "source" node.
   * target storage = a storage created using a "target" node.
   *
   * --default attributes/values
   * If any default attribute (outside of an overriding method or attribute) needs to be set by using
   * the "getDef" method, the "lazy" prefix should be added.
   * This assures that the attribute is set after the "definitions" map setting.
   *
   * --constructor
   * setAttr =:
   *   This method is used to set the attributes the will be present in the parse .scala class/file.
   *   Its purpose is to avoid collisions between two or more storage within the same set.
   * setPRs =:
   *   This method is used to define how the storage should replace the placeholders occuring during
   *   the parsing of the XML source node.
   *   The are 9 different placeholders, ergo up to 9 possible different replacers.
   *
   * --support attributes/methods (optional)
   * d =: The regular expression of a positive integer.
   * ro, i & j are counters.
   *   ro =: round counter
   *   i =: inner counter
   *   j =: additional counter (additional to i or used in additional methods)
   * break =: "break"
   *   An attribute that can be handy while writing the exit-query of the big loop.
   * fe =: "firstEntry"
   *   An attribute that can be handy in case a command should only be proceeded
   *   during the first entry in the big loop.
   * getAttr =:
   *   Method used to retrieve an attribute set with the "setAttr" method.
   * getDef =:
   *   Method used to retrieve values from the "definitions" map.
   * getSource =:
   *   Method used to get the current source storage.
   * addSupMethod =:
   *   Method used to add an additional method to the parsed class.
   * getDynCounter =:
   *   Method used to get the position of the current storage relative to its source storage.
   *
   * --overriding attributes/methods
   * isQueriable =:
   *   The "isQueriable" attribute needs the "lazy" prefix for
   *   the same reason as the other default attributes.
   * checkup =:
   *   The checkup whether an empty/invalid query should be return or not is done implicitly.
   *   Therefore there's no need the include the "isQueriable" attr. in each overriding method.
   * line break =:
   *   There's equally no need to include line breaks at the beginning & end of each returned query.
   *   This is done implicitly.
   * order of invocation =:
   *   The methods below are listed according to the order in which they are invoked.
   *   source storage: beforeParse -> buildImpQuery -> buildConQuery -> buildReadQuery -> buildLoopQuery -> buildDisconQuery
   *   target storage: beforeParse -> buildImpQuery -> buildConQuery -> buildWriteQuery -> buildDisconQuery
   * #####Instructions - end#####
   *
   *
   * #####Definitions - start#####
   * --both
   * ...
   *
   * --source storage
   * ...
   *
   * --target storage
   * ...
   * #####Definitions - end#####
   *
   *
   * #####isQueriable - start#####
   * This attribute determines whether the returned queries will be empty or not.
   * The decision should be made according to the state of the mandatory definitions.
   * A definition is mandatory, if its value is indispensable for the proper compilation of the
   * class to be created by parsing a source node.
   * This attribute is used more than once.
   * #####isQueriable - end#####
   */
  protected lazy val isQueriable = {
    val regWordRegex = "[a-zA-Z_]+\\w*"
    url.nonEmpty && user.nonEmpty && pw.nonEmpty && table.matches(regWordRegex + "\\." + regWordRegex)
  }

  /**
   * This attribute represents the set of .jar file, that are to be move to the parsed class directory,
   * to enable its proper compilation. It is used once.
   * The following tasks are implicitly executed.:
   * - The insertion of the ".jar" suffix at the end of each file name.
   * - This attribute invocation.
   */
  override protected val jarFileNames = ListBuffer("mysql-connector-java-5.1.26")

  /**
   * This method returns a set import queries based on the chosen storage type.
   * Each member of this set represent one simple/combined import statement in the Scala language.
   * Since the "import" prefix is implicitly included to each member of the returned set,
   * there is no need to use it.
   * Here are some set member examples:
   *  - "java.io.File" (simple)
   *  - "scala.collection.mutable.{Map, ListBuffer}" (combined)
   * This method is invoked once.
   * @return A set of non empty queries
   */
  protected def buildImpQuery = ListBuffer("java.sql.DriverManager")
    //"import java.sql.{Connection, DriverManager, ResultSet}"

  /**
   * This method returns the query containing all initialisations necessary
   * for the parsed node to connect. It is invoked once.
   * @return A non empty query
   */
  protected def buildConQuery: String = (
    if(source == null || !source.isInstanceOf[SqlStorage])
      "%sClass.forName(%scom.mysql.jdbc.Driver%s)%s".format(tb3, a, a, br)
    else "") +
    "%sval %s = DriverManager.getConnection(%sjdbc:mysql://%s?user=%s&passowrd=%s%s)%s".format(
      tb3, co, a, url, user, pw, a, br
    ) + "%sval %s = %s.createStatement".format(tb3, st, co
  )

  /**
   * This method returns the query necessary to read the required part of the storage.
   * It is only invoked (once) by the source storage.
   * @return A non empty query.
   */
  protected def buildReadQuery: String = {
    val (rows, cond) = (getDef("columns"), getDef("condition"))
    "%sval %s = %sSELECT ".format(tb3, qr, a) +
    (if(rows.nonEmpty) rows else "*") +
    " FROM %s".format(table) +
    (if(cond.nonEmpty) " WHERE %s".format(cond) else "") +
    "%s%s%sval %s = %s.executeQuery(%s)".format(
      a, br,
      tb3, rs, st, qr
    )
  }

  /**
   * This method returns the query necessary to write the values obtained from the
   * source storage in the target storage. It is only invoked (ore than once) by the target storage.
   * @param tbn A sequence made of a certain amount of tabs.
   *             The amount is set by the source storage.
   * @param cols The names of the columns in which the values should be writen/inserted
   *             Template =: a,b,c,d,...,z
   * @param values The values to insert/write
   *               Template =: ${a},,${b},,${c},,${d},,...${z}
   * @return A non empty query.
   */
  protected def buildWriteQuery(tbn: String, cols: String, values: String) = {
    "%sval %s = %sINSERT INTO %s ".format(tbn, qr, a, table) +
    (if(cols.nonEmpty) "(%s) "format cols else "") +
    "VALUES ('%s + %s + %s')%s%s%s%s.executeUpdate(%s)".format(
      a, values.replace(cc, "+%s'%s'%s+".format(a, c, a)), a, a, br,
      tbn, st, qr
    )
  }

  /**
   * This method returns the query used to insert row by row the values of the source
   * storage in the target storage. It is only invoked (once) by the source storage.
   * @param loopBodyPh The placeholder of the sequence provided by parsing all the target nodes
   *                    residing within the source node.
   * @return A non empty query.
   */
  protected def buildLoopQuery(loopBodyPh: String) = "%swhile(%s.next){%s%s%s%s}".format(
    tb3, rs, br,
    loopBodyPh,
    br, tb3
  )

  /**
   * This method returns the query required to disconnect all previously connected
   * attributes/instances. It is invoked once.
   * @return A non empty query.
   */
  protected def buildDisconQuery = "%s%s.close".format(tb3, co)
  /********** overridden attributes & methods - end **********/
}
