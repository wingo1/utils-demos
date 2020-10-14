package com.wingo1.example.shapefile.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SysMapPolyline {
	@XmlAttribute
	String name;
	@XmlAttribute
	String filltype = "nobrush";
	@XmlAttribute
	String linetype = "solidline";
	@XmlAttribute
	String font = "Cantarell,11,-1,5,50,0,0,0,0,0";
	@XmlAttribute
	String pencolor = "#696969";
	@XmlAttribute
	String brushcolor = "#000000";
	@XmlAttribute
	String width = "1";
	@XmlAttribute
	String refid = "-1";
	@XmlAttribute
	String id = "37176";

	@XmlElement(name = "point")
	List<SysMapPoint> points;

	public List<SysMapPoint> getPoints() {
		return points;
	}

	public void setPoints(List<SysMapPoint> points) {
		this.points = points;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilltype() {
		return filltype;
	}

	public void setFilltype(String filltype) {
		this.filltype = filltype;
	}

	public String getLinetype() {
		return linetype;
	}

	public void setLinetype(String linetype) {
		this.linetype = linetype;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getPencolor() {
		return pencolor;
	}

	public void setPencolor(String pencolor) {
		this.pencolor = pencolor;
	}

	public String getBrushcolor() {
		return brushcolor;
	}

	public void setBrushcolor(String brushcolor) {
		this.brushcolor = brushcolor;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getRefid() {
		return refid;
	}

	public void setRefid(String refid) {
		this.refid = refid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
