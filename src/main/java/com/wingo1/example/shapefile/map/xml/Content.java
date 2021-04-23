package com.wingo1.example.shapefile.map.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "content")
public class Content {
	@XmlElement(name = "text")
	public List<SysMapText> texts;
	@XmlElement(name = "polygon")
	public List<SysMapPolygon> polygons;

	public List<SysMapText> getTexts() {
		return texts;
	}

	public void setTexts(List<SysMapText> texts) {
		this.texts = texts;
	}

	public List<SysMapPolygon> getPolygons() {
		return polygons;
	}

	public void setPolygons(List<SysMapPolygon> polygons) {
		this.polygons = polygons;
	}
}
