package com.unilever.unilever.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
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

import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;

import com.unilever.unilever.controller.DBconnection;
import com.unilever.unilever.controller.DataCalculate;

//import wasdev.sample.servlet.Test.PayloadClass;

@Controller
public class UploadController {

	

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
	 * @throws IOException 
	 */
	@CrossOrigin
	@RequestMapping(value = "/uploadFile/{username}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> uploadFile(@RequestParam("uploadfile") MultipartFile uploadfile, @PathVariable String username) throws IOException, Exception {

		
		    InputStream inputStream = uploadfile.getInputStream();
			Date start = new Date();
			System.out.println(start);
			long startTime = System.nanoTime();
			String url = "jdbc:postgresql://hanno.db.elephantsql.com/vnepifvr";
			Properties props = new Properties();
			props.setProperty("user","vnepifvr");
			props.setProperty("password","n5CEh6y0w-zoORD2Hw1X_WpCT2yvy4wR");
			//props.setProperty("ssl","true");
			Connection conn;
			
			try {
				conn = DBconnection.createDbConn();
				System.out.println("connected");
				UploadController copy = new UploadController();
				PgConnection copyOperationConnection = conn.unwrap(PgConnection.class);
				System.out.println("converted");
				copy.readFirstSheet(copyOperationConnection,startTime,inputStream);
				copy.readSecondSheet(copyOperationConnection,startTime,inputStream);
				copy.readThirdSheet(copyOperationConnection,startTime,inputStream);
				copy.readFourthSheet(conn,copyOperationConnection,startTime,inputStream);
			//	copy.calculateData(conn);
				long end = System.nanoTime();
				System.out.println("Overall took "+(end-startTime)+" ns" );
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return new ResponseEntity<>(HttpStatus.OK);
			
     		}

        
	
	private void readFourthSheet(Connection conn, PgConnection copyOperationConnection, long startTime,InputStream inputStream) {
		// TODO Auto-generated method stub
		
		try {
			CopyManager copyManager = new CopyManager(copyOperationConnection);
			System.out.println("copied");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			BufferedWriter bw = new BufferedWriter(new FileWriter("C:\\Unilever\\FilefromCode\\FutureForecast.csv"));
			int row = 0;
			while((line=br.readLine())!=null) {
				//System.out.println(row);
				row++;
				String []data = line.split(",");
				float avgForecast= (Float.parseFloat(data[6].toString())+Float.parseFloat(data[7].toString())+Float.parseFloat(data[8].toString())+Float.parseFloat(data[9].toString())+Float.parseFloat(data[10].toString())+Float.parseFloat(data[11].toString())+Float.parseFloat(data[12].toString())+Float.parseFloat(data[13].toString())+Float.parseFloat(data[14].toString()))/2;
				line=line.replaceAll("%", "");
				//System.out.println(line);
				line=line+","+new Date()+",Kannan,0,0,"+data[0]+"-"+data[4]+","+avgForecast+"\n";
				bw.write(line);
			}
			bw.close();
			br.close();
			System.out.println("done");
			System.out.println(copyManager.copyIn("COPY future_forecast FROM STDIN WITH DELIMITER ','", new FileReader("C:\\Unilever\\FilefromCode\\FutureForecast.csv")));
			
			System.out.println(new Date());
			long endTime = System.nanoTime();
			System.out.println("Took "+(endTime - startTime) + " ns");
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	private void readThirdSheet(PgConnection copyOperationConnection, long startTime,InputStream inputStream) {
		// TODO Auto-generated method stub
		
		try {
			CopyManager copyManager = new CopyManager(copyOperationConnection);
			System.out.println("copied");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			BufferedWriter bw = new BufferedWriter(new FileWriter("PastSales.csv"));
			int row = 0;
			while((line=br.readLine())!=null) {
				//System.out.println(row);
				row++;
				String []data = line.split(",");
				line=line.replaceAll("%", "");
				//System.out.println(line);
				line=line+","+new Date()+",Kannan,0,0,"+data[0]+"-"+data[1]+"\n";
				bw.write(line);
			}
			bw.close();
			br.close();
			System.out.println("done");
			System.out.println(copyManager.copyIn("COPY past_sales FROM STDIN WITH DELIMITER ','", new FileReader("PastSales.csv")));
			
			System.out.println(new Date());
			long endTime = System.nanoTime();
			System.out.println("Took "+(endTime - startTime) + " ns");
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	private void readSecondSheet( PgConnection copyOperationConnection, long startTime,
			InputStream inputStream) {
		
		try {
			CopyManager copyManager = new CopyManager(copyOperationConnection);
			System.out.println("copied");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			BufferedWriter bw = new BufferedWriter(new FileWriter("PastForecast.csv"));
			int row = 0;
			while((line=br.readLine())!=null) {
				//System.out.println(row);
				row++;
				String []data = line.split(",");
				line=line.replaceAll("%", "");
				//System.out.println(line);
				line=line+","+new Date()+",Kannan,0,0,"+data[0]+"-"+data[1]+"\n";
				bw.write(line);
			}
			bw.close();
			br.close();
			System.out.println("done");
			System.out.println(copyManager.copyIn("COPY past_forecast FROM STDIN WITH DELIMITER ','", new FileReader("PastForecast.csv")));
			
			System.out.println(new Date());
			long endTime = System.nanoTime();
			System.out.println("Took "+(endTime - startTime) + " ns");
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private void readFirstSheet(PgConnection copyOperationConnection, long startTime,InputStream inputStream) {
		{
			try {
			CopyManager copyManager = new CopyManager(copyOperationConnection);
			System.out.println("copied");
		//	BufferedReader br = new BufferedReader(new FileReader("C:\\Unilever\\UPLoad CSV\\IPM Final_v4_IP.csv"));
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			String line;
			BufferedWriter bw = new BufferedWriter(new FileWriter("InputParameter.csv"));
			int row = 0;
			while((line=br.readLine())!=null) {
				//System.out.println(row);
				row++;
				String []data = line.split(",");
				//System.out.println(Float.parseFloat(data[10]));
				float avgTime =(Float.parseFloat(data[10].toString())/2)+(Float.parseFloat(data[11].toString()))+(Float.parseFloat(data[12].toString()))+(Float.parseFloat(data[13].toString()))+(Float.parseFloat(data[16].toString()));
				line=line.replaceAll("%", "");
				line=line+","+new Date()+",Kannan,0,0,"+data[2]+"-"+data[4]+","+avgTime+"\n";
				bw.write(line);
			}
			bw.close();
			br.close();
			System.out.println("done");
			System.out.println(copyManager.copyIn("COPY input_parameter FROM STDIN WITH DELIMITER ','", new FileReader("InputParameter.csv")));
			
			System.out.println(new Date());
			long endTime = System.nanoTime();
			System.out.println("Took "+(endTime - startTime) + " ns");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
		

	
		
		
		}

	}


