package com.wingo1.example.shapefile.map.utils;

public class StringUtils extends org.apache.commons.lang3.StringUtils {
	public static boolean isEmpty(Object object) {
		return (object == null || "".equals(object));
	}

}
