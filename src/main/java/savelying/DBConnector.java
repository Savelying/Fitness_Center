package savelying;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
	public static Connection getDbConnect(String dbName) throws ClassNotFoundException, SQLException {
		String connectUrl = "jdbc:mysql://localhost:3306/";
		Class.forName("com.mysql.cj.jdbc.Driver");

		return DriverManager.getConnection(connectUrl + dbName, "root", "qwerty");
	}

	public static Connection getServConnect() throws ClassNotFoundException, SQLException {
		String connectUrl = "jdbc:mysql://localhost:3306/";
		Class.forName("com.mysql.cj.jdbc.Driver");

		return DriverManager.getConnection(connectUrl, "root", "qwerty");
	}
}
