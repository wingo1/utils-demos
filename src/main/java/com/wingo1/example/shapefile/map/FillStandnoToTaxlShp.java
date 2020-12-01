package com.wingo1.example.shapefile.map;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.store.ContentFeatureCollection;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.wingo1.example.shapefile.map.xml.ExtinfoTaxiLine;

public class FillStandnoToTaxlShp {
	File initDir = new File("E:/");
	private String CRS_STRING = "32648";
	private final String MultiLineString = null;
	private ContentFeatureSource standnoSource;
	private ContentFeatureSource taxlSource;

	private Map<Geometry, ExtinfoTaxiLine> taxlMap = new HashMap<>();// 滑行道信息

	private Map<Coordinate, Integer> pointMap = new HashMap<>();// 记录下点

	private Map<String, Geometry> standGeometry = new HashMap<>();// String停机位名称机位

	private Map<String, List<Geometry>> standCandidates = new ConcurrentHashMap<>();// String停机位名称机位

	private boolean needAdjust = false;
	public void excute(String crs) throws Exception {
		CRS_STRING = crs;
		System.out.println("open standno...");
		standnoSource = readShp("打开停机位shp");
		System.out.println("open taxl...");
		taxlSource = readShp("打开滑行中线shp");
		if (standnoSource == null || taxlSource == null) {
			System.out.println("neither can be null!");
			return;
		}
		ContentFeatureCollection features = taxlSource.getFeatures();
		SimpleFeatureIterator featureIterator = features.features();
		while (featureIterator.hasNext()) {
			SimpleFeature feature = featureIterator.next();
			MultiLineString lineString = (MultiLineString) feature.getProperty("the_geom").getValue();
			Coordinate[] coordinates = lineString.getCoordinates();
			if (pointMap.get(coordinates[0]) == null) {
				pointMap.put(coordinates[0], 1);
			} else {
				pointMap.put(coordinates[0], pointMap.get(coordinates[0]) + 1);
			}
			if (pointMap.get(coordinates[coordinates.length - 1]) == null) {
				pointMap.put(coordinates[coordinates.length - 1], 1);
			} else {
				pointMap.put(coordinates[coordinates.length - 1],
						pointMap.get(coordinates[coordinates.length - 1]) + 1);
			}
			// 塞入原有属性并清空原有的stand
			ExtinfoTaxiLine extinfoTaxiLine = new ExtinfoTaxiLine();
			TaxlShpToGmp.attributes2Extinfo(extinfoTaxiLine, feature);
			if ("stand".equals(extinfoTaxiLine.getType())) {
				extinfoTaxiLine.setType("normal");
				extinfoTaxiLine.setTypename("");
			}
			taxlMap.put(lineString, extinfoTaxiLine);
		}
		System.out.println("taxl total:" + taxlMap.size());
		ContentFeatureCollection standfeatures = standnoSource.getFeatures();
		SimpleFeatureIterator standfeatureIterator = standfeatures.features();
		try {
		// 获取候选线
		while (standfeatureIterator.hasNext()) {
			SimpleFeature feature = standfeatureIterator.next();
			Point point = (Point) feature.getProperty("the_geom").getValue();
			List<Geometry> geometries = new CopyOnWriteArrayList<Geometry>();
			for (Geometry geo : taxlMap.keySet()) {
				if (geo.distance(point) > 28) {
					continue;
				}
				geometries.add(geo);
			}
				if (feature.getAttribute("code") == null) {
					System.out.println("停机位shp的code属性为空");
					return;
				}
			standGeometry.put(feature.getAttribute("code").toString(), point);
			standCandidates.put(feature.getAttribute("code").toString(), geometries);
		}
			System.out.println("stand size:" + standCandidates.size());
			recursion(standCandidates, standCandidates.size());
			if (needAdjust) {
				System.out.println("调整停机位图层后再试");
				return;
			}
			saveShp(taxlMap);
		} finally {
			featureIterator.close();
			standfeatureIterator.close();// TODO: handle finally clause
		}

	}

