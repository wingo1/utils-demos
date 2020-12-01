package com.wingo1.example.shapefile.map;

import java.io.FileWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.AbstractAttribute;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.store.ContentFeatureCollection;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.wingo1.example.shapefile.map.utils.DmsAndDeg;
import com.wingo1.example.shapefile.map.utils.StringUtils;
import com.wingo1.example.shapefile.map.xml.ExtinfoTaxiLine;
import com.wingo1.example.shapefile.map.xml.SysMapPoint;
import com.wingo1.example.shapefile.map.xml.SysMapPolyline;
import com.wingo1.example.shapefile.map.xml.Taxl;

/**
 * 滑行道
 * 
 * @author cdatc-wingo1
 *
 */
public class ShapeFileReaderTaxiLine {
	private static Map<Object, Integer> vertexMap = new HashMap<Object, Integer>();
	static Integer maxLineName = 0;
	static Integer maxPointName = 0;

	public static void main(String[] args) throws Exception {
		// read gmp
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(ShapeFileReaderTaxiLine.class.getResource("/graphics.gmp"));
		List<AbstractAttribute> polylineNames = document.selectNodes("//layer[@name='TAXL']//polyline/@name");
		maxLineName = polylineNames.stream().filter(x -> !StringUtils.isEmpty(x.getValue()))
				.map(x -> Integer.valueOf(x.getValue().split("\\.")[1])).max((a, b) -> a - b).get();
		System.out.println("taxiline." + maxLineName++);
		List<AbstractAttribute> PointNames = document.selectNodes("//layer[@name='TAXL']//point/@name");
		maxPointName = PointNames.stream().filter(x -> !StringUtils.isEmpty(x.getValue()))
				.map(x -> Integer.valueOf(x.getValue())).max((a, b) -> a - b).get();
		System.out.println("point." + maxPointName++);
		// shapefile
		ShapefileDataStore shapefileDataStore = new ShapefileDataStore(
				ShapeFileReaderTaxiLine.class.getResource("/shapefile/newTaxiway.shp"));
		shapefileDataStore.setCharset(Charset.forName("GBK"));
		ContentFeatureSource featureSource = shapefileDataStore.getFeatureSource();
		ContentFeatureCollection features = featureSource.getFeatures();
		SimpleFeatureType schema = featureSource.getSchema();
		CoordinateReferenceSystem sourceCRS = schema.getCoordinateReferenceSystem();
		CoordinateReferenceSystem targetCRS = DefaultGeographicCRS.WGS84;
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		System.out.println(features.size());
		SimpleFeatureIterator featureIterator = features.features();
		Taxl taxl = new Taxl();
		taxl.setExtinfoTaxiLines(new ArrayList<>());
		taxl.setPolylines(new ArrayList<>());
		while (featureIterator.hasNext()) {
			SimpleFeature feature = featureIterator.next();
			Integer lineCount = maxLineName++;
			MultiLineString gw84geometry = (MultiLineString) JTS
					.transform((Geometry) feature.getProperty("the_geom").getValue(), transform);
			LineString lineString = (LineString) gw84geometry.getGeometryN(0);
			taxl.getPolylines().add(fillLine(lineCount, feature, lineString));
			taxl.getExtinfoTaxiLines().add(fillExtra(lineCount, feature));

		}
		String convertToXml = convertToXml(taxl);
		System.out.println(convertToXml);
		FileWriter fileWriter = new FileWriter("./taxlout.xml");
		fileWriter.write(convertToXml);
		fileWriter.close();

	}

	private static SysMapPolyline fillLine(Integer lineCount, SimpleFeature feature, LineString lineString) {
		SysMapPolyline line = new SysMapPolyline();
		line.setName("taxi." + lineCount);
		line.setPoints(new ArrayList<>());
		for (int i = 0; i < lineString.getNumPoints(); i++) {
			Coordinate coordinateN = lineString.getCoordinateN(i);
			Integer pointName = null;
			if (i == 0 || i == lineString.getNumPoints() - 1) {// 端点
				pointName = vertexMap.get(coordinateN);
				if (pointName == null) {
					vertexMap.put(coordinateN, maxPointName++);
					pointName = vertexMap.get(coordinateN);
				}

			}
			SysMapPoint point2 = new SysMapPoint();
			if (pointName != null) {
				point2.setName(String.valueOf(pointName));
			}
			point2.setLongitude("E" + DmsAndDeg.degToDms(coordinateN.getX()));
			point2.setLatitude("N" + DmsAndDeg.degToDms(coordinateN.getY()));
			line.getPoints().add(point2);
		}
		return line;
	}

	private static ExtinfoTaxiLine fillExtra(Integer lineCount, SimpleFeature feature) {
		ExtinfoTaxiLine extinfoTaxiLine = new ExtinfoTaxiLine();
		extinfoTaxiLine.setName("taxi." + lineCount);
		extinfoTaxiLine.setType(StringUtils.isEmpty(feature.getProperty("type").getValue()) ? "normal" : "stand");
		extinfoTaxiLine.setTaxiname(StringUtils.isEmpty(feature.getProperty("taxiname").getValue()) ? ""
				: (String) feature.getProperty("taxiname").getValue());
		extinfoTaxiLine.setTypename(StringUtils.isEmpty(feature.getProperty("typename").getValue()) ? ""
				: (String) feature.getProperty("typename").getValue());
		return extinfoTaxiLine;
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
