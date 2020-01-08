package com.wingo1.example.date;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class LocalDateTimeTest {

	public static void main(String[] args) {
		// 通过设置
		LocalDateTime localDateTime = LocalDateTime.of(2019, 12, 9, 16, 30, 0);
		// 通过格式化
		DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
		LocalDateTime d = LocalDateTime.parse("2019-11-12-10:14", ofPattern);
		// 当前时间
		LocalDate date = LocalDate.now();
		// 转换
		LocalDate localDate = localDateTime.toLocalDate();
		LocalTime localTime = localDateTime.toLocalTime();
		DayOfWeek dayOfWeek = DayOfWeek.valueOf("Wednesday".toUpperCase());
		Calendar calendar = Calendar.getInstance();
		calendar.set(2019, 11, 9, 16, 30, 0);
		// 得到long
		System.out.println(calendar.getTimeInMillis());
		System.out.println(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
		// long转datetime
		System.out.println(
				LocalDateTime.ofInstant(Instant.ofEpochMilli(calendar.getTimeInMillis()), ZoneId.systemDefault()));

	}

}
