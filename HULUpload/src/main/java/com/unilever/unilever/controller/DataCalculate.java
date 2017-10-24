package com.unilever.unilever.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import com.unilever.unilever.controller.DBconnection;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

public class DataCalculate {
	public static void calculateData() {

		Connection conn = null;
		PreparedStatement pstmt = null, pstmt1 = null, pstmt2 = null;
		Statement st = null, st1 = null, st2 = null, st3 = null, st4 = null;
		ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rs4 = null;
		ArrayList arrayList = new ArrayList<String>();
		// NormalDistribution d = null;
		conn = DBconnection.createDbConn();
		try {
	/*		String sql = "insert into BIAS_CALCULATION(SKU,LOCATION,SKU_NO,Forecast_WEEK_352016,Sales_WEEK_352016,Forecast_WEEK_362016,Sales_WEEK_362016,Forecast_WEEK_372016,Sales_WEEK_372016,Forecast_WEEK_382016,Sales_WEEK_382016,Forecast_WEEK_392016,Sales_WEEK_392016,Forecast_WEEK_402016,Sales_WEEK_402016,Forecast_WEEK_412016,Sales_WEEK_412016,Forecast_WEEK_422016,Sales_WEEK_422016,Forecast_WEEK_432016,Sales_WEEK_432016,Forecast_WEEK_442016,Sales_WEEK_442016,Forecast_WEEK_452016,Sales_WEEK_452016,Forecast_WEEK_462016,Sales_WEEK_462016,Forecast_WEEK_472016,Sales_WEEK_472016,Forecast_WEEK_482016,Sales_WEEK_482016,Forecast_WEEK_492016,Sales_WEEK_492016,Forecast_WEEK_502016,Sales_WEEK_502016,Forecast_WEEK_512016,Sales_WEEK_512016,Forecast_WEEK_522016,Sales_WEEK_522016,Forecast_WEEK_012017,Sales_WEEK_012017,Forecast_WEEK_022017,Sales_WEEK_022017,Forecast_WEEK_032017,Sales_WEEK_032017,Forecast_WEEK_042017,Sales_WEEK_042017,Forecast_WEEK_052017,Sales_WEEK_052017,Forecast_WEEK_062017,Sales_WEEK_062017,Forecast_WEEK_072017,Sales_WEEK_072017,Forecast_WEEK_082017,Sales_WEEK_082017,Forecast_WEEK_092017,Sales_WEEK_092017,Forecast_WEEK_102017,Sales_WEEK_102017,Forecast_WEEK_112017,Sales_WEEK_112017,Forecast_WEEK_122017,Sales_WEEK_122017,Forecast_WEEK_132017,Sales_WEEK_132017,Forecast_WEEK_142017,Sales_WEEK_142017,Forecast_WEEK_152017,Sales_WEEK_152017,Forecast_WEEK_162017,Sales_WEEK_162017,Forecast_WEEK_172017,Sales_WEEK_172017,Forecast_WEEK_182017,Sales_WEEK_182017) (select a.SKU, a.LOCATION, a.SKU_CODE, a.Week35_2016, b.Week35_2016 Sales_Week35_2016, a.Week36_2016, b.Week36_2016 Sales_Week36_2016, a.Week37_2016, b.Week37_2016 Sales_Week37_2016, a.Week38_2016, b.Week38_2016 Sales_Week38_2016, a.Week39_2016, b.Week39_2016 Sales_Week39_2016, a.Week40_2016, b.Week40_2016 Sales_Week40_2016, a.Week41_2016, b.Week41_2016 Sales_Week41_2016, a.Week42_2016, b.Week42_2016 Sales_Week42_2016, a.Week43_2016, b.Week43_2016 Sales_Week43_2016, a.Week44_2016, b.Week44_2016 Sales_Week44_2016, a.Week45_2016, b.Week45_2016 Sales_Week45_2016, a.Week46_2016, b.Week46_2016 Sales_Week46_2016, a.Week47_2016, b.Week47_2016 Sales_Week47_2016, a.Week48_2016, b.Week48_2016 Sales_Week48_2016, a.Week49_2016, b.Week49_2016 Sales_Week49_2016, a.Week50_2016, b.Week50_2016 Sales_Week50_2016, a.Week51_2016, b.Week51_2016 Sales_Week51_2016, a.Week52_2016, b.Week52_2016 Sales_Week52_2016, a.Week01_2017, b.Week01_2017 Sales_Week01_2017, a.Week02_2017, b.Week02_2017 Sales_Week02_2017, a.Week03_2017, b.Week03_2017 Sales_Week03_2017, a.Week04_2017, b.Week04_2017 Sales_Week04_2017, a.Week05_2017, b.Week05_2017 Sales_Week05_2017, a.Week06_2017, b.Week06_2017 Sales_Week06_2017, a.Week07_2017, b.Week07_2017 Sales_Week07_2017, a.Week08_2017, b.Week08_2017 Sales_Week08_2017, a.Week09_2017, b.Week09_2017 Sales_Week09_2017, a.Week10_2017, b.Week10_2017 Sales_Week10_2017, a.Week11_2017, b.Week11_2017 Sales_Week11_2017, a.Week12_2017, b.Week12_2017 Sales_Week12_2017, a.Week13_2017, b.Week13_2017 Sales_Week13_2017, a.Week14_2017, b.Week14_2017 Sales_Week14_2017, a.Week15_2017, b.Week15_2017 Sales_Week15_2017, a.Week16_2017, b.Week16_2017 Sales_Week16_2017, a.Week17_2017, b.Week17_2017 Sales_Week17_2017, a.Week18_2017, b.Week18_2017 Sales_Week18_2017 from Past_Forecast a, Past_Sales b where a.SKU_CODE=b.SKU_CODE)";
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();

			String sql1 = "select SKU,LOCATION,SKU_NO,Forecast_WEEK_352016,Sales_WEEK_352016,Forecast_WEEK_362016,Sales_WEEK_362016,Forecast_WEEK_372016,Sales_WEEK_372016,Forecast_WEEK_382016,Sales_WEEK_382016,Forecast_WEEK_392016,Sales_WEEK_392016,Forecast_WEEK_402016,Sales_WEEK_402016,Forecast_WEEK_412016,Sales_WEEK_412016,Forecast_WEEK_422016,Sales_WEEK_422016,Forecast_WEEK_432016,Sales_WEEK_432016,Forecast_WEEK_442016,Sales_WEEK_442016,Forecast_WEEK_452016,Sales_WEEK_452016,Forecast_WEEK_462016,Sales_WEEK_462016,Forecast_WEEK_472016,Sales_WEEK_472016,Forecast_WEEK_482016,Sales_WEEK_482016,Forecast_WEEK_492016,Sales_WEEK_492016,Forecast_WEEK_502016,Sales_WEEK_502016,Forecast_WEEK_512016,Sales_WEEK_512016,Forecast_WEEK_522016,Sales_WEEK_522016,Forecast_WEEK_012017,Sales_WEEK_012017,Forecast_WEEK_022017,Sales_WEEK_022017,Forecast_WEEK_032017,Sales_WEEK_032017,Forecast_WEEK_042017,Sales_WEEK_042017,Forecast_WEEK_052017,Sales_WEEK_052017,Forecast_WEEK_062017,Sales_WEEK_062017,Forecast_WEEK_072017,Sales_WEEK_072017,Forecast_WEEK_082017,Sales_WEEK_082017,Forecast_WEEK_092017,Sales_WEEK_092017,Forecast_WEEK_102017,Sales_WEEK_102017,Forecast_WEEK_112017,Sales_WEEK_112017,Forecast_WEEK_122017,Sales_WEEK_122017,Forecast_WEEK_132017,Sales_WEEK_132017,Forecast_WEEK_142017,Sales_WEEK_142017,Forecast_WEEK_152017,Sales_WEEK_152017,Forecast_WEEK_162017,Sales_WEEK_162017,Forecast_WEEK_172017,Sales_WEEK_172017,Forecast_WEEK_182017,Sales_WEEK_182017 from BIAS_CALCULATION";*/
			/* Timestamp Changes - Start*/
   			String sql= "insert into BIAS_CALCULATION(SKU,LOCATION,SKU_NO,Forecast_WEEK_352016,Sales_WEEK_352016,Forecast_WEEK_362016,Sales_WEEK_362016,Forecast_WEEK_372016,Sales_WEEK_372016,Forecast_WEEK_382016,Sales_WEEK_382016,Forecast_WEEK_392016,Sales_WEEK_392016,Forecast_WEEK_402016,Sales_WEEK_402016,Forecast_WEEK_412016,Sales_WEEK_412016,Forecast_WEEK_422016,Sales_WEEK_422016,Forecast_WEEK_432016,Sales_WEEK_432016,Forecast_WEEK_442016,Sales_WEEK_442016,Forecast_WEEK_452016,Sales_WEEK_452016,Forecast_WEEK_462016,Sales_WEEK_462016,Forecast_WEEK_472016,Sales_WEEK_472016,Forecast_WEEK_482016,Sales_WEEK_482016,Forecast_WEEK_492016,Sales_WEEK_492016,Forecast_WEEK_502016,Sales_WEEK_502016,Forecast_WEEK_512016,Sales_WEEK_512016,Forecast_WEEK_522016,Sales_WEEK_522016,Forecast_WEEK_012017,Sales_WEEK_012017,Forecast_WEEK_022017,Sales_WEEK_022017,Forecast_WEEK_032017,Sales_WEEK_032017,Forecast_WEEK_042017,Sales_WEEK_042017,Forecast_WEEK_052017,Sales_WEEK_052017,Forecast_WEEK_062017,Sales_WEEK_062017,Forecast_WEEK_072017,Sales_WEEK_072017,Forecast_WEEK_082017,Sales_WEEK_082017,Forecast_WEEK_092017,Sales_WEEK_092017,Forecast_WEEK_102017,Sales_WEEK_102017,Forecast_WEEK_112017,Sales_WEEK_112017,Forecast_WEEK_122017,Sales_WEEK_122017,Forecast_WEEK_132017,Sales_WEEK_132017,Forecast_WEEK_142017,Sales_WEEK_142017,Forecast_WEEK_152017,Sales_WEEK_152017,Forecast_WEEK_162017,Sales_WEEK_162017,Forecast_WEEK_172017,Sales_WEEK_172017,Forecast_WEEK_182017,Sales_WEEK_182017,CurrentDate) (select a.SKU, a.LOCATION, a.SKU_CODE, a.Week35_2016, b.Week35_2016 Sales_Week35_2016, a.Week36_2016, b.Week36_2016 Sales_Week36_2016, a.Week37_2016, b.Week37_2016 Sales_Week37_2016, a.Week38_2016, b.Week38_2016 Sales_Week38_2016, a.Week39_2016, b.Week39_2016 Sales_Week39_2016, a.Week40_2016, b.Week40_2016 Sales_Week40_2016, a.Week41_2016, b.Week41_2016 Sales_Week41_2016, a.Week42_2016, b.Week42_2016 Sales_Week42_2016, a.Week43_2016, b.Week43_2016 Sales_Week43_2016, a.Week44_2016, b.Week44_2016 Sales_Week44_2016, a.Week45_2016, b.Week45_2016 Sales_Week45_2016, a.Week46_2016, b.Week46_2016 Sales_Week46_2016, a.Week47_2016, b.Week47_2016 Sales_Week47_2016, a.Week48_2016, b.Week48_2016 Sales_Week48_2016, a.Week49_2016, b.Week49_2016 Sales_Week49_2016, a.Week50_2016, b.Week50_2016 Sales_Week50_2016, a.Week51_2016, b.Week51_2016 Sales_Week51_2016, a.Week52_2016, b.Week52_2016 Sales_Week52_2016, a.Week01_2017, b.Week01_2017 Sales_Week01_2017, a.Week02_2017, b.Week02_2017 Sales_Week02_2017, a.Week03_2017, b.Week03_2017 Sales_Week03_2017, a.Week04_2017, b.Week04_2017 Sales_Week04_2017, a.Week05_2017, b.Week05_2017 Sales_Week05_2017, a.Week06_2017, b.Week06_2017 Sales_Week06_2017, a.Week07_2017, b.Week07_2017 Sales_Week07_2017, a.Week08_2017, b.Week08_2017 Sales_Week08_2017, a.Week09_2017, b.Week09_2017 Sales_Week09_2017, a.Week10_2017, b.Week10_2017 Sales_Week10_2017, a.Week11_2017, b.Week11_2017 Sales_Week11_2017, a.Week12_2017, b.Week12_2017 Sales_Week12_2017, a.Week13_2017, b.Week13_2017 Sales_Week13_2017, a.Week14_2017, b.Week14_2017 Sales_Week14_2017, a.Week15_2017, b.Week15_2017 Sales_Week15_2017, a.Week16_2017, b.Week16_2017 Sales_Week16_2017, a.Week17_2017, b.Week17_2017 Sales_Week17_2017, a.Week18_2017, b.Week18_2017 Sales_Week18_2017, a.CurrentDate from Past_Forecast a, Past_Sales b where a.SKU_CODE=b.SKU_CODE)";
			 pstmt=conn.prepareStatement(sql);			 
			 pstmt.executeUpdate();
			 
			 String sql1="select SKU,LOCATION,SKU_NO,Forecast_WEEK_352016,Sales_WEEK_352016,Forecast_WEEK_362016,Sales_WEEK_362016,Forecast_WEEK_372016,Sales_WEEK_372016,Forecast_WEEK_382016,Sales_WEEK_382016,Forecast_WEEK_392016,Sales_WEEK_392016,Forecast_WEEK_402016,Sales_WEEK_402016,Forecast_WEEK_412016,Sales_WEEK_412016,Forecast_WEEK_422016,Sales_WEEK_422016,Forecast_WEEK_432016,Sales_WEEK_432016,Forecast_WEEK_442016,Sales_WEEK_442016,Forecast_WEEK_452016,Sales_WEEK_452016,Forecast_WEEK_462016,Sales_WEEK_462016,Forecast_WEEK_472016,Sales_WEEK_472016,Forecast_WEEK_482016,Sales_WEEK_482016,Forecast_WEEK_492016,Sales_WEEK_492016,Forecast_WEEK_502016,Sales_WEEK_502016,Forecast_WEEK_512016,Sales_WEEK_512016,Forecast_WEEK_522016,Sales_WEEK_522016,Forecast_WEEK_012017,Sales_WEEK_012017,Forecast_WEEK_022017,Sales_WEEK_022017,Forecast_WEEK_032017,Sales_WEEK_032017,Forecast_WEEK_042017,Sales_WEEK_042017,Forecast_WEEK_052017,Sales_WEEK_052017,Forecast_WEEK_062017,Sales_WEEK_062017,Forecast_WEEK_072017,Sales_WEEK_072017,Forecast_WEEK_082017,Sales_WEEK_082017,Forecast_WEEK_092017,Sales_WEEK_092017,Forecast_WEEK_102017,Sales_WEEK_102017,Forecast_WEEK_112017,Sales_WEEK_112017,Forecast_WEEK_122017,Sales_WEEK_122017,Forecast_WEEK_132017,Sales_WEEK_132017,Forecast_WEEK_142017,Sales_WEEK_142017,Forecast_WEEK_152017,Sales_WEEK_152017,Forecast_WEEK_162017,Sales_WEEK_162017,Forecast_WEEK_172017,Sales_WEEK_172017,Forecast_WEEK_182017,Sales_WEEK_182017, CurrentDate from BIAS_CALCULATION";
			 /* Timestamp Changes - End*/
			st = conn.createStatement();
			rs = st.executeQuery(sql1);
			ResultSetMetaData metadata = rs.getMetaData();
			int numberOfColumns = metadata.getColumnCount();

			while (rs.next()) {
				/*
				 * int i = 1; while(i <= numberOfColumns) {
				 * arrayList.add(rs.getString(i++)); }
				 */
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
				/* Timestamp Changes - Start*/
	             Timestamp currentDate = rs.getTimestamp(76);
	             /* Timestamp Changes - End*/

				/*
				 * for(int j=3;j<=numberOfColumns;j++) {
				 * Math.abs(arrayList(j)-arrayList(++j)); }
				 */

				double bias_352016 = Math.abs(Fore_352016 - sale_352016);
				double bias_362016 = Math.abs(Fore_362016 - sale_362016);
				double bias_372016 = Math.abs(Fore_372016 - sale_372016);
				double bias_382016 = Math.abs(Fore_382016 - sale_382016);
				double bias_392016 = Math.abs(Fore_392016 - sale_392016);
				double bias_402016 = Math.abs(Fore_402016 - sale_402016);
				double bias_412016 = Math.abs(Fore_412016 - sale_412016);
				double bias_422016 = Math.abs(Fore_422016 - sale_422016);
				double bias_432016 = Math.abs(Fore_432016 - sale_432016);
				double bias_442016 = Math.abs(Fore_442016 - sale_442016);
				double bias_452016 = Math.abs(Fore_452016 - sale_452016);
				double bias_462016 = Math.abs(Fore_462016 - sale_462016);
				double bias_472016 = Math.abs(Fore_472016 - sale_472016);
				double bias_482016 = Math.abs(Fore_482016 - sale_482016);
				double bias_492016 = Math.abs(Fore_492016 - sale_492016);
				double bias_502016 = Math.abs(Fore_502016 - sale_502016);
				double bias_512016 = Math.abs(Fore_512016 - sale_512016);
				double bias_522016 = Math.abs(Fore_522016 - sale_522016);
				double bias_012017 = Math.abs(Fore_012017 - sale_012017);
				double bias_022017 = Math.abs(Fore_022017 - sale_022017);
				double bias_032017 = Math.abs(Fore_032017 - sale_032017);
				double bias_042017 = Math.abs(Fore_042017 - sale_042017);
				double bias_052017 = Math.abs(Fore_052017 - sale_052017);
				double bias_062017 = Math.abs(Fore_062017 - sale_062017);
				double bias_072017 = Math.abs(Fore_072017 - sale_072017);
				double bias_082017 = Math.abs(Fore_082017 - sale_082017);
				double bias_092017 = Math.abs(Fore_092017 - sale_092017);
				double bias_102017 = Math.abs(Fore_102017 - sale_102017);
				double bias_112017 = Math.abs(Fore_112017 - sale_112017);
				double bias_122017 = Math.abs(Fore_122017 - sale_122017);
				double bias_132017 = Math.abs(Fore_132017 - sale_132017);
				double bias_142017 = Math.abs(Fore_142017 - sale_142017);
				double bias_152017 = Math.abs(Fore_152017 - sale_152017);
				double bias_162017 = Math.abs(Fore_162017 - sale_162017);
				double bias_172017 = Math.abs(Fore_172017 - sale_172017);
				double bias_182017 = Math.abs(Fore_182017 - sale_182017);

				double biasPerc_352016 = Math.round((bias_352016 / Fore_352016) * 100);
				double biasPerc_362016 = Math.round((bias_362016 / Fore_362016) * 100);
				double biasPerc_372016 = Math.round((bias_372016 / Fore_372016) * 100);
				double biasPerc_382016 = Math.round((bias_382016 / Fore_382016) * 100);
				double biasPerc_392016 = Math.round((bias_392016 / Fore_392016) * 100);
				double biasPerc_402016 = Math.round((bias_402016 / Fore_402016) * 100);
				double biasPerc_412016 = Math.round((bias_412016 / Fore_412016) * 100);
				double biasPerc_422016 = Math.round((bias_422016 / Fore_422016) * 100);
				double biasPerc_432016 = Math.round((bias_432016 / Fore_432016) * 100);
				double biasPerc_442016 = Math.round((bias_442016 / Fore_442016) * 100);
				double biasPerc_452016 = Math.round((bias_452016 / Fore_452016) * 100);
				double biasPerc_462016 = Math.round((bias_462016 / Fore_462016) * 100);
				double biasPerc_472016 = Math.round((bias_472016 / Fore_472016) * 100);
				double biasPerc_482016 = Math.round((bias_482016 / Fore_482016) * 100);
				double biasPerc_492016 = Math.round((bias_492016 / Fore_492016) * 100);
				double biasPerc_502016 = Math.round((bias_502016 / Fore_502016) * 100);
				double biasPerc_512016 = Math.round((bias_512016 / Fore_512016) * 100);
				double biasPerc_522016 = Math.round((bias_522016 / Fore_522016) * 100);
				double biasPerc_012017 = Math.round((bias_012017 / Fore_012017) * 100);
				double biasPerc_022017 = Math.round((bias_022017 / Fore_022017) * 100);
				double biasPerc_032017 = Math.round((bias_032017 / Fore_032017) * 100);
				double biasPerc_042017 = Math.round((bias_042017 / Fore_042017) * 100);
				double biasPerc_052017 = Math.round((bias_052017 / Fore_052017) * 100);
				double biasPerc_062017 = Math.round((bias_062017 / Fore_062017) * 100);
				double biasPerc_072017 = Math.round((bias_072017 / Fore_072017) * 100);
				double biasPerc_082017 = Math.round((bias_082017 / Fore_082017) * 100);
				double biasPerc_092017 = Math.round((bias_092017 / Fore_092017) * 100);
				double biasPerc_102017 = Math.round((bias_102017 / Fore_102017) * 100);
				double biasPerc_112017 = Math.round((bias_112017 / Fore_112017) * 100);
				double biasPerc_122017 = Math.round((bias_122017 / Fore_122017) * 100);
				double biasPerc_132017 = Math.round((bias_132017 / Fore_132017) * 100);
				double biasPerc_142017 = Math.round((bias_142017 / Fore_142017) * 100);
				double biasPerc_152017 = Math.round((bias_152017 / Fore_152017) * 100);
				double biasPerc_162017 = Math.round((bias_162017 / Fore_162017) * 100);
				double biasPerc_172017 = Math.round((bias_172017 / Fore_172017) * 100);
				double biasPerc_182017 = Math.round((bias_182017 / Fore_182017) * 100);

				double biasPersale_352016 = (biasPerc_352016 / 100) * sale_352016;
				double biasPersale_362016 = (biasPerc_362016 / 100) * sale_362016;
				double biasPersale_372016 = (biasPerc_372016 / 100) * sale_372016;
				double biasPersale_382016 = (biasPerc_382016 / 100) * sale_382016;
				double biasPersale_392016 = (biasPerc_392016 / 100) * sale_392016;
				double biasPersale_402016 = (biasPerc_402016 / 100) * sale_402016;
				double biasPersale_412016 = (biasPerc_412016 / 100) * sale_412016;
				double biasPersale_422016 = (biasPerc_422016 / 100) * sale_422016;
				double biasPersale_432016 = (biasPerc_432016 / 100) * sale_432016;
				double biasPersale_442016 = (biasPerc_442016 / 100) * sale_442016;
				double biasPersale_452016 = (biasPerc_452016 / 100) * sale_452016;
				double biasPersale_462016 = (biasPerc_462016 / 100) * sale_462016;
				double biasPersale_472016 = (biasPerc_472016 / 100) * sale_472016;
				double biasPersale_482016 = (biasPerc_482016 / 100) * sale_482016;
				double biasPersale_492016 = (biasPerc_492016 / 100) * sale_492016;
				double biasPersale_502016 = (biasPerc_502016 / 100) * sale_502016;
				double biasPersale_512016 = (biasPerc_512016 / 100) * sale_512016;
				double biasPersale_522016 = (biasPerc_522016 / 100) * sale_522016;
				double biasPersale_012017 = (biasPerc_012017 / 100) * sale_012017;
				double biasPersale_022017 = (biasPerc_022017 / 100) * sale_022017;
				double biasPersale_032017 = (biasPerc_032017 / 100) * sale_032017;
				double biasPersale_042017 = (biasPerc_042017 / 100) * sale_042017;
				double biasPersale_052017 = (biasPerc_052017 / 100) * sale_052017;
				double biasPersale_062017 = (biasPerc_062017 / 100) * sale_062017;
				double biasPersale_072017 = (biasPerc_072017 / 100) * sale_072017;
				double biasPersale_082017 = (biasPerc_082017 / 100) * sale_082017;
				double biasPersale_092017 = (biasPerc_092017 / 100) * sale_092017;
				double biasPersale_102017 = (biasPerc_102017 / 100) * sale_102017;
				double biasPersale_112017 = (biasPerc_112017 / 100) * sale_112017;
				double biasPersale_122017 = (biasPerc_122017 / 100) * sale_122017;
				double biasPersale_132017 = (biasPerc_132017 / 100) * sale_132017;
				double biasPersale_142017 = (biasPerc_142017 / 100) * sale_142017;
				double biasPersale_152017 = (biasPerc_152017 / 100) * sale_152017;
				double biasPersale_162017 = (biasPerc_162017 / 100) * sale_162017;
				double biasPersale_172017 = (biasPerc_172017 / 100) * sale_172017;
				double biasPersale_182017 = (biasPerc_182017 / 100) * sale_182017;

				double totalForecast = Fore_352016 + Fore_362016 + Fore_372016 + Fore_382016 + Fore_392016 + Fore_402016
						+ Fore_412016 + Fore_422016 + Fore_432016 + Fore_442016 + Fore_452016 + Fore_462016
						+ Fore_472016 + Fore_482016 + Fore_492016 + Fore_502016 + Fore_512016 + Fore_522016
						+ Fore_012017 + Fore_022017 + Fore_032017 + Fore_042017 + Fore_052017 + Fore_062017
						+ Fore_072017 + Fore_082017 + Fore_092017 + Fore_102017 + Fore_112017 + Fore_122017
						+ Fore_132017 + Fore_142017 + Fore_152017 + Fore_162017 + Fore_172017 + Fore_172017;
				double totalSales = sale_352016 + sale_362016 + sale_372016 + sale_382016 + sale_392016 + sale_402016
						+ sale_412016 + sale_422016 + sale_432016 + sale_442016 + sale_452016 + sale_462016
						+ sale_472016 + sale_482016 + sale_492016 + sale_502016 + sale_512016 + sale_522016
						+ sale_012017 + sale_022017 + sale_032017 + sale_042017 + sale_052017 + sale_062017
						+ sale_072017 + sale_082017 + sale_092017 + sale_102017 + sale_112017 + sale_122017
						+ sale_132017 + sale_142017 + sale_152017 + sale_162017 + sale_172017 + sale_182017;

				double biasPerSale = Math
						.round(biasPersale_352016 + biasPersale_362016 + biasPersale_372016 + biasPersale_382016
								+ biasPersale_392016 + biasPersale_402016 + biasPersale_412016 + biasPersale_422016
								+ biasPersale_432016 + biasPersale_442016 + biasPersale_452016 + biasPersale_462016
								+ biasPersale_472016 + biasPersale_482016 + biasPersale_492016 + biasPersale_502016
								+ biasPersale_512016 + biasPersale_522016 + biasPersale_012017 + biasPersale_022017
								+ biasPersale_032017 + biasPersale_042017 + biasPersale_052017 + biasPersale_062017
								+ biasPersale_072017 + biasPersale_082017 + biasPersale_092017 + biasPersale_102017
								+ biasPersale_112017 + biasPersale_122017 + biasPersale_132017 + biasPersale_142017
								+ biasPersale_152017 + biasPersale_162017 + biasPersale_172017 + biasPersale_182017);

				double mape = Math.round((biasPerSale / totalSales) * 100);

				double factor = Math.sqrt(10 / 7);

				double avgForecast = Math.round(totalForecast / 36);

				double weeklyAvgForecast = Math.round(avgForecast * 0.7);

				double sdfePerc = Math.round(mape * 1.25 * factor);

				double sdfe = Math.round((avgForecast / sdfePerc) * 100);

				System.out.println("totalForecast :" + totalForecast + "avgForecast :" + avgForecast
						+ "weeklyAvgForecast : " + weeklyAvgForecast + "factor :" + factor + "totalForecast:"
						+ totalForecast + "totalSales:" + totalSales + "biasPerSale :" + biasPerSale + "mape :" + mape
						+ "sdfePerc :" + sdfePerc + " sdfe :" + sdfe);

				String sql2 = "update BIAS_CALCULATION SET Bias_Calc_WEEK_352016=?, Bias_Perc_WEEK_352016=?, Bias_Per_Sale_WEEK_352016=?, Bias_Calc_WEEK_362016=?, Bias_Perc_WEEK_362016=?, Bias_Per_Sale_WEEK_362016=?, Bias_Calc_WEEK_372016=?, Bias_Perc_WEEK_372016=?, Bias_Per_Sale_WEEK_372016=?, Bias_Calc_WEEK_382016=?, Bias_Perc_WEEK_382016=?, Bias_Per_Sale_WEEK_382016=?, Bias_Calc_WEEK_392016=?, Bias_Perc_WEEK_392016=?, Bias_Per_Sale_WEEK_392016=?, Bias_Calc_WEEK_402016=?, Bias_Perc_WEEK_402016=?, Bias_Per_Sale_WEEK_402016=?, Bias_Calc_WEEK_412016=?, Bias_Perc_WEEK_412016=?, Bias_Per_Sale_WEEK_412016=?, Bias_Calc_WEEK_422016=?, Bias_Perc_WEEK_422016=?, Bias_Per_Sale_WEEK_422016=?,Bias_Calc_WEEK_432016=?, Bias_Perc_WEEK_432016=?, Bias_Per_Sale_WEEK_432016=?, Bias_Calc_WEEK_442016=?, Bias_Perc_WEEK_442016=?, Bias_Per_Sale_WEEK_442016=?, Bias_Calc_WEEK_452016=?, Bias_Perc_WEEK_452016=?, Bias_Per_Sale_WEEK_452016=?,Bias_Calc_WEEK_462016=?, Bias_Perc_WEEK_462016=?, Bias_Per_Sale_WEEK_462016=?,Bias_Calc_WEEK_472016=?, Bias_Perc_WEEK_472016=?, Bias_Per_Sale_WEEK_472016=?,Bias_Calc_WEEK_482016=?, Bias_Perc_WEEK_482016=?, Bias_Per_Sale_WEEK_482016=?,Bias_Calc_WEEK_492016=?, Bias_Perc_WEEK_492016=?, Bias_Per_Sale_WEEK_492016=?,Bias_Calc_WEEK_502016=?, Bias_Perc_WEEK_502016=?, Bias_Per_Sale_WEEK_502016=?,Bias_Calc_WEEK_512016=?, Bias_Perc_WEEK_512016=?, Bias_Per_Sale_WEEK_512016=?,Bias_Calc_WEEK_522016=?, Bias_Perc_WEEK_522016=?, Bias_Per_Sale_WEEK_522016=?,Bias_Calc_WEEK_012017=?, Bias_Perc_WEEK_012017=?, Bias_Per_Sale_WEEK_012017=?,Bias_Calc_WEEK_022017=?, Bias_Perc_WEEK_022017=?, Bias_Per_Sale_WEEK_022017=?,Bias_Calc_WEEK_032017=?, Bias_Perc_WEEK_032017=?, Bias_Per_Sale_WEEK_032017=?,Bias_Calc_WEEK_042017=?, Bias_Perc_WEEK_042017=?, Bias_Per_Sale_WEEK_042017=?,Bias_Calc_WEEK_052017=?, Bias_Perc_WEEK_052017=?, Bias_Per_Sale_WEEK_052017=?,Bias_Calc_WEEK_062017=?, Bias_Perc_WEEK_062017=?, Bias_Per_Sale_WEEK_062017=?,Bias_Calc_WEEK_072017=?, Bias_Perc_WEEK_072017=?, Bias_Per_Sale_WEEK_072017=?,Bias_Calc_WEEK_082017=?, Bias_Perc_WEEK_082017=?, Bias_Per_Sale_WEEK_082017=?,Bias_Calc_WEEK_092017=?, Bias_Perc_WEEK_092017=?, Bias_Per_Sale_WEEK_092017=?,Bias_Calc_WEEK_102017=?, Bias_Perc_WEEK_102017=?, Bias_Per_Sale_WEEK_102017=?,Bias_Calc_WEEK_112017=?, Bias_Perc_WEEK_112017=?, Bias_Per_Sale_WEEK_112017=?,Bias_Calc_WEEK_122017=?, Bias_Perc_WEEK_122017=?, Bias_Per_Sale_WEEK_122017=?,Bias_Calc_WEEK_132017=?, Bias_Perc_WEEK_132017=?, Bias_Per_Sale_WEEK_132017=?,Bias_Calc_WEEK_142017=?, Bias_Perc_WEEK_142017=?, Bias_Per_Sale_WEEK_142017=?,Bias_Calc_WEEK_152017=?, Bias_Perc_WEEK_152017=?, Bias_Per_Sale_WEEK_152017=?,Bias_Calc_WEEK_162017=?, Bias_Perc_WEEK_162017=?, Bias_Per_Sale_WEEK_162017=?,Bias_Calc_WEEK_172017=?, Bias_Perc_WEEK_172017=?, Bias_Per_Sale_WEEK_172017=?,Bias_Calc_WEEK_182017=?, Bias_Perc_WEEK_182017=?, Bias_Per_Sale_WEEK_182017=?,Weighted_Mape=?, SDFE=?, SDFE_Perc=?, Total_Past_Forecast=?, Total_Past_sales=?, Factor=?, Avg_Forecast=?, Count=?, Weekly_Avg_Forecast=? where SKU_NO='"
						+ skuNo + "'";

				pstmt1 = conn.prepareStatement(sql2);

				pstmt1.setDouble(1, bias_352016);
				pstmt1.setDouble(2, biasPerc_352016);
				pstmt1.setDouble(3, biasPersale_352016);
				pstmt1.setDouble(4, bias_362016);
				pstmt1.setDouble(5, biasPerc_362016);
				pstmt1.setDouble(6, biasPersale_362016);
				pstmt1.setDouble(7, bias_372016);
				pstmt1.setDouble(8, biasPerc_372016);
				pstmt1.setDouble(9, biasPersale_372016);
				pstmt1.setDouble(10, bias_382016);
				pstmt1.setDouble(11, biasPerc_382016);
				pstmt1.setDouble(12, biasPersale_382016);
				pstmt1.setDouble(13, bias_392016);
				pstmt1.setDouble(14, biasPerc_392016);
				pstmt1.setDouble(15, biasPersale_392016);
				pstmt1.setDouble(16, bias_402016);
				pstmt1.setDouble(17, biasPerc_402016);
				pstmt1.setDouble(18, biasPersale_402016);
				pstmt1.setDouble(19, bias_412016);
				pstmt1.setDouble(20, biasPerc_412016);
				pstmt1.setDouble(21, biasPersale_412016);
				pstmt1.setDouble(22, bias_422016);
				pstmt1.setDouble(23, biasPerc_422016);
				pstmt1.setDouble(24, biasPersale_422016);
				pstmt1.setDouble(25, bias_432016);
				pstmt1.setDouble(26, biasPerc_432016);
				pstmt1.setDouble(27, biasPersale_432016);
				pstmt1.setDouble(28, bias_442016);
				pstmt1.setDouble(29, biasPerc_442016);
				pstmt1.setDouble(30, biasPersale_442016);
				pstmt1.setDouble(31, bias_452016);
				pstmt1.setDouble(32, biasPerc_452016);
				pstmt1.setDouble(33, biasPersale_452016);
				pstmt1.setDouble(34, bias_462016);
				pstmt1.setDouble(35, biasPerc_462016);
				pstmt1.setDouble(36, biasPersale_462016);
				pstmt1.setDouble(37, bias_472016);
				pstmt1.setDouble(38, biasPerc_472016);
				pstmt1.setDouble(39, biasPersale_472016);
				pstmt1.setDouble(40, bias_482016);
				pstmt1.setDouble(41, biasPerc_482016);
				pstmt1.setDouble(42, biasPersale_482016);
				pstmt1.setDouble(43, bias_492016);
				pstmt1.setDouble(44, biasPerc_492016);
				pstmt1.setDouble(45, biasPersale_492016);
				pstmt1.setDouble(46, bias_502016);
				pstmt1.setDouble(47, biasPerc_502016);
				pstmt1.setDouble(48, biasPersale_502016);
				pstmt1.setDouble(49, bias_512016);
				pstmt1.setDouble(50, biasPerc_512016);
				pstmt1.setDouble(51, biasPersale_512016);
				pstmt1.setDouble(52, bias_522016);
				pstmt1.setDouble(53, biasPerc_522016);
				pstmt1.setDouble(54, biasPersale_522016);
				pstmt1.setDouble(55, bias_012017);
				pstmt1.setDouble(56, biasPerc_012017);
				pstmt1.setDouble(57, biasPersale_012017);
				pstmt1.setDouble(58, bias_022017);
				pstmt1.setDouble(59, biasPerc_022017);
				pstmt1.setDouble(60, biasPersale_022017);
				pstmt1.setDouble(61, bias_032017);
				pstmt1.setDouble(62, biasPerc_032017);
				pstmt1.setDouble(63, biasPersale_032017);
				pstmt1.setDouble(64, bias_042017);
				pstmt1.setDouble(65, biasPerc_042017);
				pstmt1.setDouble(66, biasPersale_042017);
				pstmt1.setDouble(67, bias_052017);
				pstmt1.setDouble(68, biasPerc_052017);
				pstmt1.setDouble(69, biasPersale_052017);
				pstmt1.setDouble(70, bias_062017);
				pstmt1.setDouble(71, biasPerc_062017);
				pstmt1.setDouble(72, biasPersale_062017);
				pstmt1.setDouble(73, bias_072017);
				pstmt1.setDouble(74, biasPerc_072017);
				pstmt1.setDouble(75, biasPersale_072017);
				pstmt1.setDouble(76, bias_082017);
				pstmt1.setDouble(77, biasPerc_082017);
				pstmt1.setDouble(78, biasPersale_082017);
				pstmt1.setDouble(79, bias_092017);
				pstmt1.setDouble(80, biasPerc_092017);
				pstmt1.setDouble(81, biasPersale_092017);
				pstmt1.setDouble(82, bias_102017);
				pstmt1.setDouble(83, biasPerc_102017);
				pstmt1.setDouble(84, biasPersale_102017);
				pstmt1.setDouble(85, bias_112017);
				pstmt1.setDouble(86, biasPerc_112017);
				pstmt1.setDouble(87, biasPersale_112017);
				pstmt1.setDouble(88, bias_122017);
				pstmt1.setDouble(89, biasPerc_122017);
				pstmt1.setDouble(90, biasPersale_122017);
				pstmt1.setDouble(91, bias_132017);
				pstmt1.setDouble(92, biasPerc_132017);
				pstmt1.setDouble(93, biasPersale_132017);
				pstmt1.setDouble(94, bias_142017);
				pstmt1.setDouble(95, biasPerc_142017);
				pstmt1.setDouble(96, biasPersale_142017);
				pstmt1.setDouble(97, bias_152017);
				pstmt1.setDouble(98, biasPerc_152017);
				pstmt1.setDouble(99, biasPersale_152017);
				pstmt1.setDouble(100, bias_162017);
				pstmt1.setDouble(101, biasPerc_162017);
				pstmt1.setDouble(102, biasPersale_162017);
				pstmt1.setDouble(103, bias_172017);
				pstmt1.setDouble(104, biasPerc_172017);
				pstmt1.setDouble(105, biasPersale_172017);
				pstmt1.setDouble(106, bias_182017);
				pstmt1.setDouble(107, biasPerc_182017);
				pstmt1.setDouble(108, biasPersale_182017);
				pstmt1.setDouble(109, mape);
				pstmt1.setDouble(110, sdfe);
				pstmt1.setDouble(111, sdfePerc);
				pstmt1.setDouble(112, totalForecast);
				pstmt1.setDouble(113, totalSales);
				pstmt1.setDouble(114, factor);
				pstmt1.setDouble(115, avgForecast);
				pstmt1.setInt(116, 36);
				pstmt1.setDouble(117, weeklyAvgForecast);

				pstmt1.executeUpdate();

				/* IPM MODEL CALCULATIONS */

				System.out.println("IPM Calculations Started");

				String sql3 = "SELECT * FROM SELECTION";
				st1 = conn.createStatement();
				rs1 = st1.executeQuery(sql3);
				int serviceLevel;
				String targetServiceLevel = "";
				float minCapping = 0;

				if (rs1.next()) {
					targetServiceLevel = rs1.getString("Target_ServiceLevel");
					minCapping = rs1.getFloat("Min_Capping");
				}
				/*String sql4 = "SELECT a.SKU,a.Location,a.Location_Type,a.Material_Location,a.Category,a.Service_Level,b.Weekly_Avg_Forecast,b.SDFE,b.SDFE_Perc,a.Production_Time,a.OR_Value,a.Source,a.SKU_Classification,a.Avg_Lead_Time,a.Lead_Time_Variability,a.Current_SSWeeks,a.Price,a.SKU_Name FROM Input_Parameter a, BIAS_CALCULATION b where a.Material_Location= b.SKU_NO and a.Material_Location='"
						+ skuNo + "'";*/
				/* Timestamp Changes - Start*/
		         String sql4="SELECT a.SKU,a.Location,a.Location_Type,a.Material_Location,a.Category,a.Service_Level,b.Weekly_Avg_Forecast,b.SDFE,b.SDFE_Perc,a.Production_Time,a.OR_Value,a.Source,a.SKU_Classification,a.Avg_Lead_Time,a.Lead_Time_Variability,a.Current_SSWeeks,a.Price,a.SKU_Name, b.CurrentDate FROM Input_Parameter a, BIAS_CALCULATION b where a.Material_Location= b.SKU_NO and a.Material_Location='"+skuNo+"'";
		         /* Timestamp Changes - End*/
				st2 = conn.createStatement();
				rs2 = st2.executeQuery(sql4);

				while (rs2.next()) {
					System.out.println("Inside select block");
					String SKU = rs2.getString(1);
					String location1 = rs2.getString(2);
					String locationType = rs2.getString(3);
					String materialLocation = rs2.getString(4);
					String category = rs2.getString(5);
					if (targetServiceLevel.equals("Category service level")) {
						serviceLevel = rs1.getInt("Category_ServiceLevel");
					} else {
						serviceLevel = rs2.getInt(6);
					}
					float avgWeeklyDemand = rs2.getFloat(7);
					float sdfe1 = rs2.getFloat(8);
					int sdfePerc1 = rs2.getInt(9);
					float cycleTime = rs2.getFloat(10);
					int orValue = rs2.getInt(11);
					String source = rs2.getString(12);
					String skuClassiication = rs2.getString(13);
					float avgLeadTime = rs2.getFloat(14);
					float leadTimeVariability = rs2.getFloat(15);
					float currentSSWeeks = rs2.getFloat(16);
					float price = rs2.getFloat(17);
					String skuName = rs2.getString(18);

					float lotSize = cycleTime * avgWeeklyDemand;
					double SdVariability = Math.round(Math.sqrt(((Math.pow(sdfe, 2)) * (avgLeadTime))
							+ Math.pow(((avgWeeklyDemand) * (leadTimeVariability)), 2) + Math.pow(avgWeeklyDemand, 2)
									* Math.pow(1.25, 2) * Math.pow((cycleTime * (1 - (orValue / 100))), 2)));
					double cFactorSales = Math.round(0.92
							+ Math.log(avgWeeklyDemand * cycleTime * ((1 - (serviceLevel / 100)) / SdVariability)));
					double kFactorSales = Math
							.round(-1.19 + Math.sqrt(((Math.pow(1.19, 2)) - 4 * 0.37 * cFactorSales) / (2 * 0.37)));
					// double cycleServiceLevel =
					// d.cumulativeProbability(kFactorSales);
					long cycleServiceLevel = 0;
					double modelSafetyStock = 0;
					if (avgWeeklyDemand == 0) {
						modelSafetyStock = 0;
					} else if ((kFactorSales * SdVariability) > 0) {
						modelSafetyStock = kFactorSales * SdVariability;
					}
					double safetyStockWeeks;
					if (modelSafetyStock == 0) {
						safetyStockWeeks = 0;
					} else if ((modelSafetyStock / avgWeeklyDemand) > 13) {
						safetyStockWeeks = 13;
					} else {
						safetyStockWeeks = (modelSafetyStock / avgWeeklyDemand);
					}
					long safetyStockDays = Math.round(safetyStockWeeks * 7);
					double minStockAfterCapping;
					if (safetyStockWeeks < minCapping || safetyStockWeeks == 0) {
						minStockAfterCapping = minCapping;
					} else {
						minStockAfterCapping = safetyStockWeeks;
					}
					double maxStockWeeks = minStockAfterCapping + cycleTime;
					double minStockAfterCappingCs = avgWeeklyDemand * minStockAfterCapping;
					double maxStockCs = avgWeeklyDemand * maxStockWeeks;
					double currentSsValue = currentSSWeeks * avgWeeklyDemand * price;
					double proposedIpmSsValue = price * minStockAfterCapping;
					double minNormWeeks = minStockAfterCapping;
					double maxNormWeeks = maxStockWeeks;
					double minStock = minNormWeeks * price * avgWeeklyDemand;
					double maxStock = maxNormWeeks * price * avgWeeklyDemand;
					double avgCycleStock = (cycleTime / 2) * price * avgWeeklyDemand;

					System.out.println("SKU: " + SKU + "skuName : " + skuName + "location1 " + location1
							+ "locationType : " + locationType + "materialLocation " + materialLocation
							+ " skuClassiication" + skuClassiication + "source " + source + " category" + category
							+ "serviceLevel " + serviceLevel + " avgWeeklyDemand" + avgWeeklyDemand + "sdfePerc1 "
							+ sdfePerc1 + "sdfe " + sdfe + "lotSize " + lotSize + "orValue " + orValue + "cycleTime "
							+ cycleTime + " avgLeadTime" + avgLeadTime + "leadTimeVariability " + leadTimeVariability
							+ " SdVariability" + SdVariability + "cFactorSales " + cFactorSales + "kFactorSales "
							+ kFactorSales + "modelSafetyStock " + modelSafetyStock + "safetyStockWeeks "
							+ safetyStockWeeks + "safetyStockDays " + safetyStockDays + "minStockAfterCapping "
							+ minStockAfterCapping + "maxStockWeeks " + maxStockWeeks + "minStockAfterCappingCs "
							+ minStockAfterCappingCs + "maxStockCs " + maxStockCs + "currentSsValue " + currentSsValue
							+ "proposedIpmSsValue " + proposedIpmSsValue + "minNormWeeks " + minNormWeeks
							+ "maxNormWeeks " + maxNormWeeks + "minStock " + minStock + "maxStock " + maxStock
							+ "avgCycleStock " + avgCycleStock);

					//String sql5 = "insert into IPM_MODEL values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					String sql5 = "insert into IPM_MODEL values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					pstmt2 = conn.prepareStatement(sql5);
					pstmt2.setString(1, SKU);
					pstmt2.setString(2, skuName);
					pstmt2.setString(3, location1);
					pstmt2.setString(4, locationType);
					pstmt2.setString(5, materialLocation);
					pstmt2.setString(6, skuClassiication);
					pstmt2.setString(7, source);
					pstmt2.setString(8, category);
					pstmt2.setInt(9, serviceLevel);
					pstmt2.setFloat(10, avgWeeklyDemand);
					pstmt2.setFloat(11, sdfePerc1);
					pstmt2.setDouble(12, sdfe);
					pstmt2.setDouble(13, lotSize);
					pstmt2.setInt(14, orValue);
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
					pstmt2.setLong(28, safetyStockDays);
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
					 /* Timestamp Changes - Start*/
			         pstmt2.setTimestamp(42, currentDate);
			         /* Timestamp Changes - End*/
			         
					pstmt2.executeUpdate();

					System.out.println("Insertion Completed");

					/*
					 * String sql6="SELECT * from ipm_model"; st3 =
					 * conn.createStatement(); rs3 = st3.executeQuery(sql6);
					 * ResultSetMetaData metadata1 = rs3.getMetaData(); int
					 * numberOfColumns1 = metadata1.getColumnCount();
					 * 
					 * while(rs2.next()) { for(int i=0; i<=numberOfColumns1;
					 * i++) { System.out.println(); } }
					 */
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
				pstmt.close();
				pstmt1.close();
				pstmt.close();
				rs.close();
				rs1.close();
				rs2.close();
				st.close();
				st1.close();
				st2.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*static double Math.round(double val) {
        DecimalFormat df2 = new DecimalFormat("###.##");
    return Double.valueOf(df2.format(val));
}*/
}
