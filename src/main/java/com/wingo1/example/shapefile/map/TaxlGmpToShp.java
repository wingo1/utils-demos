package com.wingo1.example.shapefile.map;

import java.io.File;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.wingo1.example.shapefile.map.utils.DmsAndDeg;
import com.wingo1.example.shapefile.map.xml.ExtinfoTaxiLine;
import com.wingo1.example.shapefile.map.xml.SysMapPoint;
import com.wingo1.example.shapefile.map.xml.SysMapPolyline;
import com.wingo1.example.shapefile.map.xml.Taxl;

/**
 * graphics gmp to shp
 * 
 * @author cdatc-wingo1
 *
 */
public class TaxlGmpToShp {
	private String CRS_STRING = "32648";

	public void excute(String crsCode) throws Exception {
		if (crsCode != null && !"".equals(crsCode)) {
			CRS_STRING = crsCode;
		}
		File file = JFileDataStoreChooser.showOpenFile("gmp", new File("d:/"), null);
		if (file == null) {
			return;
		}
		Taxl taxl = gmpToObj(file);
		System.out.println("载入gmp成功...");
		CoordinateReferenceSystem sourceCRS = DefaultGeographicCRS.WGS84;
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:" + CRS_STRING, true);
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		GeometryFactory geometryFactory = new GeometryFactory();
		final SimpleFeatureType TYPE = DataUtilities.createType("taxiLine",
				"the_geom:MultiLineString:srid=" + CRS_STRING + "," + "type:String,taxiname:String,"
						+ "typename:String,direct:String,winglength:String,pcn:String,mainwheel:String");
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
		Map<String, ExtinfoTaxiLine> extinfoMap = new HashMap<>();
		taxl.getExtinfoTaxiLines().forEach(line -> {
			extinfoMap.put(line.getName(), line);
		});
		// 构建SHP
		List<SimpleFeature> features = new ArrayList<>();
		for (SysMapPolyline polyline : taxl.getPolylines()) {
			Coordinate[] coordinates = new Coordinate[polyline.getPoints().size()];
			for (int i = 0; i < polyline.getPoints().size(); i++) {
				SysMapPoint point = polyline.getPoints().get(i);
				coordinates[i] = new Coordinate();
				try {
					JTS.transform(new Coordinate(DmsAndDeg.dmsTodeg(point.getLongitude().substring(1)),
							DmsAndDeg.dmsTodeg(point.getLatitude().substring(1))), coordinates[i], transform);
				} catch (TransformException e) {
					e.printStackTrace();
				}
			}
			LineString createLineString = geometryFactory.createLineString(coordinates);
			ExtinfoTaxiLine extinfo = extinfoMap.get(polyline.getName());
			MultiLineString createMultiLineString = geometryFactory
					.createMultiLineString(new LineString[] { createLineString });
			if (extinfo != null) {
				featureBuilder.addAll(new Object[] { createMultiLineString, extinfo.getType(), extinfo.getTaxiname(),
						extinfo.getTypename(), extinfo.getDirect(), extinfo.getWinglength(), extinfo.getPcn(),
						extinfo.getMainwheel() });
			} else {
				featureBuilder.addAll(new Object[] { createMultiLineString, "normal", "", "", "Both", 0, 0, 0 });
			}
			SimpleFeature feature = featureBuilder.buildFeature(null);
			features.add(feature);

		}
		System.out.println("处理完毕，准备保存shp...");
		// 保存shp
		File newFile = getNewShapeFile(file);
		if (newFile == null) {
			System.out.println("保存shp文件出错！");
			return;
		}

		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

		Map<String, Serializable> params = new HashMap<>();
		params.put("url", newFile.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);

		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);

		/*
		 * TYPE is used as a template to describe the file contents
		 */
		newDataStore.createSchema(TYPE);
		/*
		 * Write the features to the shapefile
		 */
		Transaction transaction = new DefaultTransaction("create");

		String typeName = newDataStore.getTypeNames()[0];
		SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
		SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();
		/*
		 * The Shapefile format has a couple limitations: - "the_geom" is always first,
		 * and used for the geometry attribute name - "the_geom" must be of type Point,
		 * MultiPoint, MuiltiLineString, MultiPolygon - Attribute names are limited in
		 * length - Not all data types are supported (example Timestamp represented as
		 * Date)
		 *
		 * Each data store has different limitations so check the resulting
		 * SimpleFeatureType.
		 */

		if (featureSource instanceof SimpleFeatureStore) {
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
			/*
			 * SimpleFeatureStore has a method to add features from a
			 * SimpleFeatureCollection object, so we use the ListFeatureCollection class to
			 * wrap our list of features.
			 */
			SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, features);
			featureStore.setTransaction(transaction);
			try {
				featureStore.addFeatures(collection);
				transaction.commit();
			} catch (Exception problem) {
				problem.printStackTrace();
				transaction.rollback();
			} finally {
				transaction.close();
			}
			System.out.println("保存完毕！");
			return; // success!
		} else {
			System.out.println(typeName + " does not support read/write access");
			return;
		}
	}

	private static File getNewShapeFile(File csvFile) {
		String path = csvFile.getAbsolutePath();
		String newPath = path.substring(0, path.length() - 4) + ".shp";

		JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
		chooser.setDialogTitle("Save shapefile");
		chooser.setSelectedFile(new File(newPath));

		int returnVal = chooser.showSaveDialog(null);

		if (returnVal != JFileDataStoreChooser.APPROVE_OPTION) {
			return null;
		}

		File newFile = chooser.getSelectedFile();
		if (newFile.equals(csvFile)) {
			return null;
		}

		return newFile;
	}

	private static Taxl gmpToObj(File file) throws Exception {
		SAXReader reader = new SAXReader();
		Document read = reader.read(file);
		String asXML = read.selectSingleNode("/map/layer[@name='TAXL']").asXML();
		JAXBContext context = JAXBContext.newInstance(Taxl.class);
		// 进行将Xml转成对象的核心接口
		Unmarshaller unmarshaller = context.createUnmarshaller();
		StringReader sr = new StringReader(asXML);
		return (Taxl) unmarshaller.unmarshal(sr);
	}

}
