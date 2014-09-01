package org.big.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionHelper {

	public static Connection getConnection()
	{
		String connectionUrl = "jdbc:sqlserver://159.226.67.81:1433;" +
		   "databaseName=csdbsnap;user=sa;password=Big62553068;";
		Connection con=null;
		try{
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(connectionUrl);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return con;
	}
}
