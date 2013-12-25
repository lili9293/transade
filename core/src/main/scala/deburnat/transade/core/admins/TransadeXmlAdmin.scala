package deburnat.transade.core.admins

import xml.{Node, Elem}
import collection.mutable.{Map, ListBuffer}

import FileAdmin.save
import CoreAdmin._
import deburnat.transade.core.readers.{XmlReader, Reader}
import deburnat.transade.core.storages.{IStorage, Storage}

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 7/25/13
 * Time: 6:32 PM
 */

/**
 * The object administrator of the .xml file using the transade template.
 */
private object TransadeXmlAdmin {
  /********** attributes - start **********/
  val (pName, admin, _if, valPh, phRegex) = (
    "projectname", "admin", "if", "%VALUE%", "\\$\\{[a-zA-Z_]+\\w*\\}"
  )

  object Rs extends Enumeration{ //enums: used to determine how good a parsing have been done.
    type rs = Value
    val GOOD = Value(1)
    val POORLY = Value(2)
    val BAD = Value(3)
  }
  /********** attributes - end **********/


  /********** methods (class related) - start **********/
  def printD(a: Any){println(a); sys.exit(0)} //debug: to delete once i will be done

  /**
   * This method is used to create the scala class (file) and execute it.
   * @param classAttr =: [source id=""] attribute, body, authors, import, support methods
   * @param preview The boolean parameter helping to determine whether or not the class to be created
   *                should be compiled and run
   * @param jarFileNames The name list of all .jar files required to compile and run
   *                     the class to be created.
   *                     The data type "Map" was chosen to easily ensure the uniqueness of each file name.
   *                     The Map format is String -> Int. As one can image each String will be unique,
   *                     whereas the Int value is irrelevant for this method.
   * @param report The current report object (StringBuilder).
   * @return true if the created class was able to be processed otherwise false.
   */
  def process(
    classAttr:(String, String, String, String, String),
    preview: Boolean, jarFileNames: Map[String, Int], report: StringBuilder
  ): Boolean = {
    /**
     * @param failure The failure text to report
     * @return false
     */
    def failureReport(failure: String) = {
      report.append("%s=%s>%s%s</%s>".format(sc, a+a, br+failure+br, tab3, proc)) //report: <process/>
      false
    }

    report.append("%s<%s ".format(br+tab3, proc)) //report: <process>
    //printD(classAttr._4) //TODO delete this

    if(classAttr._1.nonEmpty && classAttr._2.nonEmpty){ //id & body are the most important parameter.
      val className = "%s_%s".format(classAttr._1.replaceAll("\\W+", ""), date("yyyyMMddHHmmss"))
      val filePath = "%s.%s".format(getDocPath_ + className, sc)

      //save the data
      if(save(filePath, buildClass(classAttr._3, classAttr._4, classAttr._1, className, classAttr._2, classAttr._5))
        .isFile){
        val _con = bug.read(con) //the failure to connect - text
        if(!classAttr._4.contains(_con)){
          /* If for any reason a part of the body can't be built properly, the interpreter will not compile.
           * However the .scala class file can be used for debugging purpose, provided it isn't deleted
           * at the end of the computation.
           */
          report.append("%s=%s>%s".format(sc, a+className+a, br)) //report: <process>

          //use the command line interpreter available to run and compile the newly created scala class
          if(preview){
            report.append(trans.read("preview", 8)+br+tab3+"</%s>".format(proc)) //report: </process>
            true
          }else{
            val data = new StringBuilder //import data
            val jars = if(jarFileNames.isEmpty){ //no jar required to compile the parsed class
              data ++= "<%s %s=%s>%s<%s>%s</%s>%s</%s>".format( //<imports><import>.jar</import></imports>
                imps, sc, a+className+a, br+tb1, imp, jar, imp, br, imps
              )

              jar //jars = .jar
            }else{ //build the jar argument to compile the parsed class
              val jars = jarFileNames.keys
              data ++= "<%s %s=%s>".format(imps, sc, a+className+a) + //<imports>
                jars.map{"%s<%s>%s</%s>".format(br+tb1, imp, _, imp)}.mkString + //<import>jar</import>
                br + "</%s>".format(imps) //</imports>

              jars.mkString(_c) //jars
            }

            save(getDocPath_ + className + _imps + _xml, data.mkString) //save the import file
            //the required jars are additionally saved to enable the direct
            //compilation and the execution their corresponding .scala class

            val (processed, out, err) = CoreAdmin.process(jars, className)
            if(processed){
              reportProcess(report, out, err, tab4)
              report.append("%s</%s>".format(br+tab3, proc)) //report: <process/>
            }else report.append(bug.read("os", 8)+br+tab3+"</%s>".format(proc)) //report: unknown operating system <process/>

            processed
          }
        }else failureReport(Reader.read(_con, 8))
      }else failureReport(bug.read("savenot", 8, sc))
    }else failureReport(bug.read("parse", 8))
  }


