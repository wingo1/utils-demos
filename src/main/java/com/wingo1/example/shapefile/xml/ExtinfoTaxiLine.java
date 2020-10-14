package com.wingo1.example.shapefile.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class ExtinfoTaxiLine {

	@XmlAttribute
	String name;
	@XmlAttribute
	String on;
	@XmlAttribute
	String only_uncoupled = "false";
	@XmlAttribute
	String priority = "0";

	String type;

	String taxiname;

	String typename;

	String direct = "Both";
	Float winglength = 0f;
	Float pcn = 0f;
	Float mainwheel = 0f;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOn() {
		return on;
	}

	public void setOn(String on) {
		this.on = on;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTaxiname() {
		return taxiname;
	}

	public void setTaxiname(String taxiname) {
		this.taxiname = taxiname;
	}

	public String getTypename() {
		return typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}

	public String getDirect() {
		return direct;
	}

	public void setDirect(String direct) {
		this.direct = direct;
	}

	public Float getWinglength() {
		return winglength;
	}

	public void setWinglength(Float winglength) {
		this.winglength = winglength;
	}

	public Float getPcn() {
		return pcn;
	}

	public void setPcn(Float pcn) {
		this.pcn = pcn;
	}

	public Float getMainwheel() {
		return mainwheel;
	}

	public void setMainwheel(Float mainwheel) {
		this.mainwheel = mainwheel;
	}

	public String getOnly_uncoupled() {
		return only_uncoupled;
	}

	public void setOnly_uncoupled(String only_uncoupled) {
		this.only_uncoupled = only_uncoupled;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

}
