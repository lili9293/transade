package deburnat.transade.core.admins

import java.io.{FileOutputStream, File}
import com.lowagie.text.{Font, Paragraph, Document} //do not merge with the statement below
import com.lowagie.text.pdf.PdfWriter
import CoreAdmin._
import FileAdmin.save
import deburnat.transade.core.readers.Reader

/**
 * Project name: transade
 * @author Patrick Meppe (tapmeppe@gmail.com)
 * Description:
 *  An algorithm for the transfer of selected/adapted data
 *  from one repository to another.
 *
 * Date: 8/24/13
 * Time: 8:38 PM
 */
protected[transade] object PdfCreator {
  private val (title, titleFont, boldFont, normFont, xmlFont, read) = (
    "title",
    new Font(Font.TIMES_ROMAN, 25, Font.BOLD),
    new Font(Font.TIMES_ROMAN, 12, Font.BOLD),
    new Font(Font.TIMES_ROMAN, 12, Font.NORMAL),
    new Font(Font.COURIER, 10),
    (s: String) => trans.read(s) //val over def simply to highlight the term "read" below
  )

  /**
   * This method is used to save the new created report as a .pdf file.
   * @see http://www.vogella.com/articles/JavaPDF/article.html
   * @param report The report to be saved.
   * @return A file object representing the newly created report.pdf file.
   *         However in case of an exception a file representing the newly report.xml file.
   */
  def saveReport(report: String, paths: String) = {
    val filePath = getDirPath + reportFileName + _pdf //the report file path
    println(pdf)
    try{
      val (doc, file) = (new Document, new File(filePath)) //initialize the document

      PdfWriter.getInstance(doc, new FileOutputStream(file))
      doc.open

      //add metadata
      doc.addTitle(read(title))
      doc.addSubject(trans.read("subject", paths))
      doc.addKeywords(_platform("keywords"))
      doc.addAuthor(_platform("author"))
      doc.addCreator(_platform("creator"))
      doc.addCreationDate

      //title page
      val page1 = new Paragraph
      addLine(page1, 1)
      page1.add(new Paragraph(read(title), titleFont))
      addLine(page1, 3)
      page1.add(new Paragraph("%s: %s%s: %s%s:%s".format(
        read("gen"), system("user.name")+br,
        read("date"), date+br,
        read(xml), br+Reader.read(paths, 2)
      ), boldFont))
      addLine(page1, 5)

      page1.add(new Paragraph(read(des), normFont))
      //page1.setAlignment(Element.ALIGN_LEFT)
      doc.add(page1)

      //second page
      doc.newPage
      doc.add(new Paragraph(report, xmlFont))

      doc.close //close the document
      file //return
    }catch{case e: Exception => //DocumentException
      /* TEMPLATE
        <report exception={e.getClass.getSimpleName}>
          {bug.read(pdf, 2, filePath)+br+Reader.read(e.getMessage, 2)}
        </report>
      */
      save(
        filePath.replaceAll(pdf+"$", xml),
        getExceptionNode("", root, e, bug.read(pdf, 2, filePath)+br+Reader.read(e.getMessage, 2))
      )
    }
  }


  /**
   * This method is used to add a certain amount of empty lines in a given paragraph.
   * @param par The paragraph in which the line(s) are to be added.
   * @param nr The number of lines to add.
   */
  private def addLine(par: Paragraph, nr: Int) = (0 until nr).foreach{
    _ => par.add(new Paragraph(" "))
  }

}
