package com.wingo1.example.shapefile.map.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "layer")
public class Taxl {
	@XmlElementWrapper(name = "content")
	@XmlElement(name = "polyline")
	public List<SysMapPolyline> polylines;

	@XmlElementWrapper(name = "extrainfo")
	@XmlElement(name = "taxl")
	public List<ExtinfoTaxiLine> extinfoTaxiLines;

	public List<SysMapPolyline> getPolylines() {
		return polylines;
	}

	public void setPolylines(List<SysMapPolyline> polylines) {
		this.polylines = polylines;
	}

	public List<ExtinfoTaxiLine> getExtinfoTaxiLines() {
		return extinfoTaxiLines;
	}

	public void setExtinfoTaxiLines(List<ExtinfoTaxiLine> extinfoTaxiLines) {
		this.extinfoTaxiLines = extinfoTaxiLines;
	}

}
