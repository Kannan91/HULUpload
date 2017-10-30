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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;

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
	public ResponseEntity<?> uploadFile(@RequestParam("uploadfile") MultipartFile uploadfile,
			@PathVariable String username) throws IOException, Exception {

		InputStream inputStream = uploadfile.getInputStream();
		File newFile = null;
		File ipFile = null, pfFile = null, psFile = null, ffFile = null;

		Date start = new Date();
		System.out.println(start);
		long startTime = System.nanoTime();
		String url = "jdbc:postgresql://hanno.db.elephantsql.com/vnepifvr";
		Properties props = new Properties();
		props.setProperty("user", "vnepifvr");
		props.setProperty("password", "n5CEh6y0w-zoORD2Hw1X_WpCT2yvy4wR");
		// props.setProperty("ssl","true");
		Connection conn;

		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(url, props);
			System.out.println("connected");
			UploadController copy = new UploadController();
			PgConnection copyOperationConnection = conn.unwrap(PgConnection.class);
			// System.out.println("converted");
			try {

				ZipInputStream zis = new ZipInputStream(inputStream);
				ZipEntry nextFile = zis.getNextEntry();
				byte[] buffer = new byte[1024];

				while (nextFile != null) {
					String fileName = nextFile.getName();
					newFile = new File(fileName);
					System.out.println("Unzipping to " + newFile.getAbsolutePath());
					if (newFile.getName().contains("Inputfile")) {
						ipFile = newFile;
					} else if (newFile.getName().contains("PastForecast")) {
						pfFile = newFile;
					} else if (newFile.getName().contains("PastSales")) {
						psFile = newFile;
					} else if (newFile.getName().contains("FutureForecast")) {
						ffFile = newFile;
					}

					// create directories for sub directories in zip
					new File(newFile.getParent()).mkdirs();
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
					// close this ZipEntry
					zis.closeEntry();
					nextFile = zis.getNextEntry();
				}

				zis.closeEntry();
				zis.close();
				inputStream.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			// FileInputStream fis2 = new FileInputStream(new File(newFile.ge))
			copy.readFirstSheet(copyOperationConnection, startTime, new FileInputStream(ipFile));
			copy.readSecondSheet(copyOperationConnection, startTime, new FileInputStream(pfFile));
			copy.readThirdSheet(copyOperationConnection, startTime, new FileInputStream(psFile));
			copy.readFourthSheet(copyOperationConnection, startTime, new FileInputStream(ffFile));
			/*
			 * copy.readFirstSheet(copyOperationConnection,startTime, new
			 * FileInputStream(ipFile));
			 * copy.readSecondSheet(copyOperationConnection,startTime);
			 * copy.readThirdSheet(copyOperationConnection,startTime);
			 * copy.readFourthSheet(copyOperationConnection,startTime);
			 */
			UploadController.calculateData(conn, copyOperationConnection);
			long end = System.nanoTime();
			System.out.println("Overall took " + (end - startTime) + " ns");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.OK);

	}

	private void readFourthSheet(PgConnection copyOperationConnection, long startTime, InputStream fileInputStream) {
		try {
			CopyManager copyManager = new CopyManager(copyOperationConnection);
			// System.out.println("copied");
			BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
			String line;
			File file1 = new File("FutureForecast.csv");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file1));
			int row = 0;
			while ((line = br.readLine()) != null) {
				// System.out.println(row);
				row++;
				String[] data = line.split(",");
				float avgForecast = (Float.parseFloat(data[6].toString()) + Float.parseFloat(data[7].toString())
						+ Float.parseFloat(data[8].toString()) + Float.parseFloat(data[9].toString())
						+ Float.parseFloat(data[10].toString()) + Float.parseFloat(data[11].toString())
						+ Float.parseFloat(data[12].toString()) + Float.parseFloat(data[13].toString())
						+ Float.parseFloat(data[14].toString())) / 9;
				line = line.replaceAll("%", "");
				// System.out.println(line);
				line = line + "," + new Date() + ",puser,0,0," + data[0] + "-" + data[4] + "," + avgForecast + "\n";
				bw.write(line);
			}
			bw.close();
			br.close();
			// System.out.println("done");
			System.out.println(
					copyManager.copyIn("COPY future_forecast FROM STDIN WITH DELIMITER ','", new FileReader(file1)));

			System.out.println(new Date());
			long endTime = System.nanoTime();
			System.out.println("Took " + (endTime - startTime) + " ns");
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

	private void readThirdSheet(PgConnection copyOperationConnection, long startTime, InputStream fileInputStream) {
		try {
			CopyManager copyManager = new CopyManager(copyOperationConnection);
			// System.out.println("copied");
			BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
			String line;
			File file1 = new File("PastSales.csv");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file1));
			int row = 0;
			while ((line = br.readLine()) != null) {
				// System.out.println(row);
				row++;
				String[] data = line.split(",");
				line = line.replaceAll("%", "");
				// System.out.println(line);
				line = line + "," + new Date() + ",puser,0,0," + data[0] + "-" + data[1] + "\n";
				bw.write(line);
			}
			bw.close();
			br.close();
			// System.out.println("done");
			System.out.println(
					copyManager.copyIn("COPY past_sales FROM STDIN WITH DELIMITER ','", new FileReader(file1)));

			System.out.println(new Date());
			long endTime = System.nanoTime();
			System.out.println("Took " + (endTime - startTime) + " ns");
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

	private void readSecondSheet(PgConnection copyOperationConnection, long startTime, InputStream fileInputStream) {
		try {
			CopyManager copyManager = new CopyManager(copyOperationConnection);
			// System.out.println("copied");
			BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
			String line;
			File file1 = new File("PastForecast.csv");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file1));
			int row = 0;
			while ((line = br.readLine()) != null) {
				// System.out.println(row);
				row++;
				String[] data = line.split(",");
				line = line.replaceAll("%", "");
				// System.out.println(line);
				line = line + "," + new Date() + ",puser,0,0," + data[0] + "-" + data[1] + "\n";
				bw.write(line);
			}
			bw.close();
			br.close();
			// System.out.println("done");
			System.out.println(
					copyManager.copyIn("COPY past_forecast FROM STDIN WITH DELIMITER ','", new FileReader(file1)));

			System.out.println(new Date());
			long endTime = System.nanoTime();
			System.out.println("Took " + (endTime - startTime) + " ns");
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

	private void readFirstSheet(PgConnection copyOperationConnection, long startTime, InputStream fileInputStream) {
		{
			try {
				CopyManager copyManager = new CopyManager(copyOperationConnection);
				// System.out.println("copied");
				BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream));
				String line;
				File file1 = new File("InputParameter.csv");
				BufferedWriter bw = new BufferedWriter(new FileWriter(file1));
				int row = 0;
				while ((line = br.readLine()) != null) {
					// System.out.println(row);
					row++;
					line = line.replaceAll(",,", ",0,") + "0";
					line = line.replaceAll("%", "");
					String[] data = line.split(",");
					// System.out.println(data.length);
					// System.out.println(Float.parseFloat(data[10]));
					String value1 = data[10].toString();
					String value2 = data[11].toString();
					String value5 = data[12].toString();
					String value3 = data[13].toString();
					String value4 = data[16].toString();
					float a, b, c, d, e, f, g, h, i;
					try {
						a = Float.parseFloat(value1);
					} catch (NumberFormatException e1) {
						a = 0;
					}
					try {
						b = Float.parseFloat(value2);
					} catch (NumberFormatException e1) {
						b = 0;
					}
					try {
						c = Float.parseFloat(value5);
					} catch (NumberFormatException e1) {
						c = 0;
					}
					try {
						d = Float.parseFloat(value3);
					} catch (NumberFormatException e1) {
						d = 0;
					}
					try {
						e = Float.parseFloat(value4);
					} catch (NumberFormatException e1) {
						e = 0;
					}

					float avgTime = (a / 2) + (b) + (c) + (d) + e;
					// line=line.replaceAll("%", "");
					String[] d1 = line.split(",");
					// System.out.println(line);
					// System.out.println(d1.length);
					try {
						f = Float.parseFloat(d1[9]);
					} catch (NumberFormatException e1) {
						f = 0;
					}
					try {
						g = Float.parseFloat(d1[13]);
					} catch (NumberFormatException e1) {
						g = 0;
					}
					try {
						h = Float.parseFloat(d1[17]);
					} catch (NumberFormatException e1) {
						h = 0;
					}
					try {
						i = Float.parseFloat(d1[18]);
					} catch (NumberFormatException e1) {
						i = 0;
					}

					try {
						Float k = Float.parseFloat(d1[19]);
					} catch (NumberFormatException e1) {
						d1[19] = "0";
					}

					line = d1[0] + "," + d1[1] + "," + d1[2] + "," + d1[3] + "," + d1[4] + "," + d1[5] + "," + d1[6]
							+ "," + d1[7] + "," + d1[8] + "," + f + "," + d1[10] + "," + d1[11] + "," + d1[12] + "," + g
							+ "," + d1[14] + "," + d1[15] + "," + d1[16] + "," + h + "," + i + "," + d1[19] + ","
							+ new Date() + ",puser,0,0," + data[2] + "-" + data[4] + "," + avgTime + "\n";
					bw.write(line);
				}
				bw.close();
				br.close();
				// System.out.println("done");
				System.out.println(copyManager.copyIn("COPY input_parameter FROM STDIN WITH DELIMITER ','",
						new FileReader(file1)));

				System.out.println(new Date());
				long endTime = System.nanoTime();
				System.out.println("Took " + (endTime - startTime) + " ns");
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

	public static void calculateData(Connection conn, PgConnection copyOperationConnection)
			throws MathException, SQLException {

   		//Connection conn=null;
       	PreparedStatement pstmt=null, pstmt1=null, pstmt2=null;
       	Statement st = null, st1 = null, st2 = null, st3 = null, st4=null;
        ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rs4=null;
        ArrayList arrayList = new ArrayList<String>();
        NormalDistribution d = null;
        CopyManager copyManager = new CopyManager(copyOperationConnection);
        String sql1 = "insert into BIAS_CALCULATION1 values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String sql5 = "insert into IPM_MODEL1 values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
			pstmt1=conn.prepareStatement(sql1);
	         pstmt2=conn.prepareStatement(sql5);

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        try {
   			
   			String sql3="SELECT * FROM SELECTION";
			 st1 = conn.createStatement();
	         rs1 = st1.executeQuery(sql3);
	         double serviceLevel;
	         String targetServiceLevel="", biasCorrectionFactor="", sdfeCapping = "";
	         double minCapping = 0;
	         SelectionDetails selection = new SelectionDetails();
	         
	         if (rs1.next()) {	        	 
	        	 selection.setLocation(rs1.getString(1));
	        	 selection.setCategory(rs1.getString(2));
	        	 selection.setMin_Capping(rs1.getDouble(3));
	        	 selection.setLead_Time_variability(rs1.getInt(4));
	        	 selection.setBias_Correction_factor(rs1.getString(5));
	        	 selection.setSDFE_Capping(rs1.getString(6));
	        	 selection.setSDFE_Capping_Perc(rs1.getInt(7));
	        	 selection.setTarget_ServiceLevel(rs1.getString(8));
	        	 selection.setCategory_ServiceLevel(rs1.getDouble(9));
	        	 selection.setX_value(rs1.getInt(10));
	         }
	         
   			/* Timestamp Changes - Start*/
   			String sql = "select a.SKU, a.LOCATION, a.SKU_CODE, a.Week35_2016, b.Week35_2016 Sales_Week35_2016, a.Week36_2016, b.Week36_2016 Sales_Week36_2016, a.Week37_2016, b.Week37_2016 Sales_Week37_2016, a.Week38_2016, b.Week38_2016 Sales_Week38_2016, a.Week39_2016, b.Week39_2016 Sales_Week39_2016, a.Week40_2016, b.Week40_2016 Sales_Week40_2016,"
   					+ " a.Week41_2016, b.Week41_2016 Sales_Week41_2016, a.Week42_2016, b.Week42_2016 Sales_Week42_2016, a.Week43_2016, b.Week43_2016 Sales_Week43_2016,"
   					+ " a.Week44_2016, b.Week44_2016 Sales_Week44_2016, a.Week45_2016, b.Week45_2016 Sales_Week45_2016, a.Week46_2016, b.Week46_2016 Sales_Week46_2016,"
   					+ " a.Week47_2016, b.Week47_2016 Sales_Week47_2016, a.Week48_2016, b.Week48_2016 Sales_Week48_2016, a.Week49_2016, b.Week49_2016 Sales_Week49_2016, "
   					+ "a.Week50_2016, b.Week50_2016 Sales_Week50_2016, a.Week51_2016, b.Week51_2016 Sales_Week51_2016, a.Week52_2016, b.Week52_2016 Sales_Week52_2016, a.Week01_2017,"
   					+ " b.Week01_2017 Sales_Week01_2017, a.Week02_2017, b.Week02_2017 Sales_Week02_2017, a.Week03_2017, b.Week03_2017 Sales_Week03_2017, a.Week04_2017, "
   					+ "b.Week04_2017 Sales_Week04_2017, a.Week05_2017, b.Week05_2017 Sales_Week05_2017, a.Week06_2017, b.Week06_2017 Sales_Week06_2017, a.Week07_2017, "
   					+ "b.Week07_2017 Sales_Week07_2017, a.Week08_2017, b.Week08_2017 Sales_Week08_2017, a.Week09_2017, b.Week09_2017 Sales_Week09_2017, a.Week10_2017, "
   					+ "b.Week10_2017 Sales_Week10_2017, a.Week11_2017, b.Week11_2017 Sales_Week11_2017, a.Week12_2017, b.Week12_2017 Sales_Week12_2017, a.Week13_2017, "
   					+ "b.Week13_2017 Sales_Week13_2017, a.Week14_2017, b.Week14_2017 Sales_Week14_2017, a.Week15_2017, b.Week15_2017 Sales_Week15_2017, a.Week16_2017,"
   					+ " b.Week16_2017 Sales_Week16_2017, a.Week17_2017, b.Week17_2017 Sales_Week17_2017, a.Week18_2017, b.Week18_2017 Sales_Week18_2017, a.CurrentDate, c.SKU,"
   					+ "c.Location,c.Location_Type,c.Material_Location,c.Category,c.Service_Level,c.Production_Time,c.OR_Value,c.Source,c.SKU_Classification,c.Avg_Lead_Time,"
   					+ "c.Lead_Time_Variability,c.Current_SSWeeks,c.Price,c.SKU_Name, d.avg_future_forecast from Past_Forecast a, Past_Sales b, Input_Parameter c, future_forecast d where a.SKU_CODE=b.SKU_CODE and "
   					+ "b.SKU_CODE = c.material_location and a.SKU_CODE = c.material_location and a.SKU_CODE = d.SKU_CODE and b.SKU_CODE = d.SKU_CODE and c.material_location = d.SKU_CODE";
   			st = conn.createStatement();
	        rs = st.executeQuery(sql);
	         
	        /*ResultSetMetaData metadata = rs.getMetaData();
	        int numberOfColumns = metadata.getColumnCount();*/
	       // List<Details> list = new ArrayList<Details>();
	        BufferedWriter bw = null, bw1 = null;
	        try {
				bw = new BufferedWriter(new FileWriter("bias_1.csv"));
				bw1 = new BufferedWriter(new FileWriter("ipm_model1.csv"));

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
int z=0;
	         while (rs.next()) {
	        	/* Details details = new Details();
	        	 details.setSKU(rs.getString(1));*/
	           
	        	 if(z>=3000) {
	        		 bw.close();
	        		 bw = new BufferedWriter(new FileWriter("bias_1.csv", true));
	        		 bw1.close();
	        		 bw1 = new BufferedWriter(new FileWriter("ipm_model1.csv", true));
	        	 }
	        	// System.out.println(z);
	        	 z++;
	        	 	String sku = rs.getString(1);
	  	             String location = rs.getString(2);
	  	             String skuNo = rs.getString(3);
	  				 double Fore_352016 = rs.getDouble(4);
	  	             double sale_352016 = rs.getDouble(5);
	  	             double Fore_362016 = rs.getDouble(6);
	  	             double sale_362016 = rs.getDouble(7);
	  	             double Fore_372016 = rs.getDouble(8);
	  	             double sale_372016 = rs.getDouble(9);
	  	             double Fore_382016 = rs.getDouble(10);
	  	             double sale_382016 = rs.getDouble(11);
	  	             double Fore_392016 = rs.getDouble(12);
	  	             double sale_392016 = rs.getDouble(13);
	  	             double Fore_402016 = rs.getDouble(14);
	  	             double sale_402016 = rs.getDouble(15);
	  	             double Fore_412016 = rs.getDouble(16);
	  	             double sale_412016 = rs.getDouble(17);
	  	             double Fore_422016 = rs.getDouble(18);
	  	             double sale_422016 = rs.getDouble(19);
	  	             double Fore_432016 = rs.getDouble(20);
	  	             double sale_432016 = rs.getDouble(21);
	  	             double Fore_442016 = rs.getDouble(22);
	  	             double sale_442016 = rs.getDouble(23);
	  	             double Fore_452016 = rs.getDouble(24);
	  	             double sale_452016 = rs.getDouble(25);
	  	             double Fore_462016 = rs.getDouble(26);
	  	             double sale_462016 = rs.getDouble(27);
	  	             double Fore_472016 = rs.getDouble(28);
	  	             double sale_472016 = rs.getDouble(29);
	  	             double Fore_482016 = rs.getDouble(30);
	  	             double sale_482016 = rs.getDouble(31);
	  	             double Fore_492016 = rs.getDouble(32);
	  	             double sale_492016 = rs.getDouble(33);
	  	             double Fore_502016 = rs.getDouble(34);
	  	             double sale_502016 = rs.getDouble(35);
	  	             double Fore_512016 = rs.getDouble(36);
	  	             double sale_512016 = rs.getDouble(37);
	  	             double Fore_522016 = rs.getDouble(38);
	  	             double sale_522016 = rs.getDouble(39);
	  	             double Fore_012017 = rs.getDouble(40);
	  	             double sale_012017 = rs.getDouble(41);
	  	             double Fore_022017 = rs.getDouble(42);
	  	             double sale_022017 = rs.getDouble(43);
	  	             double Fore_032017 = rs.getDouble(44);
	  	             double sale_032017 = rs.getDouble(45);
	  	             double Fore_042017 = rs.getDouble(46);
	  	             double sale_042017 = rs.getDouble(47);
	  	             double Fore_052017 = rs.getDouble(48);
	  	             double sale_052017 = rs.getDouble(49);
	  	             double Fore_062017 = rs.getDouble(50);
	  	             double sale_062017 = rs.getDouble(51);
	  	             double Fore_072017 = rs.getDouble(52);
	  	             double sale_072017 = rs.getDouble(53);
	  	             double Fore_082017 = rs.getDouble(54);
	  	             double sale_082017 = rs.getDouble(55);
	  	             double Fore_092017 = rs.getDouble(56);
	  	             double sale_092017 = rs.getDouble(57);
	  	             double Fore_102017 = rs.getDouble(58);
	  	             double sale_102017 = rs.getDouble(59);
	  	             double Fore_112017 = rs.getDouble(60);
	  	             double sale_112017 = rs.getDouble(61);
	  	             double Fore_122017 = rs.getDouble(62);
	  	             double sale_122017 = rs.getDouble(63);
	  	             double Fore_132017 = rs.getDouble(64);
	  	             double sale_132017 = rs.getDouble(65);
	  	             double Fore_142017 = rs.getDouble(66);
	  	             double sale_142017 = rs.getDouble(67);
	  	             double Fore_152017 = rs.getDouble(68);
	  	             double sale_152017 = rs.getDouble(69);
	  	             double Fore_162017 = rs.getDouble(70);
	  	             double sale_162017 = rs.getDouble(71);
	  	             double Fore_172017 = rs.getDouble(72);
	  	             double sale_172017 = rs.getDouble(73);
	  	             double Fore_182017 = rs.getDouble(74);
	  	             double sale_182017 = rs.getDouble(75);
	                 Timestamp currentDate = rs.getTimestamp(76);
	                                 
			        	 targetServiceLevel = selection.getTarget_ServiceLevel();
			        	 minCapping = selection.getMin_Capping();
			        	 biasCorrectionFactor = selection.getBias_Correction_factor();
			        	 sdfeCapping = selection.getSDFE_Capping();
			         
	                 String SKU = rs.getString(77);
		        	 String location1 = rs.getString(78);
		        	 String locationType =rs.getString(79);
		        	 String materialLocation = rs.getString(80);
		        	 String category = rs.getString(81);
		        	 
		        	 if(targetServiceLevel.equals("Category service level"))
			         {
			        	 serviceLevel =selection.getCategory_ServiceLevel(); 
			         }	
		        	 else
		        	 {
		        		 serviceLevel = rs.getInt(82);
		        	 }		
		        	 double cycleTime = rs.getFloat(83);
		        	 String cycle = String.valueOf(cycleTime);
		        	 if(cycleTime==0 || cycle.equals(""))
		        	 {
		        		 cycleTime =(float) 1.0;
		        	 }
		        	 double orValue = rs.getInt(84);
		        	 String source = rs.getString(85);
		        	 String skuClassiication = rs.getString(86);
		        	 double avgLeadTime = rs.getFloat(87);
		        	 double leadTimeVariability = rs.getFloat(88);
		        	 double currentSSWeeks = rs.getFloat(89);
		        	 double price = rs.getFloat(90);
		        	 String skuName = rs.getString(91);
		        	 double avgFutureForecast = rs.getDouble(92);
		        	 
		        	 double avgFutureForecast1 = (avgFutureForecast * 0.7);
		        	 
		        	 /*float weeklyAvgForecast = rs.getFloat(83);
		        	 float sdfe1 = rs.getFloat(84);
		        	 float sdfePerc1 = rs.getFloat(85);*/  
	             
				 double bias_352016 = Math.abs(Fore_352016-sale_352016);
	             double bias_362016 = Math.abs(Fore_362016-sale_362016);
	             double bias_372016 = Math.abs(Fore_372016-sale_372016);
	             double bias_382016 = Math.abs(Fore_382016-sale_382016);
	             double bias_392016 = Math.abs(Fore_392016-sale_392016);
	             double bias_402016 = Math.abs(Fore_402016-sale_402016);
	             double bias_412016 = Math.abs(Fore_412016-sale_412016);
	             double bias_422016 = Math.abs(Fore_422016-sale_422016);
	             double bias_432016 = Math.abs(Fore_432016-sale_432016);
	             double bias_442016 = Math.abs(Fore_442016-sale_442016);
	             double bias_452016 = Math.abs(Fore_452016-sale_452016);
	             double bias_462016 = Math.abs(Fore_462016-sale_462016);
	             double bias_472016 = Math.abs(Fore_472016-sale_472016);
	             double bias_482016 = Math.abs(Fore_482016-sale_482016);
	             double bias_492016 = Math.abs(Fore_492016-sale_492016);
	             double bias_502016 = Math.abs(Fore_502016-sale_502016);
	             double bias_512016 = Math.abs(Fore_512016-sale_512016);
	             double bias_522016 = Math.abs(Fore_522016-sale_522016);
	             double bias_012017 = Math.abs(Fore_012017-sale_012017);
	             double bias_022017 = Math.abs(Fore_022017-sale_022017);
	             double bias_032017 = Math.abs(Fore_032017-sale_032017);
	             double bias_042017 = Math.abs(Fore_042017-sale_042017);
	             double bias_052017 = Math.abs(Fore_052017-sale_052017);
	             double bias_062017 = Math.abs(Fore_062017-sale_062017);
	             double bias_072017 = Math.abs(Fore_072017-sale_072017);
	             double bias_082017 = Math.abs(Fore_082017-sale_082017);
	             double bias_092017 = Math.abs(Fore_092017-sale_092017);
	             double bias_102017 = Math.abs(Fore_102017-sale_102017);
	             double bias_112017 = Math.abs(Fore_112017-sale_112017);
	             double bias_122017 = Math.abs(Fore_122017-sale_122017);
	             double bias_132017 = Math.abs(Fore_132017-sale_132017);
	             double bias_142017 = Math.abs(Fore_142017-sale_142017);
	             double bias_152017 = Math.abs(Fore_152017-sale_152017);
	             double bias_162017 = Math.abs(Fore_162017-sale_162017);
	             double bias_172017 = Math.abs(Fore_172017-sale_172017);
	             double bias_182017 = Math.abs(Fore_182017-sale_182017);
	             
	             double biasPerc_352016 = Math.round((bias_352016/Fore_352016)*100);
	             
	             double biasPerc_362016 = Math.round((bias_362016/Fore_362016)*100);
	             double biasPerc_372016 = Math.round((bias_372016/Fore_372016)*100);
	             double biasPerc_382016 = Math.round((bias_382016/Fore_382016)*100);
	             double biasPerc_392016 = Math.round((bias_392016/Fore_392016)*100);
	             double biasPerc_402016 = Math.round((bias_402016/Fore_402016)*100);
	             double biasPerc_412016 = Math.round((bias_412016/Fore_412016)*100);
	             double biasPerc_422016 = Math.round((bias_422016/Fore_422016)*100);
	             double biasPerc_432016 = Math.round((bias_432016/Fore_432016)*100);
	             double biasPerc_442016 = Math.round((bias_442016/Fore_442016)*100);
	             double biasPerc_452016 = Math.round((bias_452016/Fore_452016)*100);
	             double biasPerc_462016 = Math.round((bias_462016/Fore_462016)*100);
	             double biasPerc_472016 = Math.round((bias_472016/Fore_472016)*100);
	             double biasPerc_482016 = Math.round((bias_482016/Fore_482016)*100);
	             double biasPerc_492016 = Math.round((bias_492016/Fore_492016)*100);
	             double biasPerc_502016 = Math.round((bias_502016/Fore_502016)*100);
	             double biasPerc_512016 = Math.round((bias_512016/Fore_512016)*100);
	             double biasPerc_522016 = Math.round((bias_522016/Fore_522016)*100);
	             double biasPerc_012017 = Math.round((bias_012017/Fore_012017)*100);
	             double biasPerc_022017 = Math.round((bias_022017/Fore_022017)*100);
	             double biasPerc_032017 = Math.round((bias_032017/Fore_032017)*100);
	             double biasPerc_042017 = Math.round((bias_042017/Fore_042017)*100);
	             double biasPerc_052017 = Math.round((bias_052017/Fore_052017)*100);
	             double biasPerc_062017 = Math.round((bias_062017/Fore_062017)*100);
	             double biasPerc_072017 = Math.round((bias_072017/Fore_072017)*100);
	             double biasPerc_082017 = Math.round((bias_082017/Fore_082017)*100);
	             double biasPerc_092017 = Math.round((bias_092017/Fore_092017)*100);
	             double biasPerc_102017 = Math.round((bias_102017/Fore_102017)*100);
	             double biasPerc_112017 = Math.round((bias_112017/Fore_112017)*100);
	             double biasPerc_122017 = Math.round((bias_122017/Fore_122017)*100);
	             double biasPerc_132017 = Math.round((bias_132017/Fore_132017)*100);
	             double biasPerc_142017 = Math.round((bias_142017/Fore_142017)*100);
	             double biasPerc_152017 = Math.round((bias_152017/Fore_152017)*100);
	             double biasPerc_162017 = Math.round((bias_162017/Fore_162017)*100);
	             double biasPerc_172017 = Math.round((bias_172017/Fore_172017)*100);
	             double biasPerc_182017 = Math.round((bias_182017/Fore_182017)*100);
	             
	             double biasPersale_352016 = (biasPerc_352016/100) * sale_352016;
	             double biasPersale_362016 = (biasPerc_362016/100) * sale_362016;
	             double biasPersale_372016 = (biasPerc_372016/100) * sale_372016;
	             double biasPersale_382016 = (biasPerc_382016/100) * sale_382016;
	             double biasPersale_392016 = (biasPerc_392016/100) * sale_392016;
	             double biasPersale_402016 = (biasPerc_402016/100) * sale_402016;
	             double biasPersale_412016 = (biasPerc_412016/100) * sale_412016;
	             double biasPersale_422016 = (biasPerc_422016/100) * sale_422016;
	             double biasPersale_432016 = (biasPerc_432016/100) * sale_432016;
	             double biasPersale_442016 = (biasPerc_442016/100) * sale_442016;
	             double biasPersale_452016 = (biasPerc_452016/100) * sale_452016;
	             double biasPersale_462016 = (biasPerc_462016/100) * sale_462016;
	             double biasPersale_472016 = (biasPerc_472016/100) * sale_472016;
	             double biasPersale_482016 = (biasPerc_482016/100) * sale_482016;
	             double biasPersale_492016 = (biasPerc_492016/100) * sale_492016;
	             double biasPersale_502016 = (biasPerc_502016/100) * sale_502016;
	             double biasPersale_512016 = (biasPerc_512016/100) * sale_512016;
	             double biasPersale_522016 = (biasPerc_522016/100) * sale_522016;
	             double biasPersale_012017 = (biasPerc_012017/100) * sale_012017;
	             double biasPersale_022017 = (biasPerc_022017/100) * sale_022017;
	             double biasPersale_032017 = (biasPerc_032017/100) * sale_032017;
	             double biasPersale_042017 = (biasPerc_042017/100) * sale_042017;
	             double biasPersale_052017 = (biasPerc_052017/100) * sale_052017;
	             double biasPersale_062017 = (biasPerc_062017/100) * sale_062017;
	             double biasPersale_072017 = (biasPerc_072017/100) * sale_072017;
	             double biasPersale_082017 = (biasPerc_082017/100) * sale_082017;
	             double biasPersale_092017 = (biasPerc_092017/100) * sale_092017;
	             double biasPersale_102017 = (biasPerc_102017/100) * sale_102017;
	             double biasPersale_112017 = (biasPerc_112017/100) * sale_112017;
	             double biasPersale_122017 = (biasPerc_122017/100) * sale_122017;
	             double biasPersale_132017 = (biasPerc_132017/100) * sale_132017;
	             double biasPersale_142017 = (biasPerc_142017/100) * sale_142017;
	             double biasPersale_152017 = (biasPerc_152017/100) * sale_152017;
	             double biasPersale_162017 = (biasPerc_162017/100) * sale_162017;
	             double biasPersale_172017 = (biasPerc_172017/100) * sale_172017;
	             double biasPersale_182017 = (biasPerc_182017/100) * sale_182017;
	             
	             double totalForecast = Fore_352016+Fore_362016+Fore_372016+Fore_382016+Fore_392016+Fore_402016+Fore_412016+Fore_422016+Fore_432016+Fore_442016+Fore_452016+Fore_462016+Fore_472016+Fore_482016+Fore_492016+Fore_502016+Fore_512016+Fore_522016+Fore_012017+Fore_022017+Fore_032017+Fore_042017+Fore_052017+Fore_062017+Fore_072017+Fore_082017+Fore_092017+Fore_102017+Fore_112017+Fore_122017+Fore_132017+Fore_142017+Fore_152017+Fore_162017+Fore_172017+Fore_182017;
	             double totalSales = sale_352016+sale_362016+sale_372016+sale_382016+sale_392016+sale_402016+sale_412016+sale_422016+sale_432016+sale_442016+sale_452016+sale_462016+sale_472016+sale_482016+sale_492016+sale_502016+sale_512016+sale_522016+sale_012017+sale_022017+sale_032017+sale_042017+sale_052017+sale_062017+sale_072017+sale_082017+sale_092017+sale_102017+sale_112017+sale_122017+sale_132017+sale_142017+sale_152017+sale_162017+sale_172017+sale_182017;
	             
	             double biasPerSale = Math.round(biasPersale_352016+biasPersale_362016+biasPersale_372016+biasPersale_382016+biasPersale_392016+biasPersale_402016+biasPersale_412016+biasPersale_422016+biasPersale_432016+biasPersale_442016+biasPersale_452016+biasPersale_462016+biasPersale_472016+biasPersale_482016+biasPersale_492016+biasPersale_502016+biasPersale_512016+biasPersale_522016+biasPersale_012017+biasPersale_022017+biasPersale_032017+biasPersale_042017+biasPersale_052017+biasPersale_062017+biasPersale_072017+biasPersale_082017+biasPersale_092017+biasPersale_102017+biasPersale_112017+biasPersale_122017+biasPersale_132017+biasPersale_142017+biasPersale_152017+biasPersale_162017+biasPersale_172017+biasPersale_182017);
	             
	             double mape=Math.round((biasPerSale/totalSales)*100);
	         
	             //double factor = Math.sqrt(10/7);
	             
	             double factor = Math.sqrt(1.42);
	             
	             double avgForecast = (totalForecast/36);
	             
	             double weeklyAvgForecast = (avgForecast * 0.7);
	             
	             double sdfePerc = Math.round(((mape/100)*1.25*factor)*100);
	             
	             double sdfe = Math.round(avgForecast * (sdfePerc/100));
	             
	             
	             
	             String line=sku+","+skuNo+","+location+","+Fore_352016+","+sale_352016+","+bias_352016+","+biasPerc_352016+","+biasPersale_352016+","+Fore_362016+","+sale_362016+","+bias_362016+","+biasPerc_362016+","+biasPersale_362016+","+Fore_372016+","+sale_372016+","+bias_372016+","+biasPerc_372016+","+biasPersale_372016+","+Fore_382016+","+sale_382016+","+bias_382016+","+biasPerc_382016+","+biasPersale_382016+","+Fore_392016+","+sale_392016+","+bias_392016+","+biasPerc_392016+","+biasPersale_392016+","+Fore_402016+","+sale_402016+","+bias_402016+","+biasPerc_402016+","+biasPersale_402016+","+Fore_412016+","+sale_412016+","+bias_412016+","+biasPerc_412016+","+biasPersale_412016+","+Fore_422016+","+sale_422016+","+bias_422016+","+biasPerc_422016+","+biasPersale_422016+","+Fore_432016+","+sale_432016+","+bias_432016+","+biasPerc_432016+","+biasPersale_432016+","+Fore_442016+","+sale_442016+","+bias_442016+","+biasPerc_442016+","+biasPersale_442016+","+Fore_452016+","+sale_452016+","+bias_452016+","+biasPerc_452016+","+biasPersale_452016+","+Fore_462016+","+sale_462016+","+bias_462016+","+biasPerc_462016+","+biasPersale_462016+","+Fore_472016+","+sale_472016+","+bias_472016+","+biasPerc_472016+","+biasPersale_472016+","+Fore_482016+","+sale_482016+","+bias_482016+","+biasPerc_482016+","+biasPersale_482016+","+Fore_492016+","+sale_492016+","+bias_492016+","+biasPerc_492016+","+biasPersale_492016+","+Fore_502016+","+sale_502016+","+bias_502016+","+biasPerc_502016+","+biasPersale_502016+","+Fore_512016+","+sale_512016+","+bias_512016+","+biasPerc_512016+","+biasPersale_512016+","+Fore_522016+","+sale_522016+","+bias_522016+","+biasPerc_522016+","+biasPersale_522016+","+Fore_012017+","+sale_012017+","+bias_012017+","+biasPerc_012017+","+biasPersale_012017+","+Fore_022017+","+sale_022017+","+bias_022017+","+biasPerc_022017+","+biasPersale_022017+","+Fore_032017+","+sale_032017+","+bias_032017+","+biasPerc_032017+","+biasPersale_032017+","+Fore_042017+","+sale_042017+","+bias_042017+","+biasPerc_042017+","+biasPersale_042017+","+Fore_052017+","+sale_052017+","+bias_052017+","+biasPerc_052017+","+biasPersale_052017+","+Fore_062017+","+sale_062017+","+bias_062017+","+biasPerc_062017+","+biasPersale_062017+","+Fore_072017+","+sale_072017+","+bias_072017+","+biasPerc_072017+","+biasPersale_072017+","+Fore_082017+","+sale_082017+","+bias_082017+","+biasPerc_082017+","+biasPersale_082017+","+Fore_092017+","+sale_092017+","+bias_092017+","+biasPerc_092017+","+biasPersale_092017+","+Fore_102017+","+sale_102017+","+bias_102017+","+biasPerc_102017+","+biasPersale_102017+","+Fore_112017+","+sale_112017+","+bias_112017+","+biasPerc_112017+","+biasPersale_112017+","+Fore_122017+","+sale_122017+","+bias_122017+","+biasPerc_122017+","+biasPersale_122017+","+Fore_132017+","+sale_132017+","+bias_132017+","+biasPerc_132017+","+biasPersale_132017+","+Fore_142017+","+sale_142017+","+bias_142017+","+biasPerc_142017+","+biasPersale_142017+","+Fore_152017+","+sale_152017+","+bias_152017+","+biasPerc_152017+","+biasPersale_152017+","+Fore_162017+","+sale_162017+","+bias_162017+","+biasPerc_162017+","+biasPersale_162017+","+Fore_172017+","+sale_172017+","+bias_172017+","+biasPerc_172017+","+biasPersale_172017+","+Fore_182017+","+sale_182017+","+bias_182017+","+biasPerc_182017+","+biasPersale_182017+","+mape+","+sdfe+","+sdfePerc+","+totalForecast+","+totalSales+","+factor+","+avgForecast+","+36+","+weeklyAvgForecast+","+currentDate;
	          //   System.out.println("sku :"+sku+"skuNo :"+skuNo+"location :"+location+"Fore_352016 :"+Fore_352016+"sale_352016 :"+sale_352016+"bias_352016 :"+bias_352016+"biasPerc_352016 :"+biasPerc_352016+"biasPersale_352016 :"+biasPersale_352016+"Fore_362016 :"+Fore_362016+"sale_362016 :"+sale_362016+"bias_362016 :"+bias_362016+"biasPerc_362016 :"+biasPerc_362016+"biasPersale_362016 :"+biasPersale_362016+"Fore_372016 :"+Fore_372016+"sale_372016 :"+sale_372016+"bias_372016 :"+bias_372016+"biasPerc_372016 :"+biasPerc_372016+"biasPersale_372016 :"+biasPersale_372016+"Fore_382016 :"+Fore_382016+"sale_382016 :"+sale_382016+"bias_382016 :"+bias_382016+"biasPerc_382016 :"+biasPerc_382016+"biasPersale_382016 :"+biasPersale_382016+"Fore_392016 :"+Fore_392016+"sale_392016 :"+sale_392016+"bias_392016 :"+bias_392016+"biasPerc_392016 :"+biasPerc_392016+"biasPersale_392016 :"+biasPersale_392016+"Fore_402016 :"+Fore_402016+"sale_402016 :"+sale_402016+"bias_402016 :"+bias_402016+"biasPerc_402016 :"+biasPerc_402016+"biasPersale_402016 :"+biasPersale_402016+"Fore_412016 :"+Fore_412016+"sale_412016 :"+sale_412016+"bias_412016 :"+bias_412016+"biasPerc_412016 :"+biasPerc_412016+"biasPersale_412016 :"+biasPersale_412016+"Fore_422016 :"+Fore_422016+"sale_422016 :"+sale_422016+"bias_422016 :"+bias_422016+"biasPerc_422016 :"+biasPerc_422016+"biasPersale_422016 :"+biasPersale_422016+"Fore_432016 :"+Fore_432016+"sale_432016 :"+sale_432016+"bias_432016 :"+bias_432016+"biasPerc_432016 :"+biasPerc_432016+"biasPersale_432016 :"+biasPersale_432016+"Fore_442016 :"+Fore_442016+"sale_442016 :"+sale_442016+"bias_442016 :"+bias_442016+"biasPerc_442016 :"+biasPerc_442016+"biasPersale_442016 :"+biasPersale_442016+"Fore_452016 :"+Fore_452016+"sale_452016 :"+sale_452016+"bias_452016 :"+bias_452016+"biasPerc_452016 :"+biasPerc_452016+"biasPersale_452016 :"+biasPersale_452016+"Fore_462016 :"+Fore_462016+"sale_462016 :"+sale_462016+"bias_462016 :"+bias_462016+"biasPerc_462016 :"+biasPerc_462016+"biasPersale_462016 :"+biasPersale_462016+"Fore_472016 :"+Fore_472016+"sale_472016 :"+sale_472016+"bias_472016 :"+bias_472016+"biasPerc_472016 :"+biasPerc_472016+"biasPersale_472016 :"+biasPersale_472016+"Fore_482016 :"+Fore_482016+"sale_482016 :"+sale_482016+"bias_482016 :"+bias_482016+"biasPerc_482016 :"+biasPerc_482016+"biasPersale_482016 :"+biasPersale_482016+"Fore_492016 :"+Fore_492016+"sale_492016 :"+sale_492016+"bias_492016 :"+bias_492016+"biasPerc_492016 :"+biasPerc_492016+"biasPersale_492016 :"+biasPersale_492016+"Fore_502016 :"+Fore_502016+"sale_502016 :"+sale_502016+"bias_502016 :"+bias_502016+"biasPerc_502016 :"+biasPerc_502016+"biasPersale_502016 :"+biasPersale_502016+"Fore_512016 :"+Fore_512016+"sale_512016 :"+sale_512016+"bias_512016 :"+bias_512016+"biasPerc_512016 :"+biasPerc_512016+"biasPersale_512016 :"+biasPersale_512016+"Fore_522016 :"+Fore_522016+"sale_522016 :"+sale_522016+"bias_522016 :"+bias_522016+"biasPerc_522016 :"+biasPerc_522016+"biasPersale_522016 :"+biasPersale_522016+"Fore_012017 :"+Fore_012017+"sale_012017 :"+sale_012017+"bias_012017 :"+bias_012017+"biasPerc_012017 :"+biasPerc_012017+"biasPersale_012017 :"+biasPersale_012017+"Fore_022017 :"+Fore_022017+"sale_022017 :"+sale_022017+"bias_022017 :"+bias_022017+"biasPerc_022017 :"+biasPerc_022017+"biasPersale_022017 :"+biasPersale_022017+"Fore_032017 :"+Fore_032017+"sale_032017 :"+sale_032017+"bias_032017 :"+bias_032017+"biasPerc_032017 :"+biasPerc_032017+"biasPersale_032017 :"+biasPersale_032017+"Fore_042017 :"+Fore_042017+"sale_042017 :"+sale_042017+"bias_042017 :"+bias_042017+"biasPerc_042017 :"+biasPerc_042017+"biasPersale_042017 :"+biasPersale_042017+"Fore_052017 :"+Fore_052017+"sale_052017 :"+sale_052017+"bias_052017 :"+bias_052017+"biasPerc_052017 :"+biasPerc_052017+"biasPersale_052017 :"+biasPersale_052017+"Fore_062017 :"+Fore_062017+"sale_062017 :"+sale_062017+"bias_062017 :"+bias_062017+"biasPerc_062017 :"+biasPerc_062017+"biasPersale_062017 :"+biasPersale_062017+"Fore_072017 :"+Fore_072017+"sale_072017 :"+sale_072017+"bias_072017 :"+bias_072017+"biasPerc_072017 :"+biasPerc_072017+"biasPersale_072017 :"+biasPersale_072017+"Fore_082017 :"+Fore_082017+"sale_082017 :"+sale_082017+"bias_082017 :"+bias_082017+"biasPerc_082017 :"+biasPerc_082017+"biasPersale_082017 :"+biasPersale_082017+"Fore_092017 :"+Fore_092017+"sale_092017 :"+sale_092017+"bias_092017 :"+bias_092017+"biasPerc_092017 :"+biasPerc_092017+"biasPersale_092017 :"+biasPersale_092017+"Fore_102017 :"+Fore_102017+"sale_102017 :"+sale_102017+"bias_102017 :"+bias_102017+"biasPerc_102017 :"+biasPerc_102017+"biasPersale_102017 :"+biasPersale_102017+"Fore_112017 :"+Fore_112017+"sale_112017 :"+sale_112017+"bias_112017 :"+bias_112017+"biasPerc_112017 :"+biasPerc_112017+"biasPersale_112017 :"+biasPersale_112017+"Fore_122017 :"+Fore_122017+"sale_122017 :"+sale_122017+"bias_122017 :"+bias_122017+"biasPerc_122017 :"+biasPerc_122017+"biasPersale_122017 :"+biasPersale_122017+"Fore_132017 :"+Fore_132017+"sale_132017 :"+sale_132017+"bias_132017 :"+bias_132017+"biasPerc_132017 :"+biasPerc_132017+"biasPersale_132017 :"+biasPersale_132017+"Fore_142017 :"+Fore_142017+"sale_142017 :"+sale_142017+"bias_142017 :"+bias_142017+"biasPerc_142017 :"+biasPerc_142017+"biasPersale_142017 :"+biasPersale_142017+"Fore_152017 :"+Fore_152017+"sale_152017 :"+sale_152017+"bias_152017 :"+bias_152017+"biasPerc_152017 :"+biasPerc_152017+"biasPersale_152017 :"+biasPersale_152017+"Fore_162017 :"+Fore_162017+"sale_162017 :"+sale_162017+"bias_162017 :"+bias_162017+"biasPerc_162017 :"+biasPerc_162017+"biasPersale_162017 :"+biasPersale_162017+"Fore_172017 :"+Fore_172017+"sale_172017 :"+sale_172017+"bias_172017 :"+bias_172017+"biasPerc_172017 :"+biasPerc_172017+"biasPersale_172017 :"+biasPersale_172017+"Fore_182017 :"+Fore_182017+"sale_182017 :"+sale_182017+"bias_182017 :"+bias_182017+"biasPerc_182017 :"+biasPerc_182017+"biasPersale_182017 :"+biasPersale_182017+"mape :"+mape+"sdfe :"+sdfe+"sdfePerc :"+sdfePerc+"totalForecast :"+totalForecast+"totalSales :"+totalSales+"factor :"+factor+"avgForecast :"+avgForecast+"Count :"+36+"weeklyAvgForecast :"+weeklyAvgForecast+"currentDate :"+currentDate);
	            // System.out.println("totalForecast :" +totalForecast + "avgForecast :" + avgForecast + "weeklyAvgForecast : " + weeklyAvgForecast + "factor :" + factor + "totalForecast:" + totalForecast +"totalSales:" + totalSales + "biasPerSale :" + biasPerSale + "mape :" + mape + "sdfePerc :" + sdfePerc + " sdfe :"+ sdfe);
	             
	             //String sql2 = "update BIAS_CALCULATION SET Bias_Calc_WEEK_352016=?, Bias_Perc_WEEK_352016=?, Bias_Per_Sale_WEEK_352016=?, Bias_Calc_WEEK_362016=?, Bias_Perc_WEEK_362016=?, Bias_Per_Sale_WEEK_362016=?, Bias_Calc_WEEK_372016=?, Bias_Perc_WEEK_372016=?, Bias_Per_Sale_WEEK_372016=?, Bias_Calc_WEEK_382016=?, Bias_Perc_WEEK_382016=?, Bias_Per_Sale_WEEK_382016=?, Bias_Calc_WEEK_392016=?, Bias_Perc_WEEK_392016=?, Bias_Per_Sale_WEEK_392016=?, Bias_Calc_WEEK_402016=?, Bias_Perc_WEEK_402016=?, Bias_Per_Sale_WEEK_402016=?, Bias_Calc_WEEK_412016=?, Bias_Perc_WEEK_412016=?, Bias_Per_Sale_WEEK_412016=?, Bias_Calc_WEEK_422016=?, Bias_Perc_WEEK_422016=?, Bias_Per_Sale_WEEK_422016=?,Bias_Calc_WEEK_432016=?, Bias_Perc_WEEK_432016=?, Bias_Per_Sale_WEEK_432016=?, Bias_Calc_WEEK_442016=?, Bias_Perc_WEEK_442016=?, Bias_Per_Sale_WEEK_442016=?, Bias_Calc_WEEK_452016=?, Bias_Perc_WEEK_452016=?, Bias_Per_Sale_WEEK_452016=?,Bias_Calc_WEEK_462016=?, Bias_Perc_WEEK_462016=?, Bias_Per_Sale_WEEK_462016=?,Bias_Calc_WEEK_472016=?, Bias_Perc_WEEK_472016=?, Bias_Per_Sale_WEEK_472016=?,Bias_Calc_WEEK_482016=?, Bias_Perc_WEEK_482016=?, Bias_Per_Sale_WEEK_482016=?,Bias_Calc_WEEK_492016=?, Bias_Perc_WEEK_492016=?, Bias_Per_Sale_WEEK_492016=?,Bias_Calc_WEEK_502016=?, Bias_Perc_WEEK_502016=?, Bias_Per_Sale_WEEK_502016=?,Bias_Calc_WEEK_512016=?, Bias_Perc_WEEK_512016=?, Bias_Per_Sale_WEEK_512016=?,Bias_Calc_WEEK_522016=?, Bias_Perc_WEEK_522016=?, Bias_Per_Sale_WEEK_522016=?,Bias_Calc_WEEK_012017=?, Bias_Perc_WEEK_012017=?, Bias_Per_Sale_WEEK_012017=?,Bias_Calc_WEEK_022017=?, Bias_Perc_WEEK_022017=?, Bias_Per_Sale_WEEK_022017=?,Bias_Calc_WEEK_032017=?, Bias_Perc_WEEK_032017=?, Bias_Per_Sale_WEEK_032017=?,Bias_Calc_WEEK_042017=?, Bias_Perc_WEEK_042017=?, Bias_Per_Sale_WEEK_042017=?,Bias_Calc_WEEK_052017=?, Bias_Perc_WEEK_052017=?, Bias_Per_Sale_WEEK_052017=?,Bias_Calc_WEEK_062017=?, Bias_Perc_WEEK_062017=?, Bias_Per_Sale_WEEK_062017=?,Bias_Calc_WEEK_072017=?, Bias_Perc_WEEK_072017=?, Bias_Per_Sale_WEEK_072017=?,Bias_Calc_WEEK_082017=?, Bias_Perc_WEEK_082017=?, Bias_Per_Sale_WEEK_082017=?,Bias_Calc_WEEK_092017=?, Bias_Perc_WEEK_092017=?, Bias_Per_Sale_WEEK_092017=?,Bias_Calc_WEEK_102017=?, Bias_Perc_WEEK_102017=?, Bias_Per_Sale_WEEK_102017=?,Bias_Calc_WEEK_112017=?, Bias_Perc_WEEK_112017=?, Bias_Per_Sale_WEEK_112017=?,Bias_Calc_WEEK_122017=?, Bias_Perc_WEEK_122017=?, Bias_Per_Sale_WEEK_122017=?,Bias_Calc_WEEK_132017=?, Bias_Perc_WEEK_132017=?, Bias_Per_Sale_WEEK_132017=?,Bias_Calc_WEEK_142017=?, Bias_Perc_WEEK_142017=?, Bias_Per_Sale_WEEK_142017=?,Bias_Calc_WEEK_152017=?, Bias_Perc_WEEK_152017=?, Bias_Per_Sale_WEEK_152017=?,Bias_Calc_WEEK_162017=?, Bias_Perc_WEEK_162017=?, Bias_Per_Sale_WEEK_162017=?,Bias_Calc_WEEK_172017=?, Bias_Perc_WEEK_172017=?, Bias_Per_Sale_WEEK_172017=?,Bias_Calc_WEEK_182017=?, Bias_Perc_WEEK_182017=?, Bias_Per_Sale_WEEK_182017=?,Weighted_Mape=?, SDFE=?, SDFE_Perc=?, Total_Past_Forecast=?, Total_Past_sales=?, Factor=?, Avg_Forecast=?, Count=?, Weekly_Avg_Forecast=? where SKU_NO='"+skuNo+"'";
	/*             
	             
	             pstmt1.setString(1, sku);
	             pstmt1.setString(2, skuNo);
	             pstmt1.setString(3, location);
	             pstmt1.setDouble(4, Fore_352016);
	             pstmt1.setDouble(5, sale_352016);
	             pstmt1.setDouble(6, bias_352016);
	             pstmt1.setDouble(7, biasPerc_352016);
	             pstmt1.setDouble(8, biasPersale_352016);
	             pstmt1.setDouble(9, Fore_362016);
	             pstmt1.setDouble(10, sale_362016);
	             pstmt1.setDouble(11, bias_362016);
	             pstmt1.setDouble(12, biasPerc_362016);
	             pstmt1.setDouble(13, biasPersale_362016);
	             pstmt1.setDouble(14, Fore_372016);
	             pstmt1.setDouble(15, sale_372016);
	             pstmt1.setDouble(16, bias_372016);
	             pstmt1.setDouble(17, biasPerc_372016);
	             pstmt1.setDouble(18, biasPersale_372016);
	             pstmt1.setDouble(19, Fore_382016);
	             pstmt1.setDouble(20, sale_382016);
	             pstmt1.setDouble(21, bias_382016);
	             pstmt1.setDouble(22, biasPerc_382016);
	             pstmt1.setDouble(23, biasPersale_382016);
	             pstmt1.setDouble(24, Fore_392016);
	             pstmt1.setDouble(25, sale_392016);
	             pstmt1.setDouble(26, bias_392016);
	             pstmt1.setDouble(27, biasPerc_392016);
	             pstmt1.setDouble(28, biasPersale_392016);
	             pstmt1.setDouble(29, Fore_402016);
	             pstmt1.setDouble(30, sale_402016);
	             pstmt1.setDouble(31, bias_402016);
	             pstmt1.setDouble(32, biasPerc_402016);
	             pstmt1.setDouble(33, biasPersale_402016);
	             pstmt1.setDouble(34, Fore_412016);
	             pstmt1.setDouble(35, sale_412016);
	             pstmt1.setDouble(36, bias_412016);
	             pstmt1.setDouble(37, biasPerc_412016);
	             pstmt1.setDouble(38, biasPersale_412016);
	             pstmt1.setDouble(39, Fore_422016);
	             pstmt1.setDouble(40, sale_422016);
	             pstmt1.setDouble(41, bias_422016);
	             pstmt1.setDouble(42, biasPerc_422016);
	             pstmt1.setDouble(43, biasPersale_422016);
	             pstmt1.setDouble(44, Fore_432016);
	             pstmt1.setDouble(45, sale_432016);
	             pstmt1.setDouble(46, bias_432016);
	             pstmt1.setDouble(47, biasPerc_432016);
	             pstmt1.setDouble(48, biasPersale_432016);
	             pstmt1.setDouble(49, Fore_442016);
	             pstmt1.setDouble(50, sale_442016);
	             pstmt1.setDouble(51, bias_442016);
	             pstmt1.setDouble(52, biasPerc_442016);
	             pstmt1.setDouble(53, biasPersale_442016);
	             pstmt1.setDouble(54, Fore_452016);
	             pstmt1.setDouble(55, sale_452016);
	             pstmt1.setDouble(56, bias_452016);
	             pstmt1.setDouble(57, biasPerc_452016);
	             pstmt1.setDouble(58, biasPersale_452016);
	             pstmt1.setDouble(59, Fore_462016);
	             pstmt1.setDouble(60, sale_462016);
	             pstmt1.setDouble(61, bias_462016);
	             pstmt1.setDouble(62, biasPerc_462016);
	             pstmt1.setDouble(63, biasPersale_462016);
	             pstmt1.setDouble(64, Fore_472016);
	             pstmt1.setDouble(65, sale_472016);
	             pstmt1.setDouble(66, bias_472016);
	             pstmt1.setDouble(67, biasPerc_472016);
	             pstmt1.setDouble(68, biasPersale_472016);
	             pstmt1.setDouble(69, Fore_482016);
	             pstmt1.setDouble(70, sale_482016);
	             pstmt1.setDouble(71, bias_482016);
	             pstmt1.setDouble(72, biasPerc_482016);
	             pstmt1.setDouble(73, biasPersale_482016);
	             pstmt1.setDouble(74, Fore_492016);
	             pstmt1.setDouble(75, sale_492016);
	             pstmt1.setDouble(76, bias_492016);
	             pstmt1.setDouble(77, biasPerc_492016);
	             pstmt1.setDouble(78, biasPersale_492016);
	             pstmt1.setDouble(79, Fore_502016);
	             pstmt1.setDouble(80, sale_502016);
	             pstmt1.setDouble(81, bias_502016);
	             pstmt1.setDouble(82, biasPerc_502016);
	             pstmt1.setDouble(83, biasPersale_502016);
	             pstmt1.setDouble(84, Fore_512016);
	             pstmt1.setDouble(85, sale_512016);
	             pstmt1.setDouble(86, bias_512016);
	             pstmt1.setDouble(87, biasPerc_512016);
	             pstmt1.setDouble(88, biasPersale_512016);
	             pstmt1.setDouble(89, Fore_522016);
	             pstmt1.setDouble(90, sale_522016);
	             pstmt1.setDouble(91, bias_522016);
	             pstmt1.setDouble(92, biasPerc_522016);
	             pstmt1.setDouble(93, biasPersale_522016);
	             pstmt1.setDouble(94, Fore_012017);
	             pstmt1.setDouble(95, sale_012017);
	             pstmt1.setDouble(96, bias_012017);
	             pstmt1.setDouble(97, biasPerc_012017);
	             pstmt1.setDouble(98, biasPersale_012017);
	             pstmt1.setDouble(99, Fore_022017);
	             pstmt1.setDouble(100, sale_022017);
	             pstmt1.setDouble(101, bias_022017);
	             pstmt1.setDouble(102, biasPerc_022017);
	             pstmt1.setDouble(103, biasPersale_022017);
	             pstmt1.setDouble(104, Fore_032017);
	             pstmt1.setDouble(105, sale_032017);
	             pstmt1.setDouble(106, bias_032017);
	             pstmt1.setDouble(107, biasPerc_032017);
	             pstmt1.setDouble(108, biasPersale_032017);
	             pstmt1.setDouble(109, Fore_042017);
	             pstmt1.setDouble(110, sale_042017);
	             pstmt1.setDouble(111, bias_042017);
	             pstmt1.setDouble(112, biasPerc_042017);
	             pstmt1.setDouble(113, biasPersale_042017);
	             pstmt1.setDouble(114, Fore_052017);
	             pstmt1.setDouble(115, sale_052017);
	             pstmt1.setDouble(116, bias_052017);
	             pstmt1.setDouble(117, biasPerc_052017);
	             pstmt1.setDouble(118, biasPersale_052017);
	             pstmt1.setDouble(119, Fore_062017);
	             pstmt1.setDouble(120, sale_062017);
	             pstmt1.setDouble(121, bias_062017);
	             pstmt1.setDouble(122, biasPerc_062017);
	             pstmt1.setDouble(123, biasPersale_062017);
	             pstmt1.setDouble(124, Fore_072017);
	             pstmt1.setDouble(125, sale_072017);
	             pstmt1.setDouble(126, bias_072017);
	             pstmt1.setDouble(127, biasPerc_072017);
	             pstmt1.setDouble(128, biasPersale_072017);
	             pstmt1.setDouble(129, Fore_082017);
	             pstmt1.setDouble(130, sale_082017);
	             pstmt1.setDouble(131, bias_082017);
	             pstmt1.setDouble(132, biasPerc_082017);
	             pstmt1.setDouble(133, biasPersale_082017);
	             pstmt1.setDouble(134, Fore_092017);
	             pstmt1.setDouble(135, sale_092017);
	             pstmt1.setDouble(136, bias_092017);
	             pstmt1.setDouble(137, biasPerc_092017);
	             pstmt1.setDouble(138, biasPersale_092017);
	             pstmt1.setDouble(139, Fore_102017);
	             pstmt1.setDouble(140, sale_102017);
	             pstmt1.setDouble(141, bias_102017);
	             pstmt1.setDouble(142, biasPerc_102017);
	             pstmt1.setDouble(143, biasPersale_102017);
	             pstmt1.setDouble(144, Fore_112017);
	             pstmt1.setDouble(145, sale_112017);
	             pstmt1.setDouble(146, bias_112017);
	             pstmt1.setDouble(147, biasPerc_112017);
	             pstmt1.setDouble(148, biasPersale_112017);
	             pstmt1.setDouble(149, Fore_122017);
	             pstmt1.setDouble(150, sale_122017);
	             pstmt1.setDouble(151, bias_122017);
	             pstmt1.setDouble(152, biasPerc_122017);
	             pstmt1.setDouble(153, biasPersale_122017);
	             pstmt1.setDouble(154, Fore_132017);
	             pstmt1.setDouble(155, sale_132017);
	             pstmt1.setDouble(156, bias_132017);
	             pstmt1.setDouble(157, biasPerc_132017);
	             pstmt1.setDouble(158, biasPersale_132017);
	             pstmt1.setDouble(159, Fore_142017);
	             pstmt1.setDouble(160, sale_142017);
	             pstmt1.setDouble(161, bias_142017);
	             pstmt1.setDouble(162, biasPerc_142017);
	             pstmt1.setDouble(163, biasPersale_142017);
	             pstmt1.setDouble(164, Fore_152017);
	             pstmt1.setDouble(165, sale_152017);
	             pstmt1.setDouble(166, bias_152017);
	             pstmt1.setDouble(167, biasPerc_152017);
	             pstmt1.setDouble(168, biasPersale_152017);
	             pstmt1.setDouble(169, Fore_162017);
	             pstmt1.setDouble(170, sale_162017);
	             pstmt1.setDouble(171, bias_162017);
	             pstmt1.setDouble(172, biasPerc_162017);
	             pstmt1.setDouble(173, biasPersale_162017);
	             pstmt1.setDouble(174, Fore_172017);
	             pstmt1.setDouble(175, sale_172017);
	             pstmt1.setDouble(176, bias_172017);
	             pstmt1.setDouble(177, biasPerc_172017);
	             pstmt1.setDouble(178, biasPersale_172017);
	             pstmt1.setDouble(179, Fore_182017);
	             pstmt1.setDouble(180, sale_182017);
	             pstmt1.setDouble(181, bias_182017);
	             pstmt1.setDouble(182, biasPerc_182017);
	             pstmt1.setDouble(183, biasPersale_182017);
	             pstmt1.setDouble(184, mape);
	             pstmt1.setDouble(185, sdfe);
	             pstmt1.setDouble(186, sdfePerc);
	             pstmt1.setDouble(187, totalForecast);
	             pstmt1.setDouble(188, totalSales);
	             pstmt1.setDouble(189, factor);
	             pstmt1.setDouble(190, avgForecast);
	             pstmt1.setInt(191, 36);
	             pstmt1.setDouble(192, weeklyAvgForecast);
	             pstmt1.setTimestamp(193, currentDate);
				 
				 pstmt1.executeUpdate();
				 */
				 /* IPM MODEL CALCULATIONS */
				 
				 //System.out.println("IPM Calculations Started");			 
				 
	             try {
					bw.write(line+",Puser,0,0\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	             
	             double biasPerc = ((biasPerc_352016)+(biasPerc_362016)+(biasPerc_372016)+(biasPerc_382016)+(biasPerc_392016)+(biasPerc_402016)+(biasPerc_412016)+(biasPerc_422016)+(biasPerc_432016)+(biasPerc_442016)+(biasPerc_452016)+(biasPerc_462016)+(biasPerc_472016)+(biasPerc_482016)+(biasPerc_492016)+(biasPerc_502016)+(biasPerc_512016)+(biasPerc_522016)+(biasPerc_012017)+(biasPerc_022017)+(biasPerc_032017)+(biasPerc_042017)+(biasPerc_052017)+(biasPerc_062017)+(biasPerc_072017)+(biasPerc_082017)+(biasPerc_092017)+(biasPerc_102017)+(biasPerc_112017)+(biasPerc_122017)+(biasPerc_132017)+(biasPerc_142017)+(biasPerc_152017)+(biasPerc_162017)+(biasPerc_172017)+(biasPerc_182017))/36; 
		         double adjustedSdfe;
		         double finalSdfe = 0;
		         if(((sdfePerc)-(biasPerc)) < 0)
		         {
		        	 adjustedSdfe = 0.0;
		         }
		         else
		         {
		        	 adjustedSdfe = ((sdfePerc)-(biasPerc));
		         }
		         
		         if(biasCorrectionFactor.equals("ON") && sdfeCapping.equals("ON"))
		         {
		        	 if(adjustedSdfe<30)
		        	 {
		        		 finalSdfe = adjustedSdfe;
		        	 }
		        	 else
		        	 {
		        		 finalSdfe = 30.0;
		        	 }
		         }
		         if(biasCorrectionFactor.equals("ON") && sdfeCapping.equals("OFF"))
		         {
		        	 finalSdfe = adjustedSdfe;
		         }
		         if(biasCorrectionFactor.equals("OFF") && sdfeCapping.equals("OFF"))
		         {
		        	 finalSdfe = sdfePerc;
		         }
		         if(biasCorrectionFactor.equals("ON") && sdfeCapping.equals("ON"))
		         {
		        	 if(sdfe<30)
		        	 {
		        		 finalSdfe = sdfePerc;
		        	 }
		        	 else
		        	 {
		        		 finalSdfe = 30.0;
		        	 }
		         }
		         
		         	 double sdfe1 = Math.round(avgFutureForecast1 * (finalSdfe/100));
		             double lotSize = cycleTime * avgFutureForecast1;
			         double SdVariability = Math.sqrt((Math.pow(sdfe1, 2) * avgLeadTime) + Math.pow((avgFutureForecast1 * leadTimeVariability),2) + (Math.pow(avgFutureForecast1,2)) * (Math.pow(1.25, 2)) * (Math.pow(cycleTime * (1-(orValue/100)),2)));
			        // System.out.println("sdfe1 :" + sdfe1 + "avgLeadTime :" + avgLeadTime + "avgFutureForecast1 :" + avgFutureForecast1 + "leadTimeVariability :" + leadTimeVariability + "cycleTime :" + cycleTime + "orValue :" + orValue);
				     double cFactorSales = 0.92 + Math.log(avgFutureForecast1 * cycleTime * ((1-(serviceLevel/100))/SdVariability));
			         double kFactorSales = -1.19 + Math.sqrt((Math.pow(1.19, 2) - 4*0.37*cFactorSales)/(2*0.37));
			         //double cycleServiceLevel = d.cumulativeProbability(kFactorSales);
			         long cycleServiceLevel =0;
			         double modelSafetyStock=0;
			         
			        	 
			         if(avgFutureForecast1==0)
			         {
			        	 modelSafetyStock=0;
			         }
			         else if((kFactorSales * SdVariability)>0)
			         {
			        	 modelSafetyStock = kFactorSales * SdVariability;
			         }
			         double safetyStockWeeks;
			         if(modelSafetyStock==0)
			         {
			        	 safetyStockWeeks=0;
			         }
			         else if ((modelSafetyStock/avgFutureForecast1)>13)
			         {
			        	 safetyStockWeeks =13;
			         }
			         else
			         {
			        	 safetyStockWeeks = modelSafetyStock/avgFutureForecast1;
			         }
			         long safetyStockDays = Math.round(safetyStockWeeks*7);
			         double minStockAfterCapping;
			         if(safetyStockWeeks < minCapping || safetyStockWeeks == 0)
			         {
			        	 minStockAfterCapping = minCapping;
			         }
			         else {
			        	 minStockAfterCapping = safetyStockWeeks;
			         }
			         if(price==0)
			        	 price=1;
			         double maxStockWeeks = minStockAfterCapping + cycleTime;
			         double minStockAfterCappingCs = Math.round(avgFutureForecast1 * minStockAfterCapping);
			         double maxStockCs = avgFutureForecast1 * maxStockWeeks;
			         double currentSsValue = currentSSWeeks * avgFutureForecast1 * price;
			         double proposedIpmSsValue = price * minStockAfterCapping;
			         double minNormWeeks = minStockAfterCapping;
			         double maxNormWeeks = maxStockWeeks;
			         double minStock = minNormWeeks * price * avgFutureForecast1;
			         double maxStock = maxNormWeeks * price * avgFutureForecast1;
			         double avgCycleStock = (cycleTime/2) * price * avgFutureForecast1;
			         
			         
			         
			         //System.out.println("SKU: " +SKU+ "skuName : " + skuName+ "location1 " + location1 + "locationType : " + locationType + "materialLocation " + materialLocation + " skuClassiication" + skuClassiication + "source " + source + " category" + category + "serviceLevel " + serviceLevel + " weeklyAvgForecast" + weeklyAvgForecast + "sdfePerc1 " + sdfePerc1 + "sdfe " + sdfe + "lotSize " + lotSize + "orValue " + orValue + "cycleTime " + cycleTime + " avgLeadTime" + avgLeadTime + "leadTimeVariability " + leadTimeVariability + " SdVariability" + SdVariability + "cFactorSales " + cFactorSales + "kFactorSales " + kFactorSales + "modelSafetyStock " + modelSafetyStock + "safetyStockWeeks " + safetyStockWeeks + "safetyStockDays " + safetyStockDays + "minStockAfterCapping " + minStockAfterCapping + "maxStockWeeks " + maxStockWeeks + "minStockAfterCappingCs " +  minStockAfterCappingCs + "maxStockCs " + maxStockCs + "currentSsValue " + currentSsValue + "proposedIpmSsValue " + proposedIpmSsValue + "minNormWeeks " + minNormWeeks + "maxNormWeeks " + maxNormWeeks + "minStock " + minStock + "maxStock " + maxStock + "avgCycleStock " + avgCycleStock);
			         			         
			         /*pstmt2.setString(1,SKU);
			         pstmt2.setString(2,skuName);
			         pstmt2.setString(3,location1);
			         pstmt2.setString(4,locationType);
			         pstmt2.setString(5,materialLocation);
			         pstmt2.setString(6,skuClassiication);
			         pstmt2.setString(7,source);
			         pstmt2.setString(8,category);
			         pstmt2.setDouble(9,serviceLevel);
			         pstmt2.setDouble(10,weeklyAvgForecast);
			         pstmt2.setDouble(11,sdfePerc);
			         pstmt2.setDouble(12,sdfe);
			         pstmt2.setDouble(13,lotSize);
			         pstmt2.setLong(14, orValue);
			         pstmt2.setFloat(15, cycleTime);
			         pstmt2.setFloat(16, avgLeadTime);
			         pstmt2.setFloat(17, leadTimeVariability);
			         pstmt2.setDouble(18, SdVariability);
			         pstmt2.setDouble(19, cFactorSales);
			         pstmt2.setDouble(20, kFactorSales);
			         pstmt2.setLong(21, cycleServiceLevel);
			         pstmt2.setDouble(22, 0);
			         pstmt2.setInt(23, 0);
			         pstmt2.setInt(24, 0);
			         pstmt2.setInt(25, 0);
			         pstmt2.setDouble(26, modelSafetyStock);
			         pstmt2.setDouble(27, safetyStockWeeks);
			         pstmt2.setLong(28,safetyStockDays);
			         pstmt2.setDouble(29, minStockAfterCapping);
			         pstmt2.setDouble(30, maxStockWeeks);
			         pstmt2.setDouble(31, minStockAfterCappingCs);
			         pstmt2.setDouble(32, maxStockCs);
			         pstmt2.setFloat(33, currentSSWeeks);
			         pstmt2.setFloat(34, price);
			         pstmt2.setDouble(35, currentSsValue);
			         pstmt2.setDouble(36, proposedIpmSsValue);
			         pstmt2.setDouble(37, minNormWeeks);
			         pstmt2.setDouble(38, maxNormWeeks);
			         pstmt2.setDouble(39, minStock);
			         pstmt2.setDouble(40, maxStock);
			         pstmt2.setDouble(41, avgCycleStock);
			          Timestamp Changes - Start
			         pstmt2.setTimestamp(42, currentDate)*/;
			         /* Timestamp Changes - End*/
			         
					 //pstmt2.executeUpdate();
			         String line1 = SKU+"|"+skuName+"|"+location1+"|"+locationType+"|"+materialLocation+"|"+skuClassiication+"|"+source+"|"+category+"|"+serviceLevel+"|"+avgFutureForecast1+"|"+finalSdfe+"|"+sdfe1+"|"+lotSize+"|"+orValue+"|"+cycleTime+"|"+avgLeadTime+"|"+leadTimeVariability+"|"+SdVariability+"|"+cFactorSales+"|"+kFactorSales+"|"+cycleServiceLevel+"|"+biasPerc+"|"+0+"|"+adjustedSdfe+"|"+finalSdfe+"|"+modelSafetyStock+"|"+safetyStockWeeks+"|"+safetyStockDays+"|"+minStockAfterCapping+"|"+maxStockWeeks+"|"+minStockAfterCappingCs+"|"+maxStockCs+"|"+currentSSWeeks+"|"+price+"|"+currentSsValue+"|"+proposedIpmSsValue+"|"+minNormWeeks+"|"+maxNormWeeks+"|"+minStock+"|"+maxStock+"|"+avgCycleStock+"|"+currentDate+"|0|0";
			        /* String line1 = materialLocation+"|0|"+weeklyAvgForecast+"|"+avgCycleStock+"|"+avgLeadTime+"|0|0|"+cFactorSales+"|"+category+
			 				"|"+currentSsValue+"|"+currentSSWeeks+"|"+cycleServiceLevel+"|0|0|"+kFactorSales+"|"+leadTimeVariability+"|"+location1+"|"+locationType+"|"+lotSize+
			 				"|0|"+maxNormWeeks+"|"+maxStockCs+"|"+maxStock+"|"+maxStockWeeks+"|0|"+minNormWeeks+"|"+minStockAfterCappingCs+"|"+minStockAfterCapping+"|"+
			 				minStock+"|"+modelSafetyStock+"|"+safetyStockDays+"|"+safetyStockWeeks+"|"+orValue+"|"+price+"|"+proposedIpmSsValue+"|"+SdVariability+"|"+sdfe+"|"+
			 				sdfePerc+"|"+serviceLevel+"|"+skuName+"|"+SKU+"|"+skuClassiication+"|"+source;*/
			         try {
				bw1.write(line1+"\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}    
	         }
	         /*BufferedReader br1 = new BufferedReader(new FileReader("C:\\Unilever\\UPLoad CSV\\ipm_model1.csv"));
	         String test = br1.readLine();
	         String a[] = test.split(",");
	         System.out.println("Length = "+a.length);*/
		System.out.println(copyManager.copyIn("COPY bias_calculation FROM STDIN WITH DELIMITER ','", new FileReader("bias_1.csv")));

				System.out.println(copyManager.copyIn("COPY ipm_model FROM STDIN WITH DELIMITER '|'", new FileReader("ipm_model1.csv")));
				

	         bw.close();
	         bw1.close();
	         st1.close();
	         st.close();
	         rs.close();
	         }

   		catch (SQLException | IOException e) {
			e.printStackTrace();
	 }
   		finally 
   		{
  			try 
  			{
	  			conn.close();
	  			pstmt1.close();
	  			pstmt2.close();
	  			rs1.close();

  			}
  			catch (SQLException e) {
  				e.printStackTrace();
  		 }
  		} 
	}

}
