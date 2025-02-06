package com.wingo1.demo;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackTest {
	private static Logger logger = LoggerFactory.getLogger(LogbackTest.class);

	public static void main(String[] args) throws InterruptedException {
		while (true) {
			logger.info("now time:{}", new Date());
			TimeUnit.SECONDS.sleep(1);
		}
	}
}
