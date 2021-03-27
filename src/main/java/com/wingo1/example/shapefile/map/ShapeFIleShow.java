package com.wingo1.example.shapefile.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.sld.v1_1.SLDConfiguration;
import org.geotools.styling.NamedLayer;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyledLayerDescriptor;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.dialog.JExceptionReporter;
import org.geotools.swing.styling.JSimpleStyleDialog;
import org.geotools.xsd.Configuration;
import org.geotools.xsd.Parser;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory;

public class ShapeFIleShow {
	static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
	static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

	public static void main(String[] args) {
		// 1.数据源选择 shp扩展类型的
		File file = JFileDataStoreChooser.showOpenFile("shp", null);
		if (file == null) {
			return;
		}

		// 2.得到打开的文件的数据源
		FileDataStore store = null;
		try {
			store = FileDataStoreFinder.getDataStore(file);
			// 3.设置数据源的编码，防止中文乱码
			((ShapefileDataStore) store).setCharset(Charset.forName("UTF-8"));

			// 4.以java对象的方式访问地理信息
			SimpleFeatureSource featureSource = store.getFeatureSource();

			// 5.创建映射内容，并将我们的shapfile添加进去
			MapContent mapContent = new MapContent();

			// 6.设置容器的标题
			mapContent.setTitle("Appleyk's GeoTools");

			// 7.创建简单样式
			Style style = createStyle(file, featureSource);

			// 8.显示【shapfile地理信息+样式】
			Layer layer = new FeatureLayer(featureSource, style);

			// 9.将显示添加进map容器
			mapContent.addLayer(layer);

			// 10.窗体打开，高大尚的操作开始
			JMapFrame.showMap(mapContent);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static Style createStyle(File file, FeatureSource featureSource) {
		File sld = toSLDFile(file);
		if (sld != null) {
			return createFromSLD(sld);
		}

		SimpleFeatureType schema = (SimpleFeatureType) featureSource.getSchema();
		return JSimpleStyleDialog.showDialog(null, schema);
	}

	public static File toSLDFile(File file) {
		String path = file.getAbsolutePath();
		String base = path.substring(0, path.length() - 4);
		String newPath = base + ".sld";
		File sld = new File(newPath);
		if (sld.exists()) {
			return sld;
		}
		newPath = base + ".SLD";
		sld = new File(newPath);
		if (sld.exists()) {
			return sld;
		}
		return null;
	}

	/** Create a Style object from a definition in a SLD document */
	private static Style createFromSLD(File sld) {
		try {
			/*
			 * SLDParser stylereader = new SLDParser(styleFactory, sld.toURI().toURL());
			 * Style[] style = stylereader.readXML(); return style[0];
			 */
			// v1.1 sld
			Configuration config = new SLDConfiguration();
			Parser parser = new Parser(config);
			StyledLayerDescriptor sldDesc = (StyledLayerDescriptor) parser.parse(new FileInputStream(sld));
			NamedLayer styledLayer = (NamedLayer) sldDesc.getStyledLayers()[0];
			return styledLayer.getStyles()[0];
		} catch (Exception e) {
			JExceptionReporter.showDialog(e, "Problem creating style");
		}
		return null;
	}
}
