package com.wingo1.demo.xml.jaxb.marshaller.generic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "row")
public class AtypeOnline {
	@XmlAttribute
	String chname;
	@XmlAttribute
	String actwinglength;
	@XmlAttribute
	String ptype;
	@XmlAttribute
	String atype;
	@XmlAttribute
	String turbo;
	@XmlAttribute
	String actlength;
	@XmlAttribute
	String basisturbo;
	@XmlAttribute
	String engname;

	public String getChname() {
		return chname;
	}

	public void setChname(String chname) {
		this.chname = chname;
	}

	public String getActwinglength() {
		return actwinglength;
	}

	public void setActwinglength(String actwinglength) {
		this.actwinglength = actwinglength;
	}

	public String getPtype() {
		return ptype;
	}

	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	public String getAtype() {
		return atype;
	}

	public void setAtype(String atype) {
		this.atype = atype;
	}

	public String getTurbo() {
		return turbo;
	}

	public void setTurbo(String turbo) {
		this.turbo = turbo;
	}

	public String getActlength() {
		return actlength;
	}

	public void setActlength(String actlength) {
		this.actlength = actlength;
	}

	public String getBasisturbo() {
		return basisturbo;
	}

	public void setBasisturbo(String basisturbo) {
		this.basisturbo = basisturbo;
	}

	public String getEngname() {
		return engname;
	}

	public void setEngname(String engname) {
		this.engname = engname;
	}

}
