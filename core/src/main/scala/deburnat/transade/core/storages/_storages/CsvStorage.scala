package deburnat.transade.core.storages._storages

import collection.mutable.ListBuffer
import deburnat.transade.core.admins.CoreAdmin.{tb1, tb3, tb4, tb5, tb6, a, c, cc, br, brr}
import deburnat.transade.core.storages.AbsStorage

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 1/1/14
 * Time: 12:00 AM
 */
protected[_storages] final class CsvStorage extends AbsStorage{
  //http://viralpatel.net/blogs/java-read-write-csv-file/
  //http://opencsv.sourceforge.net/

  private val (_csv, _rows, _row, _keys, map, _co) = (
    "csv", "rows", "row", "keys", "innerMap", "content"
  )

  private lazy val (path, fe, csv, keys, co) = (
    getDef("path"),
    getAttr(_fe), getAttr(_csv), getAttr(_keys), getAttr(_co)
  )


  /********** constructors - start **********/
  setAttr(_csv, _rows, _row, _keys, _fe, _co, _i)

  private val (left, right) = ("%s(%s".format(map, a), a+")")
  private def right(s: String): String = "%s.to%s".format(right, s)
  setPRs(tb5, left, right, left, right("Boolean"), left, right("Double"), left, right)
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
   * getCuId =:
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
  protected lazy val isQueriable: Boolean = path.endsWith(".csv")

  /**
   * This attribute represents the set of .jar file, that are to be move to the parsed class directory,
   * to enable its proper compilation. It is used once.
   * The following tasks are implicitly executed.:
   * - The insertion of the ".jar" suffix at the end of each file name.
   * - This attribute invocation.
   */
  override protected val jarFileNames = ListBuffer("opencsv-2.3")

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
  protected def buildImpQuery: ListBuffer[String] = {
    def csv(s: String) = "au.com.bytecode.opencsv."+s
    def io(s: String) = "java.io."+s
    val list  = "java.util.ArrayList"

    source match{
      case null => ListBuffer( //reader
        "import scala.collection.mutable.{Map, ListBuffer}", csv("CSVReader"), io("FileReader")
      )
      case st: CsvStorage => ListBuffer( //writer
        csv("CSVWriter"), io("{File, FileWriter}"), list
      )
      case _ => ListBuffer( //writer
        csv("{CSVWriter, CSVReader}"), io("{File, FileWriter, FileReader}"), list
      )
    }
  }

  /**
   * This method returns the query containing all initialisations necessary
   * for the parsed node to connect. It is invoked once.
   * @return A non empty query
   */
  protected def buildConQuery: String =
  "%sval %s = new CSV%s".format(tb3 , csv, source match{
    case null => //reader
      val (del, quote) = (getDef("delimiter"), getDef("quote"))
      val others = if(del.length == 1)
        if(quote.length == 1)", '%s', '%s'".format(del, quote) else ", '%s'".format(del)
      else ""
      "Reader(new FileReader(%s)%s)".format(a+path+a, others)

    case _ =>
      "Writer(new FileWriter(%s))%s".format(a+path+a, br) + //writer
      "%sval (%s, _%s) = if(new File(%s).exists)%s".format(tb3, co, fe, a+path+a, br) +
      "%s(new CSVReader(new FileReader(%s)).readAll, false)%s".format(tb4, a+path+a, br) +
      "%selse (new ArrayList[Array[String]](), true)%s".format(tb3, br) +
      "%svar %s = _%s".format(tb3, fe, fe)
  })

  /**
   * This method returns the query necessary to read the required part of the storage.
   * It is only invoked (once) by the source storage.
   * @return A non empty query.
   */
  protected def buildReadQuery: String = "%sval %s = ListBuffer[String]()".format(tb3, keys)

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
  protected def buildWriteQuery(tbn: String, cols: String, values: String): String =
  "%sif(%s){%s%s = false%s%s.add(Array(%s))%s%s}%s".format( //first write the column names
    tbn, fe, br,
    tbn+tb1+fe, br,
    tbn+tb1+co, a+cols.replace(c, "%s%s %s".format(a, c, a))+a, br,
    tbn, br
  ) + "%s%s.add(Array[String](%s))".format( //second continuously write the values
    tbn, co,
    values.replace(cc, ".toString%s ".format(c)) + ".toString"
  )

  /**
   * This method returns the query used to insert row by row the values of the source
   * storage in the target storage. It is only invoked (once) by the source storage.
   * @param loopBodyPh The placeholder of the sequence provided by parsing all the target nodes
   *                    residing within the source node.
   * @return A non empty query.
   */
  protected def buildLoopQuery(loopBodyPh: String): String = {
    val r = getAttr(_row)
    val namesRow = if(getDef("colnamesrow").matches(d)) getDef("colnamesrow") else "1"
    val (start, end) = (if(getDef("start").matches(d)) getDef("start") else namesRow, getDef("end"))

    //round and break initialisation
    val ini = "%svar (%s, %s) = (0, false)%s".format(tb3, ro, break, br)

    val innerStatement = {
      val ini =
        "%s += 1%s%sval %s = %s.readNext%s".format(
          tb4+ro, br,
          tb4, r, csv, brr
        ) +
        //store column names to be able to use them as keys.
        "%sif(%s == %s) %s.foreach(r => %s += r)%s".format(
          tb4, ro, namesRow, r, keys, brr
        ) +
        //first if statement: break out of the big while-loop once the limit reached.
        "%sif(%s == null".format(tb4, r) +
        (if(end.matches(d)) " || %s == %s + 1".format(ro, end) else "") +
        ") %s = true%s".format(break, br)

      val ifStatements = {
        val _foreach = "%s%s += 1%s%s%s(%s(%s)) = r".format(
          tb6, j, br,
          tb6, map, keys, j
        )
        val arToMap =
          "%svar %s = -1%s%sval %s = Map[String, String]()%s%s.foreach{r => %s%s%s}%s".format(
            tb5, j, br,
            tb5, map, br,
            tb5+r, br,
            _foreach,
            br+tb5,
            br
          )

        "%selse if(%s >= %s){%s%s%s}%s".format(
          tb4, ro, start, br,
          arToMap + loopBodyPh + br,
          tb4, br
        )
      }

      ini + ifStatements
    }

    "%s%swhile(!%s){%s%s%s}".format(
      ini,
      tb3, break, br,
      innerStatement,
      br+tb3
    )
  }

  /**
   * This method returns the query required to disconnect all previously connected
   * attributes/instances. It is invoked once.
   * @return A non empty query.
   */
  protected def buildDisconQuery: String = (
    if(source != null) "%s%s.writeAll(%s)%s".format(tb3, csv, co, br) else ""
  ) + "%s%s.close".format(tb3, csv)
  /********** overridden attributes & methods - end **********/
}
