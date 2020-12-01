package com.wingo1.example.shapefile.map;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import org.geotools.swing.data.JFileDataStoreChooser;
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
 * 必须是gmp转的shp（属性全的），必须要有prj投影文件
 * 
 * @author cdatc-wingo1
 *
 */
public class TaxlShpToGmp {
	private Map<Object, Integer> vertexMap = new HashMap<Object, Integer>();
	Integer maxLineName = 0;
	Integer maxPointName = 0;

	public void excute() throws Exception {
		File file = JFileDataStoreChooser.showOpenFile("shp", new File("d:/"), null);
		if (file == null) {
			return;
		}
		// shapefile
		ShapefileDataStore shapefileDataStore = new ShapefileDataStore(file.toURI().toURL());
		shapefileDataStore.setCharset(Charset.forName("GBK"));
		ContentFeatureSource featureSource = shapefileDataStore.getFeatureSource();
		ContentFeatureCollection features = featureSource.getFeatures();
		SimpleFeatureType schema = featureSource.getSchema();
		CoordinateReferenceSystem sourceCRS = schema.getCoordinateReferenceSystem();
		CoordinateReferenceSystem targetCRS = DefaultGeographicCRS.WGS84;
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		System.out.println("滑行线feature总数:" + features.size());
		SimpleFeatureIterator featureIterator = features.features();
		Taxl taxl = new Taxl();
		taxl.setExtinfoTaxiLines(new ArrayList<>());
		taxl.setPolylines(new ArrayList<>());
		while (featureIterator.hasNext()) {
			SimpleFeature feature = featureIterator.next();
			Integer lineCount = maxLineName++;
			Geometry geom = (Geometry) feature.getProperty("the_geom").getValue();
			if (geom == null) {
				System.out.println("'the_geom'图形为null，该feature toSring:\n" + feature.toString());
				return;
			}
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
		featureIterator.close();
		System.out.println("生成完毕，查看taxlout.xml文件");

	}

	private SysMapPolyline fillLine(Integer lineCount, SimpleFeature feature, LineString lineString) {
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
		try {
			attributes2Extinfo(extinfoTaxiLine, feature);
		} catch (Exception e) {
			System.out.println("shp extra信息！出错");
			e.printStackTrace();
		}
		return extinfoTaxiLine;
	}

	public static void attributes2Extinfo(ExtinfoTaxiLine extinfoTaxiLine, SimpleFeature feature) throws Exception {
		extinfoTaxiLine.setType(
				feature.getProperty("type") == null || StringUtils.isEmpty(feature.getProperty("type").getValue())
						? "normal"
						: feature.getAttribute("type").toString());
		extinfoTaxiLine.setTaxiname(feature.getProperty("taxiname") == null
				|| StringUtils.isEmpty(feature.getProperty("taxiname").getValue()) ? ""
						: (String) feature.getProperty("taxiname").getValue());
		extinfoTaxiLine.setTypename(feature.getProperty("typename") == null
				|| StringUtils.isEmpty(feature.getProperty("typename").getValue()) ? ""
						: (String) feature.getProperty("typename").getValue());
		extinfoTaxiLine.setDirect(
				feature.getProperty("direct") == null || StringUtils.isEmpty(feature.getProperty("direct").getValue())
						? "Both"
						: (String) feature.getProperty("direct").getValue());
		extinfoTaxiLine.setWinglength(feature.getProperty("winglength") == null
				|| StringUtils.isEmpty(feature.getProperty("winglength").getValue()) ? 0f
						: Float.valueOf((String) feature.getProperty("winglength").getValue()));
		extinfoTaxiLine.setPcn(
				feature.getProperty("pcn") == null || StringUtils.isEmpty(feature.getProperty("pcn").getValue()) ? 0f
						: Float.valueOf((String) feature.getProperty("pcn").getValue()));
		extinfoTaxiLine.setMainwheel(feature.getProperty("mainwheel") == null
				|| StringUtils.isEmpty(feature.getProperty("mainwheel").getValue()) ? 0f
						: Float.valueOf((String) feature.getProperty("mainwheel").getValue()));
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
