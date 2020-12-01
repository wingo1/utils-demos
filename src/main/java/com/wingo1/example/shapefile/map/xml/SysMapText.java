package com.wingo1.example.shapefile.map.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class SysMapText {
	@XmlAttribute
	String id;
	@XmlAttribute
	String refid = "-1";
	@XmlAttribute
	String width = "0";
	@XmlAttribute
	String brushcolor = "#000000";
	@XmlAttribute
	String linetype = "solidline";
	@XmlAttribute
	String pencolor = "#9c9c9c";
	@XmlAttribute
	String filltype = "nobrush";
	@XmlAttribute
	String name;
	@XmlAttribute
	String font = "Courier 10 Pitch,8,-1,5,50,0,0,0,0,0,Regular";
	@XmlElement(name = "string")
	String str;
	@XmlElement(name = "pos")
	SysMapPoint point;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRefid() {
		return refid;
	}

	public void setRefid(String refid) {
		this.refid = refid;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public SysMapPoint getPoint() {
		return point;
	}

	public void setPoint(SysMapPoint point) {
		this.point = point;
	}

}
