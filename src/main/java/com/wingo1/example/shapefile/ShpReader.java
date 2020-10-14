package com.wingo1.example.shapefile;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.store.ContentFeatureCollection;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

public class ShpReader {

	public static void main(String[] args) throws Exception {

		ShapefileDataStore shapefileDataStore = new ShapefileDataStore(ShpReader.class.getResource("standsno.shp"));
		shapefileDataStore.setCharset(Charset.forName("GBK"));
		ContentFeatureSource featureSource = shapefileDataStore.getFeatureSource();
		ContentFeatureCollection features = featureSource.getFeatures();
		SimpleFeatureType schema = featureSource.getSchema();
		// 读取prj的映射
		CoordinateReferenceSystem sourceCRS = schema.getCoordinateReferenceSystem();
		CoordinateReferenceSystem targetCRS = DefaultGeographicCRS.WGS84;
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		System.out.println(features.size());
		SimpleFeatureIterator featureIterator = features.features();
		while (featureIterator.hasNext()) {
			SimpleFeature feature = featureIterator.next();
			Collection<Property> p = feature.getProperties();
			Iterator<Property> it = p.iterator();
			while (it.hasNext()) {
				Property pro = it.next();
				System.out.println(pro.getName() + " = " + pro.getValue());

			}
			// 坐标转换，转为WG84
			Geometry transform2 = JTS.transform((Geometry) feature.getProperty("the_geom").getValue(), transform);
			System.out.println("WG84:" + transform2);
		}

	}

}
