package com.wingo1.example.shapefile.map.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

public class DmsDistance {
	private static String CRS_STRING = "32648";

	public static double distance(String dmsLon1, String dmsLat1, String dmsLon2, String dmsLat2) throws Exception {
		CoordinateReferenceSystem sourceCRS = DefaultGeographicCRS.WGS84;
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:" + CRS_STRING, true);
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		Coordinate coordinate1 = new Coordinate();
		JTS.transform(
				new Coordinate(DmsAndDeg.dmsTodeg(dmsLon1.substring(1)), DmsAndDeg.dmsTodeg(dmsLat1.substring(1))),
				coordinate1, transform);
		Coordinate coordinate2 = new Coordinate();
		JTS.transform(
				new Coordinate(DmsAndDeg.dmsTodeg(dmsLon2.substring(1)), DmsAndDeg.dmsTodeg(dmsLat2.substring(1))),
				coordinate2, transform);

		return coordinate1.distance(coordinate2);

	}

	public static void main(String[] args) throws Exception {
		DecimalFormat format = new DecimalFormat("#");
		FileReader fileReader = new FileReader("C:\\Users\\cdatc-wingo1\\Desktop\\origin.txt");
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String str1 = null;
		List<Double> result = new ArrayList<Double>();
		while ((str1 = bufferedReader.readLine()) != null) {
			String str2 = bufferedReader.readLine();
			String[] split = str1.split("\t");
			String[] split2 = str2.split("\t");
			double distance = distance(split[0], split2[0], "E" + split[1], "N" + split2[1]);
			result.add(distance);
		}
		for (Double double1 : result) {

			System.out.println(format.format(double1));
		}

	}

}