  /**
   * This method is used to build the scala class.
   * @param classAttr =: authors, import, [source id=""] attribute, className, body, support methods
   * @return A string containing all the informations about the class.
   */
  def buildClass(classAttr:(String, String, String, String, String, String)): String = {
    val authors = classAttr._1.trim

    classAttr._2 + brr + //import
    "/**%s".format(br) + //comments
    " * %s".format(trans.read("gensource", classAttr._3) + br) + //[source id=""] attribute
    (if(authors.nonEmpty) " * @%s: %s".format(trans.read("author"), authors + br) else "") +
    " */" +br+
    "object %s{%s".format(classAttr._4, brr) + //class name
    "%sdef main(args: Array[String]){%s".format(tb1, br) + //the main method
    classAttr._5 +br+ //the body tabs are inserted in the IStorage/AbsStorage.get...Query methods
    "%s}%s".format(tb1, br) +
    (if(classAttr._6.nonEmpty) brr+tb1+ classAttr._6 +br else "") +"}"
    //support methods: the additional line breaks are inserted in the AbsStorage.addSupMethod method
  }
  /********** methods (class related) - end **********/


  /********** methods (node related) - start **********/
  /**
   * This method is used by the _Storage objects to add the initialized attributes to the report.
   * @param report The report object in which the setting of the definitions are stored.
   * @param defs The keys and values.
   * @param tabn A sequence made of a certain amount of double tabs.
   */
  def reportDef(report: StringBuilder, defs: Map[String, String], tabn: String) = report.append(
    "%s<%s> <!--%s-->%s%s%s</%s>".format(
      br+tabn, CoreAdmin.defs, trans.read("param", "set"), br,
      Reader.read(defs.keys.mkString(c+" "), (tabn+tab1).length), //definition text, distance from the left margin
      br+tabn, CoreAdmin.defs
    )
  )


  /**
   * This method retrieves from the "references" node the appropriate "ref" node describing
   * the given current node. If nothing is found this method will return the given current node.
   * @note The [node ref=""] attribute is only allowed for the following nodes:
   *       {source, target, definitions, run}
   * @param node The node used when calling the method.
   * @return A node object.
   */
  def getRef(references: Seq[Node], node: Node): Node = {
    val ref = (node \ "@ref").text
    if(ref.nonEmpty) references.find(n =>
      (n \_id).text.equals(ref) && (n \ "@label").text == node.label
    ) match {
      case None => node
      case Some(value) => value
    }
    else node
  }


  /**
   * This method retrieves the appropriate node to include for the given current node.
   * If nothing is found this method will return the current node.
   * @note The [node include=""] attribute is only allowed for the following nodes:
   *       {header, footer, references, transfer}
   * @param node The node used when calling the method.
   * @return A node object.
   */
  def getInc(node: Node): Node = {
    val fileName = (node \ "@include").text.trim
    if(fileName.nonEmpty){
      val elt = new XmlReader(getCanPath(fileName)).root //<node/>
      if(elt.isInstanceOf[Elem] && elt.label == node.label) elt else node
    }else node
  }


