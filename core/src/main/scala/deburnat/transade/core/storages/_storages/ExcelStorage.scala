package deburnat.transade.core.storages._storages

import collection.mutable.ListBuffer
import deburnat.transade.core.admins.CoreAdmin.{a, c, cc, br, brr, tb1, tb2, tb3, tb4, tb5, tb6, tb7}
import deburnat.transade.core.storages.AbsStorage

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 7/26/13
 * Time: 11:56 PM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[storages] final class ExcelStorage extends AbsStorage {
  //http://viralpatel.net/blogs/java-read-write-excel-file-apache-poi/

  private val (_f, _wb, _sh, _rowIt, _cellIt, _cell, map, _keys, _stream) = (
    "file", "workBook", "sheet", "rowIt", "cellIt", "cell", "innerMap", "keys",
    "stream"
  )
  private lazy val (path, loc, i, cellIt, keys, wb, sh, stream, rowIt) = (
    getDef("path"), getDef("location"),
    getAttr(_i), getAttr(_cellIt), getAttr(_keys), getAttr(_wb), getAttr(_sh), getAttr(_stream), getAttr(_rowIt)
  )


  /********** constructors - start **********/
  setAttr(_f, _wb, _sh, _rowIt, _cellIt, _cell, _i, _keys, _stream)

  private val left = "%s(%s".format(map, a)
  private def right(of: String) = "%s).asInstanceOf[%s]".format(a, of)
  setPRs(tb5, left, a+")", left, right("Boolean"), left, right("Double"), left, right("String"))
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
  protected lazy val isQueriable = path.endsWith(".xls")

  /**
   * This attribute represents the set of .jar file, that are to be move to the parsed class directory,
   * to enable its proper compilation. It is used once.
   * The following tasks are implicitly executed.:
   * - The insertion of the ".jar" suffix at the end of each file name.
   * - This attribute invocation.
   */
  override protected val jarFileNames = ListBuffer("poi-3.9")

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
    val (poi1, poi2) = (
      "org.apache.poi.hssf.usermodel.{HSSFWorkbook, HSSFSheet}",
      "org.apache.poi.ss.usermodel.{Row, Cell}"
    )
    def stream(s: String) = "java.io.{File, File%sputStream}".format(s)
    source match{
      case null => ListBuffer("scala.collection.mutable.{Map, ListBuffer}", poi1, poi2, stream("In"))
      case st: ExcelStorage => ListBuffer(stream("Out"))
      case _ => ListBuffer(poi1, poi2, "java.io.{File, FileInputStream, FileOutputStream}")
    }
  }

  /**
   * This method returns the query containing all initialisations necessary
   * for the parsed node to connect. It is invoked once.
   * @return A non empty query
   */
  protected def buildConQuery: String = {
    source match{
      case null => //this object is a source storage
        val (s1, s2) = if(loc.matches(d)) ("getSheetAt", loc) else ("getSheet", a+loc+a)

        "%sval %s = new FileInputStream(new File(%s))%s".format(//first the input stream
          tb3, stream, a+path+a, br
        ) +
        "%sval %s = new HSSFWorkbook(%s).%s(%s)".format(//second the workbook and the sheet
          tb3, sh, stream, s1, s2
        )
      case _ => //this object is a target storage
        val f = getAttr(_f)

        "%sval %s = new File(%s)%s%sval %s = new FileOutputStream(%s)%s".format(//first the output stream
          tb3, f, a+path+a, br,
          tb3, stream, f, br
        ) + //second the workbook and the sheet
        "%sval (%s: HSSFWorkbook, %s: HSSFSheet, _%s: Int) = if(%s.exists)(%s ".format(
          tb3, wb, sh, i, f, br
        ) +
        "%snew HSSFWorkbook(new FileInputStream(%s)),%s%s.getSheet%s,%s%s.getLastRowNum%s".format(
          tb4, f, br,
          tb4+wb, if(loc.matches(d)) "At(%s)".format(loc) else "(%s)".format(a+loc+a), br,
          tb4+sh, br
        ) +
        "%s)else (new HSSFWorkbook(), %s.createSheet%s, 0)%s".format(
          tb3, wb, if(loc.nonEmpty) "(%s)".format(a+loc+a) else "", br
        ) +
        "%svar %s = _%s".format(tb3, i, i)
    }
  }

  /**
   * This method returns the query necessary to read the required part of the storage.
   * It is only invoked (once) by the source storage.
   * @return A non empty query.
   */
  protected def buildReadQuery: String =
  "%sval %s = %s.iterator%s%sval %s = ListBuffer[String]()".format(
    tb3, rowIt, sh, br,
    tb3, keys
  )

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
  protected def buildWriteQuery(tbn: String, cols: String, values: String): String = {
    val smq=
    "%sprivate def write(sheet: HSSFSheet, i: Int, cols: String){%s".format(tb1, br)+
    "%sval row = sheet.createRow(i)%s".format(tb2, br)+
    "%svar j = 0%s".format(tb2, br)+
    "%scols.split(%s,%s).foreach(col => {%s".format(tb2, a, a, br)+
    "%sj += 1%s".format(tb3, br)+
    "%srow.createCell(j).setCellValue(col)%s".format(tb3, br)+
    "%s})%s".format(tb2, br)+
    tb1+"}"
    addSupMethod(smq)

    //first write the column names if the file is new
    "%sif(%s == 0){%s%s += 1%s%swrite(%s, %s, %s)%s%s}%s".format(
      tbn, i, br,
      tbn+tb1+i, br,
      tbn+tb1, sh, i, a+cols+a, br,
      tbn, br
    ) +
    //second continuously write the values
    "%s += 1%swrite(%s, %s, %s)".format(
      tbn+i, br+tbn,
      sh, i, values.replace(cc, "+"+a+c+a+"+")
    )
  }

  /**
   * This method returns the query used to insert row by row the values of the source
   * storage in the target storage. It is only invoked (once) by the source storage.
   * @param loopBodyPh The placeholder of the sequence provided by parsing all the target nodes
   *                    residing within the source node.
   * @return A non empty query.
   */
  protected def buildLoopQuery(loopBodyPh: String): String = {
    val namesRow = if(getDef("colnamesrow").matches(d)) getDef("colnamesrow") else "1"
    val (start, end) = (if(getDef("start").matches(d)) getDef("start") else namesRow, getDef("end"))

    val ini = "%svar %s%s".format(
      tb3, if(end.matches(d)) "(%s, %s) = (0, false)".format(ro, break) else "%s = 0".format(ro), br
    )

    val innerStatement = {
      def ifStatements(s: String) = {
        val cell = getAttr(_cell)
        def _case(s1: String, s2: String) = "%scase %s => %s.get%sCellValue%s".format(
          tb7, s1, cell, s2, br
        )
        val switch =
          "%sval %s = %s.next%s".format(tb6, cell, cellIt, br)+
          "%s%s(%s(%s)) = %s.getCellType match{%s".format(tb6, map, keys, j, cell, br)+
          _case("Cell.CELL_TYPE_BOOLEAN","Boolean")+
          _case("Cell.CELL_TYPE_NUMERIC","Numeric")+
          _case("_","String")+
          "%s}%s".format(tb6, br)
        val itToMap =
          "%svar %s = -1%s%sval %s = Map[String, Any]()%s%swhile(%s.hasNext){%s%s%s += 1%s%s%s}%s".format(
            tb5, j, br,
            tb5, map, br,
            tb5, cellIt, br,
            tb6, j, br,
            switch,
            tb5, brr
          )

        "%sif(%s >= %s){%s%s%s}%s".format(
          tb4+s, ro, start, br,
          itToMap + loopBodyPh + br,
          tb4, br
        )
      }

      //store column names to be able to use them as keys.
      "%sif(%s == %s) while(%s.hasNext) %s += %s.next.getStringCellValue%s".format(
        tb4, ro, namesRow, cellIt, keys, cellIt, brr
      )+( //break out of the big while-loop once the limit reached.
        if(end.matches(d)) "%sif(%s == %s + 1) %s = true%s%s".format(
          tb4, ro, end, break, br,
          ifStatements("else ")
        )
        else ifStatements("")
      )
    }

    "%s%swhile(%s.hasNext%s){%s%s%s += 1%s%sval %s = %s.next.cellIterator%s%s%s}".format(
      ini,
      tb3, rowIt, if(end.matches(d)) " && !%s".format(break) else "", br,
      tb4, ro, br,
      tb4, cellIt, rowIt, brr,
      innerStatement,
      tb3
    )
  }

  /**
   * This method returns the query required to disconnect all previously connected
   * attributes/instances. It is invoked once.
   * @return A non empty query.
   */
  protected def buildDisconQuery: String = source match{
    case null => "%s%s.close".format(tb3, stream)
    case _ => "%s%s.write(%s)%s%s%s.close".format(
      tb3, wb, stream, br,
      tb3, stream
    )
  }
  /********** overwritten attributes & methods - end **********/
}
