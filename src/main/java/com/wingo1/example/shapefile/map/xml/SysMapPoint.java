package com.wingo1.example.shapefile.map.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class SysMapPoint {

	String longitude;

	String latitude;
	@XmlAttribute
	String name;
	@XmlAttribute
	String brushcolor;
	@XmlAttribute
	String linetype;
	@XmlAttribute
	String pencolor;
	@XmlAttribute
	String filltype;
	@XmlAttribute
	String width;

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrushcolor() {
		return brushcolor;
	}

	public void setBrushcolor(String brushcolor) {
		this.brushcolor = brushcolor;
	}

	public String getLinetype() {
		return linetype;
	}

	public void setLinetype(String linetype) {
		this.linetype = linetype;
	}

	public String getPencolor() {
		return pencolor;
	}

	public void setPencolor(String pencolor) {
		this.pencolor = pencolor;
	}

	public String getFilltype() {
		return filltype;
	}

	public void setFilltype(String filltype) {
		this.filltype = filltype;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

}