  /**
   * This method parses the [definitions] node into a HashMap object.
   * @note So far definitions with an empty value are allowed.
   * @param node The node to be parsed.
   * @return A Map object
   */
  def parseDefs(node: Node): Map[String, String] = {
    val map = Map[String, String]()
    (node \_def).foreach{child =>
      val key = (child \_key).text
      if(key.nonEmpty) map += (key -> (child \_val).text)
    }

    map
  }


  /**
   * This method is invoked to parse a [transade.transfer.source] node.
   * @param _node The [source] node to be parsed.
   * @param references see class TransadeXmlAdmin.references
   * @param jarFileNames see the method TransadeXmlAdmin.run
   * @param report see the method TransadeXmlAdmin.run
   * @return 5 string values corresponding to 5 different queries:
   *         [source id=""] attribute, body, authors, import, support methods
   */
  def parseSource(
    _node: Node, references: Seq[Node], jarFileNames: Map[String, Int], report: StringBuilder
  ): (String, String, String, String, String) = {
    val (error, sourceId, format) = (("","","","", ""), (_node \_id).text, (_node \_format).text)
    report.append("%s<%s %s=%s %s=%s>".format( //report: <source>
      br+tab2, _node.label, CoreAdmin.id, a+sourceId+a, CoreAdmin.format, a+format+a
    ))
    val node = getRef(references, _node)
    val targets = node \\ target

    if(sourceId.nonEmpty && format.nonEmpty && targets.length > 0){
      /* Definitions:
       * - the "getRef" method is invoked in the "parseDefs" method
       * - the report is done in the "storages" package in the adequate "Storage" class
       */
      val seq = node \\ defs
      if(seq.nonEmpty) try{
        //handle the format attribute and the definitions node.
        val (source, defs) = (Storage.getStorage(format), parseDefs(getRef(references, seq(0))))
        //source = null if the format is unknown
        source.register.setDefs(defs)
        reportDef(report, defs, tab3)

        val (imp, targetCon, innerLoop, targetDisCon, supMeth) = (
          new StringBuilder(source.getImpQuery),
          new StringBuilder, new StringBuilder, new StringBuilder, new StringBuilder
        )
        source.copyJarFileNamesTo(jarFileNames)

        /* Targets:
         * 1:import query
         * 2:connect query
         * 3:inner loop + insert query
         * 4:disconnect query
         * 5:support methods
         */
        targets.foreach(child => {
          val rs = parseTarget(child, references, source, jarFileNames, report)//target -> the search for a reference is done here

          if(!imp.mkString.contains(rs._1)) imp.append(rs._1) //avoid the repetition of import queries
          targetCon.append(rs._2)
          innerLoop.append(rs._3)

          val temp = targetDisCon.mkString
          targetDisCon.clear()
          targetDisCon.append(rs._4 + temp)

          if(!supMeth.mkString.contains(rs._5)) supMeth.append(rs._5)//avoid the repetition of methods queries
        })

        //results
        val body = source.getTryQuery +
          source.getConQuery + targetCon.mkString +
          source.getReadQuery + source.getLoopQuery(innerLoop.mkString) +
          targetDisCon.mkString + source.getDisconQuery +
          source.getCatchQuery

        //last support method
        val meth = source.getSupMethQuery
        if(!supMeth.mkString.contains(meth)) supMeth.append(meth)

        //report
        //The closure is done in the "run"- method

        (sourceId, body, (node \ "@authors").text, imp.mkString, supMeth.mkString) //return
      }catch{case e: NullPointerException =>
        report.append(br + bug.read(CoreAdmin.format, 6, format))
        error
      }else{
        report.append(br + bug.read(defs, 6))
        error
      }
    }else{
      report.append(br + bug.read(source, 6))
      error
    }
  }


