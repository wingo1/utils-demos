package com.wingo1.example.shapefile.map;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.store.ContentFeatureCollection;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.cdatc.shapefile.utils.DmsAndDeg;
import com.cdatc.shapefile.xml.Content;
import com.cdatc.shapefile.xml.SysMapPoint;
import com.cdatc.shapefile.xml.SysMapPolygon;

public class ShapeFileReaderForbitPolygon {

	public static void main(String[] args) throws Exception {
		File dir = new File("E:\\Temp\\双流机坪模块化施工");
		File[] listFiles = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname.getName().endsWith(".shp")) {
					return true;
				}
				return false;
			}
		});
		Content content = new Content();
		content.polygons = new ArrayList<>();
		for (File file : listFiles) {
			process(content, file);
		}
		String convertToXml = convertToXml(content);
		FileWriter fileWriter = new FileWriter("./fobit_out");
		fileWriter.write(convertToXml);
		fileWriter.close();
		System.out.println("done");
	}

	private static void process(Content content, File file) throws Exception {
		ShapefileDataStore shapefileDataStore = new ShapefileDataStore(file.toURI().toURL());
		ContentFeatureSource featureSource = shapefileDataStore.getFeatureSource();
		ContentFeatureCollection features = featureSource.getFeatures();
		SimpleFeatureType schema = featureSource.getSchema();
		CoordinateReferenceSystem sourceCRS = schema.getCoordinateReferenceSystem();
		CoordinateReferenceSystem targetCRS = DefaultGeographicCRS.WGS84;
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		System.out.println(features.size());
		SimpleFeatureIterator featureIterator = features.features();
		try {

			while (featureIterator.hasNext()) {
				SimpleFeature feature = featureIterator.next();
				MultiPolygon gw84geometry = (MultiPolygon) JTS
						.transform((Geometry) feature.getProperty("the_geom").getValue(), transform);
				Polygon polygon = (Polygon) gw84geometry.getGeometryN(0);
				content.polygons.add(fillPolygon(polygon, file.getName()));
			}
		} finally {
			featureIterator.close();
		}
	}

	private static SysMapPolygon fillPolygon(Polygon polygon, String name) {
		SysMapPolygon _polygon = new SysMapPolygon();
		_polygon.setName(name);
		_polygon.setPoints(new ArrayList<>());
		Coordinate[] coordinates = polygon.getCoordinates();
		for (Coordinate point : coordinates) {
			SysMapPoint point2 = new SysMapPoint();
			point2.setLongitude("E" + DmsAndDeg.degToDms(point.getX()));
			point2.setLatitude("N" + DmsAndDeg.degToDms(point.getY()));
			_polygon.getPoints().add(point2);
		}
		return _polygon;
	}

	private static String convertToXml(Object obj) {
		// 创建输出流
		StringWriter sw = new StringWriter();
		try {
			// 利用jdk中自带的转换类实现
			JAXBContext context = JAXBContext.newInstance(obj.getClass());

			Marshaller marshaller = context.createMarshaller();
			// 格式化xml输出的格式
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			// 将对象转换成输出流形式的xml
			marshaller.marshal(obj, sw);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
}
