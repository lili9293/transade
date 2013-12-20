package deburnat.transade.core.storages

import collection.mutable.{ListBuffer, Map}

import java.io.{FileOutputStream, File}
import java.util.jar.{JarEntry, JarFile, JarOutputStream}

import deburnat.transade.core.admins.CoreAdmin
import CoreAdmin._

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 7/25/13
 * Time: 9:52 PM
 */

/**
 * This is simple set of static values.
 */
private object AbsStorage{
  /* storage objects repository =: k1 -> k2 -> counter
   * k1 =: the computation unit id (1 computation unit =: source storage + its target storages)
   * k2 =: target storage simple name <code>this.getClass.getSimpleName</code>
   * counter =: the number of time each specific simple name occurs (starting from 0)
   */
  val repository = Map[Int, Map[String, Int]]()
  var counter = 0

  val (
    src, queryCounter, defs, leftW, rightW,
    aLeftPr, aRightPr, bLeftPr, bRightPr, nLeftPr, nRightPr, sLeftPr, sRightPr,
    stName, smq, tabPR, id
  ) = ('a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k, 'l, 'm, 'n, 'o, 'p, 'q)
  val (tempMap, aLeftPh, aRightPh, bLeftPh, bRightPh, nLeftPh, nRightPh, sLeftPh, sRightPh) = (
    "tempMap",
    anyDt+leftPh, rightPh+anyDt,
    boolDt+leftPh, rightPh+boolDt,
    numDt+leftPh, rightPh+numDt,
    strDt+leftPh, rightPh+strDt
  )
  val _br = "(^%s+|%s+$)".format(br, br)
}

/**
 *
 */
protected[storages] abstract class AbsStorage extends IStorage{
  import AbsStorage._

  /*
   * extAttrs (extender/external) attributes.:
   * A map made of attributes created and used by the extenders of this class. Hence the name.
   * AbsStorage solely improves this attributes names to avoid a collision among the extenders during
   * the parsing stage.
   *
   * intAttrs (internal attributes).:
   * A map made of attributes only used within this class.
   * The choice of the Map object was made to avoid using too much "var" prefixes.
   */
  private val (defCon, _jarFileNames, extAttrs, intAttrs) = (
    "'"+bug.read(con), ListBuffer[String](), Map[String, String](),
    Map[Symbol, Any]( //intern attributes
      src -> null, leftW -> (tempMap + "("+a), rightW -> (a+")"), smq -> new StringBuilder, queryCounter -> -1
    )
  )
  private var dynCounter = -1
  /* ro, i & j are counters.
   * -ro =: round counter
   * -i =: inner counter
   * -j =: additional counter (additional to i or used in additional methods)
   */
  protected final val (d, ro, _i, j, break, _fe) = ("^\\d+$", "round", "i", "j", "break", "firstEntry")


  /********** attributes methods - start **********/
  /**
   *
   * @param kvs kv =: key - value
   */
  protected final def setAttr(kvs: String*){
    kvs.foreach(kv => if(kv.nonEmpty) extAttrs(kv) = kv)
  }

  protected final def getAttr(key: String) = try{
    extAttrs(key)
  }catch{case e: NoSuchElementException => "'" + bug.read("set", key).replaceAll("\\W+", "_")}
  /********** attributes methods - end **********/


  /********** interfaces implemented methods - start **********/
  /**
   * This method is invoked by all extenders during the creation of an object to set the
   * basic attributes.
   * @param prs 1=: tabPr, 2=: aLeftPr, 3=: aRightPr, 4=: bLeftPr, 5=: bRightPr, 6=: nLeftPr, 7=: nRightPr, 8=: sLeftPr, 9=: sRightPr
   */
  protected final def setPRs(prs: (String, String, String, String, String, String, String, String, String)){
    intAttrs(tabPR) = prs._1
    //this represents the tab sequence that source storage will use to properly position
    //the queries resulting from the "getWriteQuery" invoked by the target storages.
    intAttrs(aLeftPr) = prs._2
    intAttrs(aRightPr) = prs._3

    intAttrs(bLeftPr) = prs._4
    intAttrs(bRightPr) = prs._5

    intAttrs(nLeftPr) = prs._6
    intAttrs(nRightPr) = prs._7

    intAttrs(sLeftPr) = prs._8
    intAttrs(sRightPr) = prs._9
  }

  /**
   * The "setDefs"-Method is implemented a lil lower
   * @return
   */
  protected final def getDef(key: String): String = try{
    intAttrs(defs).asInstanceOf[Map[String, String]](key)
  }catch{case e: NoSuchElementException => ""}


  protected final lazy val source = intAttrs(src).asInstanceOf[IStorage]
  private def getCon = intAttrs(queryCounter).asInstanceOf[Int] //there's no setter


  protected final def addSupMethod(SMQ: String){
    val meth = "%s%s".format(
      brr,
      SMQ.replaceAll(_br, "").replaceAll("(public|protected|private)", "private")
    )
    if(!intAttrs(smq).asInstanceOf[StringBuilder].mkString.contains(meth))
      intAttrs(smq).asInstanceOf[StringBuilder].append(meth)
  }

  protected final def getDynCounter = dynCounter
  //######################################## other methods - end ########################################


  //######################################## interfaces implemented methods - start ########################################
  /**
   *
   * @param content target row content
   * @return a String object.
   */
  final def wrapTargetRow(content: String): String =
    if(source.isInstanceOf[IStorage]) intAttrs(leftW) + content + intAttrs(rightW)
  else content

  /**
   * The tab- & line break- sequence are inserted in the TransadeXmlAdmin.parseParse & .parseIf methods.
   * It is impossible at this level to know whether or not the tupel is residing in a "if" node.
   * @param key self explanatory
   * @param value self explanatory
   * @return
   */
  final def setTupel(key: String, value: String): String =
    if(source.isInstanceOf[IStorage]) "%s(%s) = %s".format(tempMap, a+key+a, value) else ""


  final def register = {
    source match{
      case null => //source storage => occurs only once during each computation unit
        repository(counter) = Map[String, Int]()
        dynCounter = counter
        counter += 1

      case _ => //target storage
        val (key1, key2) = (
          source.asInstanceOf[AbsStorage].getDynCounter, this.getClass.getSimpleName
        )
        val i = if(repository(key1).contains(key2)) repository(key1)(key2) else 0

        //Update all attributes of the object in order to avoid collision with admins
        intAttrs(id) = i //the target identifier within the computing round
        extAttrs.foreach(attr => extAttrs(attr._1) = attr._2 + i)

        //register all changes
        repository(key1)(key2) = i + 1
    }
    
    this
  }


  final def register(storage: IStorage) = {
    intAttrs(src) = storage //-> source
    register
  }


  final def setDefs(definitions: Map[String, String]) = {
    intAttrs(defs) = definitions
    this
  }
  /********** interfaces implemented methods - end **********/


  /********** interfaces implemented methods ii - start **********/
  /**
   * This method runs before the XML file get parsed.
   * It's purpose is to take care of tasks that should be done before the XML file(s) is(are) parsed.
   * So far one has the following task:
   * - move the necessary jar-file
   * @see http://javahowto.blogspot.de/2011/07/how-to-programmatically-copy-jar-files.html
   * @return A true value if and only all the necessary tasks have been successfully executed,
   *         otherwise a false value.
   */
  private def beforeParse: Boolean = if(isQueriable){
    jarFileNames.foreach(_fileName => try{
      val fileName = _fileName.replaceAll(jar+"$", "") + jar
      val (srcPath, destPath) = (platform("jars") + fileName, getDocPath_ + fileName)
      _jarFileNames += fileName //this object is used in the compile file

      if(!new File(destPath).exists){ //copy the .jar file only if it doesn't exit yet. Purpose =: speed
        val jar = new JarFile(srcPath)
        val (jarFiles, jos) = (jar.entries, new JarOutputStream(new FileOutputStream(destPath)))

        while(jarFiles.hasMoreElements){
          val file = jarFiles.nextElement
          val is = jar.getInputStream(file)

          //jos.putNextEntry(file)
          //a new file/entry is created to avoid the following exception:
          // ZipException: invalid entry compressed size.
          jos.putNextEntry(new JarEntry(file.getName))
          val b = new Array[Byte](4096) //buffer
          var bRead = is.read(b)
          while(bRead != -1){
            jos.write(b, 0, bRead)
            bRead = is.read(b)
          }
          is.close
          jos.flush //empty the cache to avoid repetitions
          jos.closeEntry //close the zipped file's stream
        }
        jos.close
      }
    }catch{case e: Exception => return false}) //something stream-like exception :)
    true
  }else false


  final def getImpQuery = if(beforeParse){
    intAttrs(queryCounter) = source match{
      case null => 0
      case _ => 1
    }

    val imp = CoreAdmin.imp + " "
    (source match{
      case null => imp + "scala.collection.mutable.Map" + br
      case _ => ""
    }) + imp +
    buildImpQuery.reduceLeft((l, r) =>
      l.trim + br + imp + r.trim.replaceAll("^"+imp, "")
    ).replaceAll("^"+imp, "") + br
  }else ""


  final def getTryQuery: String =(
    if(source == null && intAttrs(queryCounter).asInstanceOf[Int] == 0){
      intAttrs(queryCounter) = 1
      "%stry{%s".format(tb2, br+tb3+echo("con")+br)
    }else tb2 + defCon
  ) + br
  //the line break before this statement is made in the TransadeXmlAdmin.buildClass methode.


  final def getConQuery = (
    if(getCon == 1){
      intAttrs(queryCounter) = 2

      "%s %s%s".format(
        tb3 + "//" + this.getClass.getSimpleName.replaceAll("\\d+", "").replace(st, " "+_st),
        source match{
          case null => "(source storage)"
          case _ => intAttrs(id) + " (target storage)"
        },
        br + buildConQuery.replaceAll(_br, "") + br
      )
    }else tb3 + defCon
  ) + br


  final def getReadQuery = (
    if(getCon == 2 && source == null){
      intAttrs(queryCounter) = 3
      "%s//read query%s%s".format(
        tb3, br,
        buildReadQuery.replaceAll(_br, "") + br
      )
    }else tb3 + defCon
  ) + br


  final def getWriteQuery(cols: String, values: String) = {
    val tabs = source.asInstanceOf[AbsStorage].intAttrs(tabPR).asInstanceOf[String]
    (if(getCon == 2 && source.isInstanceOf[IStorage]){
      intAttrs(queryCounter) = 4
      //since the target is allowed to invoke the "getLoopQuery" method queryCounter is set to 4 instead of 3
      buildWriteQuery(tabs, cols, values).replaceAll(_br, "") + br
    }else tabs + defCon
    ) + br
  }


  final def getLoopQuery(innerLoop: String) = if(getCon == 3 && source == null){
    //the inner loop queries are from the target node.
    val iLoop = innerLoop.replaceAll(_br, "") //eliminate all te line breaks at both ends
      // insert the replacers
      .replace(tabPh, intAttrs(tabPR).asInstanceOf[String])
      .replace(aLeftPh, intAttrs(aLeftPr).asInstanceOf[String])
      .replace(aRightPh, intAttrs(aRightPr).asInstanceOf[String])
      .replace(bLeftPh, intAttrs(bLeftPr).asInstanceOf[String])
      .replace(bRightPh, intAttrs(bRightPr).asInstanceOf[String])
      .replace(nLeftPh, intAttrs(nLeftPr).asInstanceOf[String])
      .replace(nRightPh, intAttrs(nRightPr).asInstanceOf[String])
      .replace(sLeftPh, intAttrs(sLeftPr).asInstanceOf[String])
      .replace(sRightPh, intAttrs(sRightPr).asInstanceOf[String])

    intAttrs(queryCounter) = 4
    //return
    "%s%sval %s = Map[String, Any]()%s%s%s%s%s%s".format(
      tb3 + "//ini" +br,
      tb3, tempMap, br,
      tb3 + echo("loopstart") +brr,
      tb3 + "//the loop itself" +br,
      buildLoopQuery(iLoop).replaceAll(_br, "") +brr,
      tb3+echo("loopend") +brr,
      tb3+"//disconnect" +br
    )
  }else "%s%s%s".format(tb3, defCon, br)


  final def getDisconQuery = (
    if(getCon == 4){
      intAttrs(queryCounter) = source match{
        case null => 5
        case st: IStorage => 6
      }

      buildDisconQuery.replaceAll(_br, "")
    }else tb3 + defCon
  ) + br


  final def getCatchQuery: String = if(source == null && getCon == 5){
    intAttrs(queryCounter) = 6
    val query = "%s%s%s".format(
      tb3+echo("bug")+br,
      tb3+"System.err.println(e.getClass.getSimpleName)"+br,
      tb3+"System.err.println(e.getMessage)"+br
    )

    "%s%s%strue%s%s}catch{case e: Exception => %s%s%sfalse%s%s}".format(
      tb3+echo("discon")+br,
      tb3+echo("success")+br,
      tb3, br, tb2,
      br,
      query+br,
      tb3, br,
      tb2
    )
  }else tb2 + defCon


  final def getSupMethQuery: StringBuilder = if(getCon == 6)
    intAttrs(smq).asInstanceOf[StringBuilder]
  else new StringBuilder

  final def copyJarFileNamesTo(map: Map[String, Int]){
    _jarFileNames.foreach(map += _ -> 1)
  }
  /********** interfaces implemented methods ii - end **********/


  /********** abstract attributes & methods - start **********/
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
  protected val isQueriable: Boolean

  /**
   * This attribute represents the set of .jar file, that are to be move to the parsed class directory,
   * to enable its proper compilation. It is used once.
   * The following tasks are implicitly executed.:
   * - The insertion of the ".jar" suffix at the end of each file name.
   * - This attribute invocation.
   */
  protected val jarFileNames = ListBuffer[String]()

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
  protected def buildImpQuery: ListBuffer[String]

  /**
   * This method returns the query containing all initialisations necessary
   * for the parsed node to connect. It is invoked once.
   * @return A non empty query
   */
  protected def buildConQuery: String

  /**
   * This method returns the query necessary to read the required part of the storage.
   * It is only invoked (once) by the source storage.
   * @return A non empty query.
   */
  protected def buildReadQuery: String

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
  protected def buildWriteQuery(tbn: String, cols: String, values: String): String

  /**
   * This method returns the query used to insert row by row the values of the source
   * storage in the target storage. It is only invoked (once) by the source storage.
   * @param loopBodyPh The placeholder of the sequence provided by parsing all the target nodes
   *                    residing within the source node.
   * @return A non empty query.
  */
  protected def buildLoopQuery(loopBodyPh: String): String

  /**
   * This method returns the query required to disconnect all previously connected
   * attributes/instances. It is invoked once.
   * @return A non empty query.
   */
  protected def buildDisconQuery: String
  /********** abstract attributes & methods - end **********/
}