  /**
   * This method is invoked to parse a [transade.transfer.source.target] node.
   * @param _node The [target] node to be parsed.
   * @param references see class TransadeXmlAdmin.references
   * @param source The source IStorage object
   * @param jarFileNames see the method TransadeXmlAdmin.run
   * @param report see the method TransadeXmlAdmin.run
   * @return 5 string values corresponding to 5 different queries:
   *         -import, -connection, -inner loop, -disconnection, -support methods
   */
  def parseTarget(
    _node: Node, references: Seq[Node], source: IStorage,
    jarFileNames: Map[String, Int], report: StringBuilder
  ): (String, String, String, String, StringBuilder) = {
    val (error, format, label) = (("","","","",new StringBuilder), (_node \_format).text, _node.label)
    report.append("%s<%s %s=%s %s=%s>".format( //report: <target>
      br+tab3, label, id, a+(_node \_id).text+a, CoreAdmin.format, a+format+a
    )) //the id here is optional, unlike that of the [source]- and that of the [transfer] nodes it resides in
    val node = getRef(references, _node)
    val parses = node \\ parse

    if(format.nonEmpty && parses.length > 0){
      val seq = node \\ defs //the "getRef" method is invoked in the "parseDefs" method
      if(seq.nonEmpty)try{
        //the "target storage" attribute has to be set before the "parseParse" method is invoked.
        val (target, defs) = (Storage.getStorage(format), parseDefs(getRef(references, seq(0))))
        //target = null if the format is unknown
        target.register(source).setDefs(defs)
        reportDef(report, defs, tab4)

        //handle the parses
        val(iLoop, cols, values, imp) = (
          new StringBuilder, new StringBuilder, new StringBuilder, target.getImpQuery
        )
        target.copyJarFileNamesTo(jarFileNames)

        val lists = Map[Rs.rs, ListBuffer[String]]( //report
          Rs.GOOD -> ListBuffer[String](),
          Rs.POORLY -> ListBuffer[String](),
          Rs.BAD -> ListBuffer[String]()
        )
        val conQuery = target.getConQuery //connection query

        parses.foreach(child => { //parses
          val rs = parseParse(child, references, target)//the search for a ref is done here
          iLoop.append(rs._1)
          cols.append(rs._2 + c)
          values.append(rs._3 + cc)
          lists(rs._4) += (child \_tName).text //report
        })

        val innerLoop = iLoop + target.getWriteQuery( //results: inner loop
          cols.mkString.replaceAll(c+"$", ""), values.mkString.replaceAll(cc+"$", "")
        )

        //report
        val st = "status"
        lists.foreach(tupel =>
          if(tupel._2.nonEmpty) report.append("%s<%s %s=%s> <!--%s-->%s%s%s</%s>".format( //report: <parse>
            br+tab4, parse, st, a+tupel._1+a, trans.read("param", "parsed"), br,
            Reader.read(tupel._2.mkString(c+" "), 10) + br,
            tab4, parse
          ))else report.append("%s<%s %s=%s/>".format(br+tab4, parse, st, a+tupel._1+a))
        )
        report.append("%s</%s>".format(br+tab3, label)) //report: </target>

        (imp, conQuery, innerLoop, target.getDisconQuery, target.getSupMethQuery) //return
      }catch{case e: NullPointerException =>
        report.append("%s</%s>".format(br+bug.read(CoreAdmin.format, 8, format)+br+tab3, label))
        //report: </target>
        error
      }else{
        report.append("%s</%s>".format(br+bug.read(defs, 8)+br+tab3, label)) //report: </target>
        error
      }
    }else{
      report.append("%s</%s>".format(br+bug.read(target, 8)+br+tab3, label)) //report: </target>
      error
    }
  }


