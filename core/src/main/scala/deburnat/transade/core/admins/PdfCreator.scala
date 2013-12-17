package deburnat.transade.core.admins

import java.io.{FileOutputStream, File}

import com.lowagie.text.{Font, Paragraph, Document}
import com.lowagie.text.pdf.PdfWriter

import CoreAdmin.{br, des, _pdf, pdf, system, root, xml, date, bug, trans, platform, getDirPath, getExceptionNode}
import FileAdmin.save
import deburnat.transade.core.readers.Reader

/**
 * An algorithm for dynamic programming. It uses internally a two-dimensional
 * matrix to store the previous results.
 * Project name: deburnat
 * Date: 8/24/13
 * Time: 8:38 PM
 * @author Patrick Meppe (tapmeppe@gmail.com)
 */
protected[transade] object PdfCreator {
  private val (title, titleFont, boldFont, normFont, xmlFont) = (
    "title",
    new Font(Font.TIMES_ROMAN, 25, Font.BOLD),
    new Font(Font.TIMES_ROMAN, 12, Font.BOLD),
    new Font(Font.TIMES_ROMAN, 12, Font.NORMAL),
    new Font(Font.COURIER, 10)
  )

  private def read(s: String) = trans.read(s)

  /**
   *
   * @see http://www.vogella.com/articles/JavaPDF/article.html
   * @param report
   * @return
   */
  def saveReport(report: String, paths: String) = {
    val filePath = getDirPath + trans.read("filename") + _pdf

    try{
      //initialize the document
      val (doc, file) = (new Document, new File(filePath))

      PdfWriter.getInstance(doc, new FileOutputStream(file))
      doc.open

      //add metadata
      doc.addTitle(read(title))
      doc.addSubject(trans.read("subject", paths))
      doc.addKeywords(platform("keywords", false))
      doc.addAuthor(platform("author", false)) //document.addCreator("")
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

      file
    }catch {case e: Exception => //DocumentException
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
   *
   * @param par
   * @param nr
   */
  private def addLine(par: Paragraph, nr: Int){
    (0 until nr).foreach(_ => par.add(new Paragraph(" ")))
  }
}
