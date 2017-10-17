package com.unilever.unilever.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
	
	public static Connection createDbConn() {
		System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		Connection conn = null;

		try {

			conn = DriverManager.getConnection("jdbc:postgresql://hanno.db.elephantsql.com:5432/vnepifvr", "vnepifvr",
					"n5CEh6y0w-zoORD2Hw1X_WpCT2yvy4wR");

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
		}
		return conn;
	}

}