  /**
   * This method is invoked to parse a [transade.transfer.source.target.parse] node.
   * @param _node The [parse] node to be parsed.
   * @param references see class TransadeXmlAdmin.references
   * @param target The target IStorage object
   * @return 4 different values:
   *         statement query, target name, the wrapped target name, parsed status   
   */
  def parseParse(_node: Node, references: Seq[Node], target: IStorage)
  : (String, String, String, Rs.rs) = {
    //the search for a reference is done in the "parseTarget" method
    val targetName = (_node \_tName).text
    
    if(targetName.nonEmpty){
      val (iLoopStatement, comment, sourceNameAttr) = //source name as an attribute
        (new StringBuilder, tabPh+"//"+targetName+br, (_node \_sName).text)
      var parsed = Rs.GOOD

      if(sourceNameAttr.nonEmpty)
        iLoopStatement.append(target.setTupel(targetName, parseSourceName(sourceNameAttr))) //key + value
      else{
        val node = getRef(references, _node)
        val seq = node \ sName

        if(seq.nonEmpty) //source name as the child node
          iLoopStatement.append(target.setTupel(targetName, parseSourceName(seq(0).text)))//key + value
        else{ //handle the [if] nodes
          val (blocks, tupel) = (
            //1:if- & else if- statements, 2:else- statement, 3:current state of the variable "_if"
            (new StringBuilder, new StringBuilder, new StringBuilder(_if)),
            target.setTupel(targetName, valPh) //key + %VALUE%
          )

          (node \\ _if).foreach{child => //NO REFERENCE
            val _parsed = parseIf(child, blocks, tupel)
            if(_parsed > parsed) parsed = _parsed
          }

          iLoopStatement.append(
            if(blocks._1.nonEmpty)
              blocks._1.mkString + (if(blocks._2.nonEmpty) "else{%s}".format(blocks._2) else "")
            else{
              if(parsed > Rs.BAD) parsed = Rs.POORLY //poorly due to the unnecessary <if/> node
              blocks._2.mkString.trim
            }
          )
        }
      }

      (
        if(iLoopStatement.nonEmpty) comment + tabPh + iLoopStatement.mkString + br else "",
        targetName,
        target.wrapTargetRow(targetName), //wrapping the target row content
        parsed
      )
    }else ("", "", "", Rs.BAD)
  }


  /**
   * This method is invoked to parse a [transade.transfer.source.target.parse.if] node.
   * @param node The [if] node to be parsed.
   * @param values 1:if- & else if- statements,
   *               2:else- statement,
   *               3:current state of the variable "_if"
   * @param tupel key + %VALUE%
   * @return A Rs.rs object
   */
  def parseIf(node: Node, values:(StringBuilder, StringBuilder, StringBuilder), tupel: String)
  : Rs.rs = {
    val(cond, srText) = ((node \ "@condition").text, (node \_sName).text) //source name and condition as attributes
    val sourceName = if(srText.isEmpty) (node \ sName)(0).text else srText //source name as a child node
    val tupelLine = br + tabPh + tb1 + tupel.replace(valPh, parseSourceName(sourceName)) + br + tabPh

    if(cond.isEmpty){ //else statement
      /* WARNING: do not merge both statements
       * Only the first "if" node with a missing or empty "condition" attribute will be considered.
       * All the following node having the same preconditions will simply be ignored.
       * There can only be on "else" statement, that's why.
       */
      if(values._2.isEmpty){ //the first else- statement
        values._2.append(tupelLine)
        Rs.GOOD
      }else Rs.POORLY //there should be only one else statement
    }else{//if and else if statements
      val pCond = parseCondAttr(cond)
      if(pCond.nonEmpty){
        values._1.append("%s(%s){%s}".format(values._3, pCond, tupelLine))
        if(values._3.mkString.equals(_if)){ //change from "if" to "else if"
          values._3.clear
          values._3.append("else if")
        }
        Rs.GOOD
      }else Rs.BAD //The parser has encountered an abnormality
    }
  }


