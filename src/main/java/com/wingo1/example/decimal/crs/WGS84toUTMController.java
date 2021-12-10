package com.wingo1.example.decimal.crs;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.wingo1.example.decimal.DmsAndDeg;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class WGS84toUTMController implements Initializable {
	@FXML
	private TextArea wgsArea;
	@FXML
	private TextArea utmArea;
	@FXML
	private TextField epsgField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	@FXML
	private void convert() throws Exception {
		if (StringUtils.isEmpty(epsgField.getText())) {
			new Alert(AlertType.ERROR, "ESPG为空！").showAndWait();
			return;
		}
		// 计算系统中心点utm坐标
		CoordinateReferenceSystem sourceCRS = DefaultGeographicCRS.WGS84;
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:" + epsgField.getText(), true);
		MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
		String wgs84sString = wgsArea.getText();
		String[] splitLine = wgs84sString.split("\n");
		for (String string : splitLine) {
			String[] split = string.split(",");
			Coordinate result = transform(split[0].trim(), split[1].trim(), transform);
			if (result != null) {
				utmArea.appendText(result.x + "," + result.y + "\n");
			} else {
				utmArea.appendText("no result\n");
			}
		}

	}

	private Coordinate transform(String lon, String lat, MathTransform transform) throws TransformException {
		double lonD = Double.valueOf(lon);
		double latD = Double.valueOf(lat);
		if (lon.matches("\\d{4,}.+")) {// DMS格式
			lonD = DmsAndDeg.dmsTodeg(lon);
			latD = DmsAndDeg.dmsTodeg(lat);
		}
		Coordinate resultCoordinate = new Coordinate();
		JTS.transform(new Coordinate(lonD, latD), resultCoordinate, transform);
		return resultCoordinate;
	}

}
