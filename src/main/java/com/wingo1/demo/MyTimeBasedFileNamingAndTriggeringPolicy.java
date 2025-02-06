package com.wingo1.demo;

import java.text.SimpleDateFormat;
import java.util.Date;

import ch.qos.logback.core.joran.spi.NoAutoStart;

@NoAutoStart
public class MyTimeBasedFileNamingAndTriggeringPolicy<E>
		extends ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy<E> {

	@Override
	public String getElapsedPeriodsFileName() {
		String elapsedPeriodsFileName2 = super.getElapsedPeriodsFileName();
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		String replace = elapsedPeriodsFileName2.replace("[CDATC-ENDTIME]", sdf.format(new Date()));
		return replace;
	}

}
