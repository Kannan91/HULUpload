package com.unilever.unilever.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.unilever.unilever.controller.DBconnection;
import com.unilever.unilever.controller.DataCalculate;

//import wasdev.sample.servlet.Test.PayloadClass;

@Controller
public class MainController {

	private static final String USERNAME = "admin_68a14adf7f56891156fda4dd8e09bdac08f4df53";
	private static final String PASSWORD = "xl,OrD5VIQ6Y!CRd";
	private static final String DOMAIN_ID = "3a512cebf44640acaefed32112e9ce8c";
	private static final String PROJECT_ID = "0ab4d206302d4cb38b0e3d1f9c8681bd";

	// The Environment object will be used to read parameters from the
	// application.properties configuration file
	@Autowired
	private Environment env;

	/**
	 * Show the index page containing the form for uploading a file.
	 */
	@RequestMapping("/")
	public String index() {
		return "index.html";
	}

	/**
	 * POST /uploadFile -> receive and locally save a file.
	 * 
	 * @param uploadfile
	 *            The uploaded file as Multipart file parameter in the HTTP
	 *            request. The RequestParam name must be the same of the
	 *            attribute "name" in the input tag with type file.
	 * 
	 * @return An http OK status in case of success, an http 4xx status in case
	 *         of errors.
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> uploadFile(@RequestParam("uploadfile") MultipartFile uploadfile) {

		try {

			int count = 0;
			DataCalculate dataCalculate = new DataCalculate();
			InputStream inputStream = uploadfile.getInputStream();

			ArrayList data = new ArrayList();

			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			int numberOfSheets = workbook.getNumberOfSheets();
			for (int i = 0; i < numberOfSheets; i++) {

				Sheet firstSheet = workbook.getSheetAt(i);
				System.out.println(firstSheet.getSheetName());
				/*
				 * int rowEnd = firstSheet.getLastRowNum();
				 * 
				 * for (int j = 1; j < rowEnd; j++) { Row r =
				 * firstSheet.getRow(j);
				 * 
				 * int lastColumn = r.getLastCellNum();
				 * 
				 * for (int cn = 0; cn < lastColumn; cn++) { Cell c =
				 * r.getCell(cn, Row.RETURN_BLANK_AS_NULL); if (c == null) { //
				 * The spreadsheet is empty in this cell } else { // Do
				 * something useful with the cell's contents } } }
				 */
				Iterator<Row> rowIterator = firstSheet.rowIterator();
				int rowCount = 0;
				rowIterator.next();
				while (rowIterator.hasNext()) {
					System.out.println("rowCount" + rowCount);
					XSSFRow row = (XSSFRow) rowIterator.next();

					Iterator<Cell> cellIterator = row.cellIterator();

					// List data = new LinkedList();

					DataFormatter fmt = new DataFormatter();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();

						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							System.out.print(cell.getStringCellValue());
							data.add(cell);
							break;
						case Cell.CELL_TYPE_BOOLEAN:
							System.out.print(cell.getBooleanCellValue());
							data.add(cell);
							break;
						case Cell.CELL_TYPE_NUMERIC:
							System.out.print(cell.getNumericCellValue());
							data.add(fmt.formatCellValue(cell));
							break;
						case Cell.CELL_TYPE_FORMULA:
							System.out.print(cell.getCellFormula());
							data.add(fmt.formatCellValue(cell));
							break;
						case Cell.CELL_TYPE_BLANK:
							break;
						}
						// System.out.print(" - ");
					}
					rowCount++;
					System.out.println("List Value :" + data);
					insertData(count, data);
					data.clear();
				}
				count++;

			}
			inputStream.close();
			DataCalculate.calculateData();

		} catch (Exception e) {

			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	// Insert Data Method

	public static void insertData(int count, ArrayList data) {
		DBconnection dbConnect = new DBconnection();
		if(count==0)
       	{
    		Connection conn=null;
        	PreparedStatement pstmt=null,pstmt1=null;
        	ArrayList dataHolder=data; 
        	conn = DBconnection.createDbConn();
    		try {
    			String sql= "insert into input_parameter(Source_Type, Source, SKU, SKU_Name, Location, Location_Type, Location_Layer, Category, Service_Level, SKU_Classification, Production_Time, QA_Time, Transit_Time, Planning_Period, Frozen_Period, OR_Value, Other_Lead_Time, Lead_Time_Variability, Current_SSWeeks, Price) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt=conn.prepareStatement(sql);
					System.out.println("ArrayList:" +dataHolder);
					pstmt.setString(1, data.get(0).toString());
					pstmt.setString(2, data.get(1).toString());
					pstmt.setString(3, data.get(2).toString());
					pstmt.setString(4, data.get(3).toString());
					pstmt.setString(5, data.get(4).toString());
					pstmt.setString(6, data.get(5).toString());
					pstmt.setInt(7, Integer.parseInt(data.get(6).toString()));
					pstmt.setString(8, data.get(7).toString());

					String value= data.get(8).toString();
					String value2=value.replaceAll("%", "");
					pstmt.setFloat(9, Integer.parseInt(value2));
					pstmt.setString(10, data.get(9).toString());
					pstmt.setDouble(11, Double.parseDouble(data.get(10).toString()));
					System.out.println("Float Value:"+ Float.parseFloat(data.get(10).toString()));
					pstmt.setFloat(12, Float.parseFloat(data.get(11).toString()));
					pstmt.setFloat(13,Float.parseFloat(data.get(12).toString()));
					pstmt.setFloat(14, Float.parseFloat(data.get(13).toString()));
					pstmt.setFloat(15, Float.parseFloat(data.get(14).toString()));

					String value3=dataHolder.get(15).toString();
					String value4= value3.replaceAll("%", "");
					pstmt.setInt(16, Integer.parseInt(value4));

					pstmt.setFloat(17, Float.parseFloat(data.get(16).toString()));
					pstmt.setFloat(18, Float.parseFloat(data.get(17).toString()));
					pstmt.setFloat(19, Float.parseFloat(data.get(18).toString()));
					pstmt.setFloat(20, Float.parseFloat(data.get(19).toString()));

				 pstmt.executeUpdate();

				 String skuCode=data.get(2).toString()+"-"+data.get(4).toString();
				 float avgTime =(Float.parseFloat(dataHolder.get(10).toString())/2)+(Float.parseFloat(dataHolder.get(11).toString()))+(Float.parseFloat(dataHolder.get(12).toString()))+(Float.parseFloat(dataHolder.get(13).toString()))+(Float.parseFloat(dataHolder.get(16).toString()));

				 System.out.println("SKU"+ skuCode + "AvgTime" + avgTime);
				 String sql1="update input_parameter SET Material_Location=?, Avg_Lead_Time=? where SKU='"+data.get(2).toString()+"' and Location='"+data.get(4).toString()+"'";
				 pstmt1 = conn.prepareStatement(sql1);

				 pstmt1.setString(1, skuCode);
				 pstmt1.setFloat(2, avgTime);		 


				 pstmt1.executeUpdate();
    		} catch (SQLException e) {
				e.printStackTrace();
			} 
    		finally {
      			try {
	      			conn.close();
	      			pstmt.close();
	      			pstmt1.close();
      			}
      			catch (SQLException e) {
      				e.printStackTrace();
      		 }
      		}

    	} 
		if(count==1)
		 {
   		Connection conn=null;
       	PreparedStatement pstmt=null,pstmt1=null;
       	ArrayList dataHolder=data; 
       	conn = DBconnection.createDbConn();
   		try {

   			String sql= "insert into Past_Forecast(SKU, LOCATION, Week35_2016, Week36_2016, Week37_2016, Week38_2016, Week39_2016, Week40_2016, Week41_2016, Week42_2016, Week43_2016, Week44_2016, Week45_2016, Week46_2016, Week47_2016, Week48_2016, Week49_2016, Week50_2016, Week51_2016, Week52_2016, Week01_2017, Week02_2017, Week03_2017, Week04_2017, Week05_2017, Week06_2017, Week07_2017, Week08_2017, Week09_2017, Week10_2017, Week11_2017, Week12_2017, Week13_2017, Week14_2017, Week15_2017, Week16_2017, Week17_2017, Week18_2017)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt=conn.prepareStatement(sql);

				pstmt.setString(1, data.get(0).toString());
				pstmt.setString(2, data.get(1).toString());
				pstmt.setFloat(3, Float.parseFloat(data.get(2).toString()));
				pstmt.setFloat(4, Float.parseFloat(data.get(3).toString()));
				pstmt.setFloat(5, Float.parseFloat(data.get(4).toString()));
				pstmt.setFloat(6, Float.parseFloat(data.get(5).toString()));
				pstmt.setFloat(7, Float.parseFloat(data.get(6).toString()));
				pstmt.setFloat(8, Float.parseFloat(data.get(7).toString()));
				pstmt.setFloat(9, Float.parseFloat(data.get(8).toString()));
				pstmt.setFloat(10, Float.parseFloat(data.get(9).toString()));
				pstmt.setFloat(11, Float.parseFloat(data.get(10).toString()));
				pstmt.setFloat(12, Float.parseFloat(data.get(11).toString()));
				pstmt.setFloat(13, Float.parseFloat(data.get(12).toString()));
				pstmt.setFloat(14, Float.parseFloat(data.get(13).toString()));
				pstmt.setFloat(15, Float.parseFloat(data.get(14).toString()));
				pstmt.setFloat(16, Float.parseFloat(data.get(15).toString()));
				pstmt.setFloat(17, Float.parseFloat(data.get(16).toString()));
				pstmt.setFloat(18, Float.parseFloat(data.get(17).toString()));
				pstmt.setFloat(19, Float.parseFloat(data.get(19).toString()));
				pstmt.setFloat(20, Float.parseFloat(data.get(19).toString()));
				pstmt.setFloat(21, Float.parseFloat(data.get(20).toString()));
				pstmt.setFloat(22, Float.parseFloat(data.get(21).toString()));
				pstmt.setFloat(23, Float.parseFloat(data.get(22).toString()));
				pstmt.setFloat(24, Float.parseFloat(data.get(23).toString()));
				pstmt.setFloat(25, Float.parseFloat(data.get(24).toString()));
				pstmt.setFloat(26, Float.parseFloat(data.get(25).toString()));
				pstmt.setFloat(27, Float.parseFloat(data.get(26).toString()));
				pstmt.setFloat(28, Float.parseFloat(data.get(27).toString()));
				pstmt.setFloat(29, Float.parseFloat(data.get(28).toString()));
				pstmt.setFloat(30, Float.parseFloat(data.get(29).toString()));
				pstmt.setFloat(31, Float.parseFloat(data.get(30).toString()));
				pstmt.setFloat(32, Float.parseFloat(data.get(31).toString()));
				pstmt.setFloat(33, Float.parseFloat(data.get(32).toString()));
				pstmt.setFloat(34, Float.parseFloat(data.get(33).toString()));
				pstmt.setFloat(35, Float.parseFloat(data.get(34).toString()));
				pstmt.setFloat(36, Float.parseFloat(data.get(35).toString()));
				pstmt.setFloat(37, Float.parseFloat(data.get(36).toString()));
				pstmt.setFloat(38, Float.parseFloat(data.get(37).toString()));

				pstmt.executeUpdate();

				String skuCode=data.get(0).toString()+"-"+data.get(1).toString();

				 System.out.println("SKU"+ skuCode);
				 String sql1="update Past_Forecast SET SKU_CODE=? where SKU='"+data.get(0).toString()+"' and Location='"+data.get(1).toString()+"'";
				 pstmt1 = conn.prepareStatement(sql1);

				 pstmt1.setString(1, skuCode);	
				 pstmt1.executeUpdate();
   		}
   		catch (SQLException e) {
				e.printStackTrace();
		 }
   		finally {
     			try {
	      			conn.close();
	      			pstmt.close();
	      			pstmt1.close();
     			}
     			catch (SQLException e) {
     				e.printStackTrace();
     		 }
     		}
   }
		if (count == 2) {
			Connection conn = null;
			PreparedStatement pstmt, pstmt1 = null;
			ArrayList dataHolder = data;
			conn = DBconnection.createDbConn();
			try {
				String sql = "insert into Past_Sales(SKU, LOCATION, Week35_2016, Week36_2016, Week37_2016, Week38_2016, Week39_2016, Week40_2016, Week41_2016, Week42_2016, Week43_2016, Week44_2016, Week45_2016, Week46_2016, Week47_2016, Week48_2016, Week49_2016, Week50_2016, Week51_2016, Week52_2016, Week01_2017, Week02_2017, Week03_2017, Week04_2017, Week05_2017, Week06_2017, Week07_2017, Week08_2017, Week09_2017, Week10_2017, Week11_2017, Week12_2017, Week13_2017, Week14_2017, Week15_2017, Week16_2017, Week17_2017, Week18_2017)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt = conn.prepareStatement(sql);

				pstmt.setString(1, data.get(0).toString());
				pstmt.setString(2, data.get(1).toString());
				pstmt.setFloat(3, Float.parseFloat(data.get(2).toString()));
				pstmt.setFloat(4, Float.parseFloat(data.get(3).toString()));
				pstmt.setFloat(5, Float.parseFloat(data.get(4).toString()));
				pstmt.setFloat(6, Float.parseFloat(data.get(5).toString()));
				pstmt.setFloat(7, Float.parseFloat(data.get(6).toString()));
				pstmt.setFloat(8, Float.parseFloat(data.get(7).toString()));
				pstmt.setFloat(9, Float.parseFloat(data.get(8).toString()));
				pstmt.setFloat(10, Float.parseFloat(data.get(9).toString()));
				pstmt.setFloat(11, Float.parseFloat(data.get(10).toString()));
				pstmt.setFloat(12, Float.parseFloat(data.get(11).toString()));
				pstmt.setFloat(13, Float.parseFloat(data.get(12).toString()));
				pstmt.setFloat(14, Float.parseFloat(data.get(13).toString()));
				pstmt.setFloat(15, Float.parseFloat(data.get(14).toString()));
				pstmt.setFloat(16, Float.parseFloat(data.get(15).toString()));
				pstmt.setFloat(17, Float.parseFloat(data.get(16).toString()));
				pstmt.setFloat(18, Float.parseFloat(data.get(17).toString()));
				pstmt.setFloat(19, Float.parseFloat(data.get(19).toString()));
				pstmt.setFloat(20, Float.parseFloat(data.get(19).toString()));
				pstmt.setFloat(21, Float.parseFloat(data.get(20).toString()));
				pstmt.setFloat(22, Float.parseFloat(data.get(21).toString()));
				pstmt.setFloat(23, Float.parseFloat(data.get(22).toString()));
				pstmt.setFloat(24, Float.parseFloat(data.get(23).toString()));
				pstmt.setFloat(25, Float.parseFloat(data.get(24).toString()));
				pstmt.setFloat(26, Float.parseFloat(data.get(25).toString()));
				pstmt.setFloat(27, Float.parseFloat(data.get(26).toString()));
				pstmt.setFloat(28, Float.parseFloat(data.get(27).toString()));
				pstmt.setFloat(29, Float.parseFloat(data.get(28).toString()));
				pstmt.setFloat(30, Float.parseFloat(data.get(29).toString()));
				pstmt.setFloat(31, Float.parseFloat(data.get(30).toString()));
				pstmt.setFloat(32, Float.parseFloat(data.get(31).toString()));
				pstmt.setFloat(33, Float.parseFloat(data.get(32).toString()));
				pstmt.setFloat(34, Float.parseFloat(data.get(33).toString()));
				pstmt.setFloat(35, Float.parseFloat(data.get(34).toString()));
				pstmt.setFloat(36, Float.parseFloat(data.get(35).toString()));
				pstmt.setFloat(37, Float.parseFloat(data.get(36).toString()));
				pstmt.setFloat(38, Float.parseFloat(data.get(37).toString()));

				pstmt.executeUpdate();

				String skuCode = data.get(0).toString() + "-" + data.get(1).toString();

				System.out.println("SKU" + skuCode);
				String sql1 = "update Past_Sales SET SKU_CODE=? where SKU='" + data.get(0).toString()
						+ "' and Location='" + data.get(1).toString() + "'";
				pstmt1 = conn.prepareStatement(sql1);

				pstmt1.setString(1, skuCode);
				pstmt1.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		if (count == 3) {
			Connection conn = null;
			PreparedStatement pstmt = null, pstmt1 = null;
			ArrayList dataHolder = data;
			conn = DBconnection.createDbConn();
			try {
				String sql = "insert into Future_Forecast(Basepack, Basepack_Desc, Material, Material_Desc, Depot, Depot_Desc, Week_201727, Week_201728, Week_201729, Week_201730, Week_201731, Week_201732, Week_201733, Week_201734, Week_201735)values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				pstmt = conn.prepareStatement(sql);

				pstmt.setString(1, data.get(0).toString());
				pstmt.setString(2, data.get(1).toString());
				pstmt.setString(3, data.get(2).toString());
				pstmt.setString(4, data.get(3).toString());
				pstmt.setString(5, data.get(4).toString());
				pstmt.setString(6, data.get(5).toString());
				pstmt.setFloat(7, Float.parseFloat(data.get(6).toString()));
				pstmt.setFloat(8, Float.parseFloat(data.get(7).toString()));
				pstmt.setFloat(9, Float.parseFloat(data.get(8).toString()));
				pstmt.setFloat(10, Float.parseFloat(data.get(9).toString()));
				pstmt.setFloat(11, Float.parseFloat(data.get(10).toString()));
				pstmt.setFloat(12, Float.parseFloat(data.get(11).toString()));
				pstmt.setFloat(13, Float.parseFloat(data.get(12).toString()));
				pstmt.setFloat(14, Float.parseFloat(data.get(13).toString()));
				pstmt.setFloat(15, Float.parseFloat(data.get(14).toString()));

				pstmt.executeUpdate();

				String skuCode = data.get(0).toString() + "-" + data.get(4).toString();
				float avgForecast = (Float.parseFloat(data.get(6).toString()) + Float.parseFloat(data.get(7).toString())
						+ Float.parseFloat(data.get(8).toString()) + Float.parseFloat(data.get(9).toString())
						+ Float.parseFloat(data.get(10).toString()) + Float.parseFloat(data.get(11).toString())
						+ Float.parseFloat(data.get(12).toString()) + Float.parseFloat(data.get(13).toString())
						+ Float.parseFloat(data.get(14).toString())) / 2;

				System.out.println("SKU:" + skuCode + "avgForecast:" + avgForecast);
				String sql1 = "update Future_Forecast SET SKU_CODE=?, Avg_Future_Forecast=? where Basepack='"
						+ data.get(0).toString() + "' and Depot='" + data.get(4).toString() + "'";
				pstmt1 = conn.prepareStatement(sql1);

				pstmt1.setString(1, skuCode);
				pstmt1.setFloat(2, avgForecast);

				pstmt1.executeUpdate();

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					conn.close();
					pstmt.close();
					pstmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
