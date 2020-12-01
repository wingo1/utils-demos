package com.wingo1.example.shapefile.map.utils;

import java.text.DecimalFormat;

/**
 * 度分秒和十进制转换
 * 
 * @author cdatc-wingo1
 *
 */
public class DmsAndDeg {
	public static String degToDms(double f) {
		int deg = (int) f;
		int min = (int) ((f - deg) * 60);
		float sec = (float) (((f - deg) * 60 - min) * 60);
		DecimalFormat minDf = new DecimalFormat("00");
		DecimalFormat secDf = new DecimalFormat("00.000");
		return deg + "" + minDf.format(min) + secDf.format(sec);
	}

	public static double dmsTodeg(String dms) {
		int indexOf = dms.indexOf(".");
		if (indexOf == -1) {
			indexOf = dms.length();
		}
		double sec = Double.valueOf(dms.substring(indexOf - 2));
		int min = Integer.valueOf(dms.substring(indexOf - 4, indexOf - 2));
		int deg = Integer.valueOf(dms.substring(0, indexOf - 4));
		return deg + min / 60D + sec / 3600D;
	}

	public static void main(String[] args) {
		System.out.println(dmsTodeg("1035648.278"));
	}

}
