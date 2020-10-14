package com.wingo1.demo.h2;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;

import org.h2.jdbcx.JdbcConnectionPool;

public class ConnectionPoolTest {

	public static void main(String[] args) throws Exception {
		JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:h2:mem:test_mem", "sa", "sa");
		for (int i = 0; i < 100; i++) {
			try (Connection conn = cp.getConnection();) {
				System.out.println(i + ":" + String.valueOf(conn.getAutoCommit()));
				conn.setAutoCommit(false);
			}
			TimeUnit.SECONDS.sleep(1);
		}

	}

}