  /**
   * This method parses the given source row into a text sequence similar
   * to the java/scala programming language.
   * @param content The sourcerow content to be parsed. [parse sourcerow=""] or [if sourcerow=""].
   * @return a string representing the parsed content.
   */
  def parseSourceName(content: String): String = Reader.read(
    //"^ *(-?\\d+(.\\d+)?|true|false|\".*\"|<.+>|'.+|%s) *$" =: ... + xml & symbols
    //the content matches one of the following data types: boolean, number, string, placeholder
    if(content.matches("^ *(-?\\d+(.\\d+)?|true|false|\".*\"|%s) *$".format(phRegex)))
      content.trim.replaceAll("(%s)".format(phRegex), "%s$1%s".format(anyDt, anyDt))
    else a+content+a //the content is something else, is therefore transformed to a text.
  )


  /**
   * This method parses the given cond into a text sequence similar
   * to the java/scala programming language.
   * @param cond The condition to be parsed.
   * @return a string object representing the parsed cond statement
   */
  def parseCondAttr(cond: String): String = {
    //phRegex = "\\$\\{[a-zA-Z_]+\\w*\\}"
    //http://www.vogella.com/articles/JavaRegularExpressions/article.html
    //http://docs.oracle.com/javase/tutorial/essential/regex/quant.html

    //first remove the minimal mistakes, after that break the condition in pieces
    def group(g: String) = "(%s *)%s( |true|false|\\$|\\d|-|'|<|\")".format(phRegex, g)
    val conds = cond.replaceAll(group("="), "$1==$2") //"=" -> "=="
      .replaceAll(group("!"), "$1!=$2") //"!" -> "!="
      .replaceAll(group("=(>|<|!)"), "$1$2=$3") //"=>" -> ">=" && "=<" -> "<=" && "=!" -> "!="
      //eliminating the wrong inequalities
      //"(%s *)(>|<)=( *true| *false| *<| *')" =: boolean | xml | symbols
      .replaceAll("(%s *)(>|<)=( *true| *false)".format(phRegex), "$1!=$3")
      .split("\\$\\{")

    //second the regex of numbers, strings, booleans and maybe dates are set
    val (dtPh, suffix) = ("%DATATYPE%", "[ )]*((&&|\\|{2})[ (]*)?")
    //suffix =: only the following signs can logically come after an object:
    //nothing or ")" or "&&" or "||" or "&&(" or "||("
    def regex(eq: String, value: String) = "^%s *(%s) *%s$".format(
      dtPh + phRegex + dtPh, eq, value + suffix
    )
    val (boolRegex, boolRegex2, numRegex, strRegex, devRegex, phPhRegex) = (
      regex("==|!=", "(true|false)"), //boolean object with equals
      "^%s$".format(dtPh + phRegex + dtPh + suffix), //boolean object without equals
      regex("==|!=|>=?|<=?", "-?\\d+(.\\d+)?"), //numbers
      regex("==|!=|>=?|<=?", "\".*\""), //string object - . =: [\\w\\W]
      "^%s *\\. *[a-zA-Z_]+\\.*$".format(dtPh + phRegex + dtPh),
      //insertion: this enables the use of a considerable number of String related Scala methods.
      "^%s( *(!|=)= *|%s)$".format(dtPh + phRegex + dtPh, suffix)
      //left hand of a placeholder to placeholder (in)equality | right hand of a placeholder to placeholder (in)equality
    )

    //third the parse is made
    val condition = new StringBuilder(conds.head) //the left hand of the first '$'
    conds.drop(1).foreach{c => //c is used for something else outside of this loop
      val cur = "%s${%s".format(dtPh, c.replace("}", "}"+dtPh))
      val dtPr = if(cur.matches(boolRegex) || cur.matches(boolRegex2)) boolDt
      else if(cur.matches(numRegex)) numDt
      else if(cur.matches(strRegex) || cur.matches(phPhRegex) || cur.matches(devRegex)) strDt
      else return "" //the parser encountered an abnormality =: bad parsing

      condition ++= cur.replace(dtPh, dtPr)
    }
    /* Alternative
    (1 until conds.length).foreach{i =>
      val cur = "%s${%s".format(dtPh, conds(i).replace("}", "}"+dtPh))
      val dtPr = if(cur.matches(boolRegex)) boolDt
      else if(cur.matches(numRegex)) numDt
      else if(cur.matches(strRegex) || cur.matches(phPhRegex) || cur.matches(devRegex)) strDt
      else return "" //the parser encountered an abnormality =: bad parsing

      condition ++= cur.replace(dtPh, dtPr)
    }*/

    condition.mkString //no trimming on purpose
  }
  /********** methods (node related) - end **********/
}


