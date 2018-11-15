package com.wingo1.demo.xml.jaxb.marshaller;

import java.io.Serializable;

/**
 * S01修改密码返回数据
 * 
 * @author sunshixiong
 * @date 2018/1/4 8:57
 */
public class S58DataVo implements Serializable {
	private static final long serialVersionUID = -3959411663301198737L;
	private String cureType;// 治疗方式
	private String cureName;// 治疗方式名称

	public String getCureType() {
		return cureType;
	}

	public void setCureType(String cureType) {
		this.cureType = cureType;
	}

	public String getCureName() {
		return cureName;
	}

	public void setCureName(String cureName) {
		this.cureName = cureName;
	}
}