	// 递归填入,貌似用错了，可能用迭代更好
	private void recursion(Map<String, List<Geometry>> standCandidates, int originSize) {
		for (Map.Entry<String, List<Geometry>> entry : standCandidates.entrySet()) {
			List<Geometry> list = entry.getValue();
			if (list == null || list.size() == 0) {
				System.err.println("error:" + entry.getKey());
			}
			if (list.size() == 1) {// 只有一个的优先
				ExtinfoTaxiLine extinfoTaxiLine = taxlMap.get(list.get(0));
				extinfoTaxiLine.setType("stand");
				extinfoTaxiLine.setTypename(entry.getKey());
				standCandidates.remove(entry.getKey());
				continue;
			}

			// 去除被选了的
			for (Geometry geo : list) {
				if ("stand".equals(taxlMap.get(geo).getType())) {// 被优先的选了
					list.remove(geo);
					continue;
				}
			}
			if (list.size() == 0) {
				continue;
			}
			// 在同一方向，最近的优选
			Geometry point = standGeometry.get(entry.getKey());
			Double angle = null;
			boolean sameDirection = true;
			for (Geometry geo : list) {
				DistanceOp distanceOp = new DistanceOp(point, geo);
				double angle2 = Angle.angle(point.getCoordinate(), distanceOp.nearestPoints()[1]);
				if (angle == null) {
					angle = angle2;
					continue;
				}
				if (Angle.diff(angle, angle2) > Math.toRadians(90)) {
					sameDirection = false;
					break;
				}
			}
			if (sameDirection == true) {
				Geometry closest = null;
				for (Geometry geo : list) {
					if (closest == null) {
						closest = geo;
						continue;
					}
					if (point.distance(geo) < (point.distance(closest))) {
						closest = geo;
					}
				}
				ExtinfoTaxiLine extinfoTaxiLine = taxlMap.get(closest);
				extinfoTaxiLine.setType("stand");
				extinfoTaxiLine.setTypename(entry.getKey());
				standCandidates.remove(entry.getKey());
				continue;
			}
			// 有一端点没其它线的优选(唯一一个)
			List<Geometry> endGeometries = new ArrayList<>();
			for (Geometry geo : list) {
				Coordinate[] coordinates = geo.getCoordinates();
				if (pointMap.get(coordinates[0]) == 1 || (pointMap.get(coordinates[coordinates.length - 1]) == 1)) {
					endGeometries.add(geo);
				}
			}
			if (endGeometries.size() == 1) {
				ExtinfoTaxiLine extinfoTaxiLine = taxlMap.get(endGeometries.get(0));
				extinfoTaxiLine.setType("stand");
				extinfoTaxiLine.setTypename(entry.getKey());
				standCandidates.remove(entry.getKey());
			}

		}
		System.out.println("recursion:" + standCandidates.size());
		if (standCandidates.size() == originSize) {
			System.out.println("调整：" + standCandidates.keySet());
			needAdjust = true;
			return;
		}
		if (standCandidates.size() != 0) {
			recursion(standCandidates, standCandidates.size());
		}

	}

	private ContentFeatureSource readShp(String title) throws Exception {
		JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
		chooser.setCurrentDirectory(initDir);
		chooser.setDialogTitle(title);
		int returnVal = chooser.showOpenDialog(null);
		File file = chooser.getSelectedFile();

		if (file == null) {
			return null;
		}
		initDir = file.getParentFile();
		// shapefile
		ShapefileDataStore shapefileDataStore = new ShapefileDataStore(file.toURI().toURL());
		shapefileDataStore.setCharset(Charset.forName("GBK"));
		ContentFeatureSource featureSource = shapefileDataStore.getFeatureSource();
		return featureSource;

	}

	private void saveShp(Map<Geometry, ExtinfoTaxiLine> taxlMap) throws Exception {
		final SimpleFeatureType TYPE = DataUtilities.createType("taxiLine",
				"the_geom:MultiLineString:srid=" + CRS_STRING + "," + "type:String,taxiname:String,"
						+ "typename:String,direct:String,winglength:String,pcn:String,mainwheel:String");
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
		// for
		List<SimpleFeature> features = new ArrayList<>();
		for (Entry<Geometry, ExtinfoTaxiLine> entry : taxlMap.entrySet()) {
			ExtinfoTaxiLine extinfo = entry.getValue();
			featureBuilder.addAll(
					new Object[] { entry.getKey(), extinfo.getType(), extinfo.getTaxiname(), extinfo.getTypename(),
							extinfo.getDirect(), extinfo.getWinglength(), extinfo.getPcn(), extinfo.getMainwheel() });
			SimpleFeature feature = featureBuilder.buildFeature(null);
			features.add(feature);
		}

		// 保存shp
		File newFile = getNewShapeFile();
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

	private File getNewShapeFile() {

		JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
		chooser.setCurrentDirectory(initDir);
		chooser.setDialogTitle("Save shapefile");

		int returnVal = chooser.showSaveDialog(null);

		if (returnVal != JFileDataStoreChooser.APPROVE_OPTION) {
			// the user cancelled the dialog
			return null;
		}

		File newFile = chooser.getSelectedFile();

		return newFile;
	}

}