/**
 * The class administrator of the .xml file using the transade template.
 * @param ref The reference node.
 * @param preview see the method transade.core.loaders.XmlFileLoader.compute.
 * @param output see the class CoreAdmin.
 */
protected[core] final class TransadeXmlAdmin(ref: Node, preview: Boolean, output: String => Unit){
  import TransadeXmlAdmin.{admin, pName, getInc, parseSource}

  private val references = getInc(ref) \\ "ref" //the [transade.references.ref] nodes

  /**
   * This method is used to parse the given [transade.transfer] node;
   * and compile and execute the resulting scala class.
   * @param node The [transfer] node to be parsed and eventually compiled & executed.
   * @return a string object representing the report
   */
  def run(node: Node): String = {
    var (start, end) = ("", "")
    val report = new StringBuilder //the object containing the computation report of this node

    if(node.label.equals(transfer)){
      //the [transfer] node itself & the map containing all the .jar files required for the compilation
      val transferId = a+(node \_id).text+a
      if(transferId.nonEmpty){
        val (_node, jarFileNames) = (getInc(node), Map[String, Int]())

        start = date
        report.append("%s<%s id=%s ".format(tab1, _node.label, transferId))//report: <transfer>
        output(view.read("outputstart", transferId))

        //metadata
        val _meta = "metadata"
        val seq = _node \\ _meta

        if(seq.nonEmpty){
          val meta = seq(0)
          report.append("%s=%s %s=%s %s%s>%s%s".format( //report: <transfer>
            pName, a+(meta \ ("@" + pName)).text+a, admin, a+(meta \ ("@"+admin)).text+a, br,
            tab2 + timePh, br,
            tab2
          ))
          val desText = (meta \ des).text
          if(desText.nonEmpty)report.append("<%s>%s</%s>".format( //report: <description>...</description>
            des, br+Reader.read(desText, 6)+br+tab2, des
          ))else report.append("<%s/>".format(des)) //report: <description/>
        }else report.append("%s=%s %s=%s %s%s>%s%s<%s>%s%s%s</%s>".format(
        //report: <transfer><description>...</description>
          pName, a+a, admin, a+a, br,
          tab2+timePh, br,
          tab2, des, br,
          bug.read(_meta, 6)+br,
          tab2, des
        ))

        val nodes = _node \\ source
        if(nodes.nonEmpty) nodes.foreach{child => //parse the [source] nodes && compile (&& execute) their results
          TransadeXmlAdmin.process(
            parseSource(child, references, jarFileNames, report), //report: <source>
            preview, jarFileNames, report
          )
          /* Note: Unlike in the other parse- methods the "source"-report node has to be closed outside
           * of the "parseSource" method.
           * This due to the fact that the "compile"-report node has to be created in withing
           * it's respective "source"-report node.
           */
          report.append("%s</%s>".format(br+tab2, child.label)) //report: </source>
        }else report ++= br+bug.read(source+"not", 4)

        output(view.read("outputend", transferId)) //end status of the transfer computation
        report.append("%s</%s>".format(br+tab1, node.label)) //report: </transfer>
        end = date
      }else report ++= "%s<%s>%s%s</%s>".format( //report: <transfer>...</transfer>
        tab1, node.label, br+bug.read(transfer, 4)+br, tab1, node.label
      )
    }else report.append("%s<%s>%s%s</%s>".format( //report: <transfer>...</transfer>
      tab1, node.label, br+bug.read("wrongnode", 4, transfer)+br, tab1, node.label
    ))

    report.mkString.replace(timePh, "start=%s end=%s".format(a+start+a, a+end+a))
  }

}
