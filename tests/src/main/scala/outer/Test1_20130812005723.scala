package outer

/**
* This class is the result of a specific conversion of xml in scala.
*/

import scala.collection.mutable.Map
import scala.collection.mutable.{Map, ListBuffer}
import java.io.{File, FileInputStream}
import java.sql.DriverManager
import java.io.{File, FileOutputStream}
import org.apache.poi.hssf.usermodel.{HSSFWorkbook, HSSFSheet}
import org.apache.poi.ss.usermodel.{Row, Cell}


object Test1_20130812005723{

	def run: Boolean = {
		try{
			println("Initializing and connecting the attributes.")

			// Excel storage (source storage)
			val file = new FileInputStream(new File(""))
			val workBook = new HSSFWorkbook(file)
			val sheet = workBook.getSheetAt(1)

			// Sql storage 0 (target storage)
			Class.forName("com.mysql.jdbc.Driver")
			val connect0 = DriverManager.getConnection("jdbc:mysql://localhost?user=root2&passowrd=22222")
			val statement0 = connect0.createStatement

			// Excel storage 0 (target storage)
			val file0 = new FileOutputStream(new File(""))
			val workBook0 = new HSSFWorkbook()
			val sheet0 = workBook0.createSheet("test1")

			// read query
			val rowIt = sheet.iterator
			val keys = ListBuffer[String]()

			// ini
			val tempMap = Map[String, Any]()
			println("Starting the loop iterator.")

			// the loop itself
			var round = 0
			while(rowIt.hasNext){
				round += 1
				val cellIt = rowIt.next.cellIterator

				if(round >= 5){
					if(round == 5) while(cellIt.hasNext) keys += cellIt.next.getStringCellValue

					var i = -1
					val innerMap = Map[String, Any]()
					while(cellIt.hasNext){
					i += 1
						val cell = cellIt.next
						innerMap(keys(i)) = cell.getCellType match{
							case Cell.CELL_TYPE_BOOLEAN => cell.getBooleanCellValue
							case Cell.CELL_TYPE_NUMERIC => cell.getNumericCellValue
							case _ => cell.getStringCellValue
						}
					}

					//_firstname
					tempMap("_firstname") = innerMap("firstname").asInstanceOf[String]
					//_lastname
					tempMap("_lastname") = innerMap("lastname").asInstanceOf[String]
					//_age
					tempMap("_age") = innerMap("age").asInstanceOf[String]
					//_address
					if(innerMap("age").asInstanceOf[Double] > 26 && innerMap("age").asInstanceOf[Double] < 79){
						tempMap("_address") = innerMap("own_address").asInstanceOf[String]
					}else if(innerMap("age").asInstanceOf[Double] <= 26){
						tempMap("_address") = innerMap("moms_address").asInstanceOf[String]
					}else{
						tempMap("_address") = ""
					}
					val query0 = "INSERT INTO database1.table1 (_firstname,_lastname,_age,_address) VALUES ('" + tempMap("_firstname")+"','"+tempMap("_lastname")+"','"+tempMap("_age")+"','"+tempMap("_address") + "')"
					statement0.executeUpdate(query0)

					//_profession
					tempMap("_profession") = "student"
					//_alive
					tempMap("_alive") = innerMap("alive").asInstanceOf[String]
					//_zipcode
					if(innerMap("alive").asInstanceOf[Boolean] == true){
						tempMap("_zipcode") = 68161
					}else{
						tempMap("_zipcode") = 0000
					}
					//_salary
					if(innerMap("profession").asInstanceOf[String] == "computer scientist" && innerMap("age").asInstanceOf[String] != "79"){
						tempMap("_salary") = "1230€"
					}else if(innerMap("profession").asInstanceOf[String] != innerMap("tester").asInstanceOf[String] && innerMap("profession").asInstanceOf[String] != "squasher"){
						tempMap("_salary") = "1000€"
					}else{
						tempMap("_salary") = "800€"
					}
					write(sheet0, round, tempMap("_profession")+","+tempMap("_alive")+","+tempMap("_zipcode")+","+tempMap("_salary"))
				}
			}

			println("The loop iterator has ended.")

			// disconnect
			file0.close
			connect0.close
			file.close
			println("Disconnecting the attributes.")
			println("The transfer was successful.")
			true
		}catch{case e: Exception => 
			println("Something went wrong during the transfer.")
			System.err.println(e.getClass.getSimpleName)
			System.err.println(e.getMessage)

			false
		}
	}

	

	private def write(sheet: HSSFSheet, i: Int, cols: String){
		val row = sheet.createRow(i)
		var j = 0
		cols.split(",").foreach(col => {
			j += 1
			row.createCell(j).setCellValue(col)
		})
	}


	def main(args: Array[String]){
		println(run)
	}
}