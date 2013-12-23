package deburnat.transade.core.storages

import collection.mutable.{ListBuffer, Map}

import java.io.{FileOutputStream, File}
import java.util.jar.{JarEntry, JarFile, JarOutputStream}

import deburnat.transade.core.admins.CoreAdmin
import CoreAdmin._
import java.util.NoSuchElementException
import scala.NoSuchElementException

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
  var cuCounter = 0 //the counter of computation units

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
  val _br = "(^%s+|%s+$)".format(br, br) //all the breaks at the beginning or the end
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

  /* Used by the register method.
   * cuId =: computation unit identifier
   * cuStId =: identifier within the computation unit
   * cuStCounter =: storage counter among a computation unit
   */
  private var (cuId, cuStId, cuStCounter) = (-1, -1, 0)

  /* ro, i & j are counters.
   * - ro =: round counter
   * - i =: inner counter
   * - j =: additional counter (additional to i or used in additional methods)
   */
  protected final val (d, ro, _i, j, break, _fe) = ("^\\d+$", "round", "i", "j", "break", "firstEntry")


  /********** attributes methods - start **********/
  /**
   * This method is used to set extender attributes.
   * It should ideally be invoked during the object initialization.
   * @param kvs kv =: key - value
   */
  protected final def setAttr(kvs: String*) = kvs.foreach{kv => if(kv.nonEmpty) extAttrs(kv) = kv}

  /**
   * This method is used to get a value by using its key.
   * Ideally between the setter method (setAttr) and this one (getter method), the register is invoked.
   * Its secondary task is to rearrange the extAtrrs object to avoid a terminus collision with the other
   * storage residing in the same computation unit.
   * @param key The key.
   * @return The adapted value, otherwise a symbol signalizing an error.
   */
  protected final def getAttr(key: String) = try{extAttrs(key)}catch{
    case e: NoSuchElementException => "'" + bug.read("set", key).replaceAll("\\W+", "_")
  }
  /********** attributes methods - end **********/


  /********** interfaces implemented methods - start **********/
  /**
   * This method is invoked by all extenders during the creation of an object to set the
   * basic attributes.
   * @param prs 1=: tabPr,
   *            2=: aLeftPr, 3=: aRightPr,
   *            4=: bLeftPr, 5=: bRightPr,
   *            6=: nLeftPr, 7=: nRightPr,
   *            8=: sLeftPr, 9=: sRightPr
   */
  protected final def setPRs(prs: (String, String, String, String, String, String, String, String, String)){
    //this represents the tab sequence that the source storage will use to properly position
    //the queries resulting from the getWriteQuery method invoked by the target storages.
    intAttrs(tabPR) = prs._1

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
   * This method is used to get a defined value. The setDefs method is implemented a lil lower.
   * @return a string containing the value or an empty string if the key is unknown.
   */
  protected final def getDef(key: String): String = try{
    intAttrs(defs).asInstanceOf[Map[String, String]](key)
  }catch{case e: NoSuchElementException => ""}

  /**
   * This method is invoked to add supplementary methods.
   * @param SMQ The supplementary method's query.
   */
  protected final def addSupMethod(SMQ: String){
    val meth = "%s%s".format(
      brr,
      SMQ.replaceAll(_br, "").replaceAll("(public|protected|private)", "private")
    )
    if(!intAttrs(smq).asInstanceOf[StringBuilder].mkString.contains(meth))
      intAttrs(smq).asInstanceOf[StringBuilder].append(meth)
  }

  //source has to use the "lazy" prefix to give a chance to intAttrs(src) to be set first.
  //see the 2nd register method.
  protected final lazy val source = intAttrs(src).asInstanceOf[IStorage]

  /**
   * This method is used by a storage extender object to get its identifiers.
   * @return The computation unit id and the computation unit storage id.
   */
  protected final def getIds = (cuId, cuStId) //no setter

  private def setCon(i: Int){intAttrs(queryCounter) = i}
  private def getCon = intAttrs(queryCounter).asInstanceOf[Int]
  //######################################## other methods - end ########################################


  //######################################## interfaces implemented methods - start ########################################

  final def wrapTargetRow(content: String): String =
    if(source.isInstanceOf[IStorage]) intAttrs(leftW) + content + intAttrs(rightW)
  else content

  /**
   * The tab- & line break- sequence are inserted in the
   * core.admin.TransadeXmlAdmin.parseParse & .parseIf methods.
   * It is impossible at this level to know whether or not the tupel is residing in a "if" node.
   */
  final def setTupel(key: String, value: String): String =
    if(source.isInstanceOf[IStorage]) "%s(%s) = %s".format(tempMap, a+key+a, value) else ""

  final def setDefs(definitions: Map[String, String]) = {
    intAttrs(defs) = definitions
    this
  }

  final def register = {
    source match{
      case null => //source storage => occurs only once during each computation unit
        repository(cuCounter) = Map[String, Int]()
        cuId = cuCounter  //= 0
        cuStId = cuStCounter //= 0
        cuCounter += 1
        cuStCounter += 1

      case _ => //target storage
        cuId = source.asInstanceOf[AbsStorage].cuId //to avoid retrieving it later again
        val className = this.getClass.getSimpleName //adapted format + "Storage"

        cuStId = source.asInstanceOf[AbsStorage].cuStCounter //id within the computation unit
        source.asInstanceOf[AbsStorage].cuStCounter += 1 //only the source counter is updated

        //not to worry the NoSuchElementException can never be thrown
        val i = if(repository(cuId).contains(className)) repository(cuId)(className) else 0

        //Update all attributes of the object in order to avoid collision with admins
        intAttrs(id) = i //the target identifier within the computing round
        extAttrs.foreach(attr => extAttrs(attr._1) = attr._2 + i)

        //register all changes
        repository(cuId)(className) = i + 1
    }
    
    this
  }

  final def register(storage: IStorage) = {
    intAttrs(src) = storage //-> source
    register
  }
  /********** interfaces implemented methods - end **********/


  /********** interfaces implemented methods ii - start **********/
  /**
   * This method runs before the (transade).xml file get parsed.
   * It's purpose is to take care of tasks that should be done before
   * the (transade).xml file(s) is(are) parsed.
   * So far one has the following task:
   * - move the necessary jar-file
   * @see http://javahowto.blogspot.de/2011/07/how-to-programmatically-copy-jar-files.html
   * @return true if and only all the necessary tasks have been successfully executed,
   *         otherwise false.
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
    setCon(source match{
      case null => 0
      case _ => 1 //the target storage objects will skip the getTryQuery method.
    })

    val imp = CoreAdmin.imp + " "
    (source match{
      case null => imp + "scala.collection.mutable.Map" + br
      case _ => ""
    }) + imp +
    buildImpQuery.reduceLeft((l, r) =>
      l.trim + br + imp + r.trim.replaceAll("^"+imp, "")
    ).replaceAll("^"+imp, "") + br
  }else ""


  //the line break before this statement is made in the core.admin.TransadeXmlAdmin.buildClass method.
  final def getTryQuery: String =(
    if(source == null && getCon == 0){
      setCon(1)
      "%stry{%s".format(tb2, br+tb3+echo("con")+br)
    }else tb2 + defCon
  ) + br


  final def getConQuery = (
    if(getCon == 1){
      setCon(2)
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
      setCon(3)
      "%s//read query%s%s".format(
        tb3, br,
        buildReadQuery.replaceAll(_br, "") + br
      )
    }else tb3 + defCon
  ) + br


  final def getWriteQuery(cols: String, values: String) = {
    val tabs = source.asInstanceOf[AbsStorage].intAttrs(tabPR).asInstanceOf[String]
    (if(getCon == 2 && source.isInstanceOf[IStorage]){
      //since the target is allowed to invoke the getLoopQuery method,
      //queryCounter is set to 4 instead of 3
      setCon(4)
      buildWriteQuery(tabs, cols, values).replaceAll(_br, "") + br
    }else tabs + defCon
    ) + br
  }


  final def getLoopQuery(innerLoop: String) = if(getCon == 3 && source == null){
    //the inner loop queries are from the target node.
    val iLoop = innerLoop.replaceAll(_br, "") //eliminate all the line breaks at both ends
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

    setCon(4)
    "%s%sval %s = Map[String, Any]()%s%s%s%s%s%s".format( //return
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
      setCon(source match{
        case null => 5
        case _ => 6
      })

      buildDisconQuery.replaceAll(_br, "")
    }else tb3 + defCon
  ) + br


  final def getCatchQuery: String = if(source == null && getCon == 5){
    setCon(6)
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
   * #####Definitions - start (what are the definitions used in this storage?)#####
   * -- both
   * ...
   *
   * -- source storage
   * ...
   *
   * -- target storage
   * ...
   * #####Definitions - end#####
   *
   *
   * #####isQueriable - start#####
   * This attribute needs the "lazy" prefix for the same reason as the other default attributes.
   * It determines whether the returned queries will be empty or not.
   * The decision should be made according to the state of the mandatory definitions.
   * A definition is mandatory, if its value is indispensable for the proper compilation of the
   * class to be created by parsing a source node.
   * @note The invocation of this attribute is done implicitly.
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
