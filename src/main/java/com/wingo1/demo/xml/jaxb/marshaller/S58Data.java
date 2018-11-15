package com.wingo1.demo.xml.jaxb.marshaller;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * S58重大疾病治疗方式下载返回数据
 * 
 * @author sunshixiong
 * @date 2018/1/4 8:57
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {})
@XmlRootElement(name = "data")
public class S58Data implements Serializable {
	private static final long serialVersionUID = -3959411663301198737L;

	@XmlElement(name = "com.aspirecn.hzyl.hisinterface.vo.R57")
	private List<S58DataVo> dataVos;

	public List<S58DataVo> getDataVos() {
		return dataVos;
	}

	public void setDataVos(List<S58DataVo> dataVos) {
		this.dataVos = dataVos;
	}
}
