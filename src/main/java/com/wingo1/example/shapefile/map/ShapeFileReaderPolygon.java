package com.wingo1.example.shapefile.map;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.store.ContentFeatureCollection;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.wingo1.example.shapefile.map.utils.DmsAndDeg;
import com.wingo1.example.shapefile.map.xml.Content;
import com.wingo1.example.shapefile.map.xml.SysMapPoint;
import com.wingo1.example.shapefile.map.xml.SysMapText;

public class ShapeFileReaderPolygon {

	public static void main(String[] args) throws Exception {
		ShapefileDataStore shapefileDataStore = new ShapefileDataStore(
				ShapeFileReaderPolygon.class.getResource("/shapefile/roadinside.shp"));
		ContentFeatureSource featureSource = shapefileDataStore.getFeatureSource();
		ContentFeatureCollection features = featureSource.getFeatures();
		SimpleFeatureType schema = featureSource.getSchema();
		CoordinateReferenceSystem sourceCRS = schema.getCoordinateReferenceSystem();
		CoordinateReferenceSystem targetCRS = DefaultGeographicCRS.WGS84;
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		System.out.println(features.size());
		SimpleFeatureIterator featureIterator = features.features();
		Content content = new Content();
		content.texts = new ArrayList<>();
		int count = 1;
		while (featureIterator.hasNext()) {
			SimpleFeature feature = featureIterator.next();
			feature.getDefaultGeometry();
			Collection<Property> p = feature.getProperties();
			Iterator<Property> it = p.iterator();
			while (it.hasNext()) {
				Property pro = it.next();
				System.out.println(pro.getName() + " = " + pro.getValue());

			}

		}
//		String convertToXml = convertToXml(content);
//		System.out.println(convertToXml);
//		FileWriter fileWriter = new FileWriter("./outcontent");
//		fileWriter.write(convertToXml);
//		fileWriter.close();

	}

	private static SysMapText fill(int count, Point point, String name, String code) {
		SysMapText text = new SysMapText();
		text.setId(String.valueOf(count));
		text.setName("standno." + name);
		text.setStr(code);
		SysMapPoint point2 = new SysMapPoint();
		point2.setLongitude("E" + DmsAndDeg.degToDms(point.getX()));
		point2.setLatitude("N" + DmsAndDeg.degToDms(point.getY()));
		text.setPoint(point2);
		return text;
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
