package com.wingo1.demo.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RemoteServerMode {

	public static void main(String[] args) throws Exception {
		final String[] arg = new String[] { "-tcpPort", "8092", "-tcpAllowOthers", "-tcpDaemon" };
		org.h2.tools.Server server = org.h2.tools.Server.createTcpServer(arg).start();
		Connection conn = DriverManager.getConnection("jdbc:h2:file:./test", "sa", "");
		conn.setAutoCommit(true);
		PreparedStatement prepareStatement = conn.prepareStatement(
				"CREATE TABLE IF NOT EXISTS TEST2(ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR(255));");
		prepareStatement.execute();
		conn.prepareStatement("insert into TEST2(name) values('hello');").executeUpdate();
		conn.prepareStatement("insert into TEST2(name) values('world');").executeUpdate();

		Connection conn1 = DriverManager.getConnection("jdbc:h2:tcp://localhost:8092/./test", "sa", "");
		ResultSet executeQuery = conn1.prepareStatement("select * from TEST2; ").executeQuery();
		while (executeQuery.next()) {
			int int1 = executeQuery.getInt("ID");
			String string = executeQuery.getString(2);
			System.out.println(int1 + "-" + string);
		}
		conn.close();

	}

}
