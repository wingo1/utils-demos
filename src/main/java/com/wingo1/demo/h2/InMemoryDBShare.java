package com.wingo1.demo.h2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InMemoryDBShare {

	public static void main(String[] args) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:h2:mem:db1", "sa", "");
		// 指定端口是关键
		final String[] arg = new String[] { "-tcpPort", "8092", "-tcpAllowOthers" };
		org.h2.tools.Server server = org.h2.tools.Server.createTcpServer(arg).start();
		PreparedStatement prepareStatement = conn
				.prepareStatement("CREATE TABLE IF NOT EXISTS TEST1(ID INT PRIMARY KEY, NAME VARCHAR(255));");
		prepareStatement.execute();
		conn.commit();
		conn.close();

	}

}
