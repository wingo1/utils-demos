package com.wingo1.demo.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InMemoryDBShare {

	public static void main(String[] args) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:h2:mem:db1", "sa", "");
		conn.setAutoCommit(true);
		// 指定端口是关键
		final String[] arg = new String[] { "-tcpPort", "8092", "-tcpAllowOthers" };
		org.h2.tools.Server server = org.h2.tools.Server.createTcpServer(arg).start();
		PreparedStatement prepareStatement = conn.prepareStatement(
				"CREATE TABLE IF NOT EXISTS TEST1(ID INT AUTO_INCREMENT PRIMARY KEY, NAME VARCHAR(255));");
		prepareStatement.execute();
		conn.prepareStatement("insert into test1(name) values('hello');").executeUpdate();
		conn.prepareStatement("insert into test1(name) values('world');").executeUpdate();
		ResultSet executeQuery = conn.prepareStatement("select * from test1; ").executeQuery();
		while (executeQuery.next()) {
			int int1 = executeQuery.getInt("ID");
			String string = executeQuery.getString(2);
			System.out.println(int1 + "-" + string);
		}
		conn.close();

	}

}
