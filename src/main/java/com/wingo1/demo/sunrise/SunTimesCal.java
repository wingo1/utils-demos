package com.wingo1.demo.sunrise;

import java.time.ZonedDateTime;

import org.shredzone.commons.suncalc.SunTimes;

public class SunTimesCal {

	public static void main(String[] args) {
		ZonedDateTime dateTime = ZonedDateTime.now(); // date, time and timezone of calculation
		double lat = 30.57390d; // geolocation
		double lng = 103.94800;
		SunTimes times = SunTimes.compute().on(dateTime) // set a date
				.at(lat, lng) // set a location
				.execute(); // get the results
		System.out.println("Sunrise: " + times.getRise());
		System.out.println("Sunset: " + times.getSet());

	}

}
