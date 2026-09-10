package com.wingo1.example.shapefile.map.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SysMapPolygon {
	@XmlAttribute
	String id;
	@XmlAttribute
	String group1;
	@XmlAttribute
	String name;
	@XmlAttribute
	String filltype;
	@XmlAttribute
	String linetype;
	@XmlAttribute
	String font;
	@XmlAttribute
	String pencolor;
	@XmlAttribute
	String brushcolor;
	@XmlAttribute
	String width;
	@XmlAttribute
	boolean show;
	@XmlAttribute
	boolean transfer;
//	@XmlElementWrapper(name="polygon")
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroup1() {
		return group1;
	}

	public void setGroup1(String group1) {
		this.group1 = group1;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public boolean isTransfer() {
		return transfer;
	}

	public void setTransfer(boolean transfer) {
		this.transfer = transfer;
	}
}
