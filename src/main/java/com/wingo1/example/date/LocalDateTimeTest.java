package com.wingo1.example.date;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Locale;

public class LocalDateTimeTest {

	public static void main(String[] args) {
		// 通过设置
		LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 24, 02, 30, 0);
		// 时区转换
		ZonedDateTime withZoneSameInstant = localDateTime.atZone(ZoneId.of("GMT+8"))
				.withZoneSameInstant(ZoneId.of("UTC"));
		withZoneSameInstant.toLocalDateTime().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CHINA);
		// 通过格式化
		DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
		LocalDateTime d = LocalDateTime.parse("2019-11-12-10:14", ofPattern);
		// 当前时间
		LocalDateTime date = LocalDateTime.now();
		// 计算时间差
		System.out.println(Duration.between(d, date).toDays());
		// 调整
		d.withHour(6);
		/********************** 转换 *********************************/
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
