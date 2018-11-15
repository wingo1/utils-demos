package com.wingo1.demo.xml.jaxb.marshaller;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "memberId", "AdmissionDate", "string", "hello" })
@XmlRootElement(name = "data")
public class S04Body {
	private String memberId;
	private String AdmissionDate;
	private String hello;
	@XmlElementWrapper(name = "list")
	@XmlElement(name = "com.aspirecn.hzyl.hisinterface.vo.R57")
	private List<String> string;

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getHello() {
		return hello;
	}

	public void setHello(String hello) {
		this.hello = hello;
	}

	public List<String> getString() {
		return string;
	}

	public void setString(List<String> string) {
		this.string = string;
	}

	public String getAdmissionDate() {
		return AdmissionDate;
	}

	public void setAdmissionDate(String admissionDate) {
		AdmissionDate = admissionDate;
	}

}
