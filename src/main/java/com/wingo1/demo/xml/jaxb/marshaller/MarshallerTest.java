package com.wingo1.demo.xml.jaxb.marshaller;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 * 利用JAXB,对象转XML,XML转对象
 * 
 * @author cdatc-wingo1
 *
 */
public class MarshallerTest {

	public static void main(String[] args) throws Exception {
		S04Body body = new S04Body();
		body.setMemberId("2");
		body.setAdmissionDate("666");
		body.setHello("中文");
		List<String> list = new ArrayList<>();
		Detail detail1 = new Detail();
		detail1.setCode("11");
		detail1.setName("22");
		Detail detail2 = new Detail();
		detail2.setCode("11");
		detail2.setName("22");
		list.add("2444");
		list.add("44444");
		body.setString(list);
		/*
		 * S58Data data = new S58Data(); S58DataVo vo = new S58DataVo(); List<S58DataVo>
		 * voList = new ArrayList<>(); vo.setCureName("name"); voList.add(vo);
		 * data.setDataVos(voList);
		 */

		JAXBContext context = JAXBContext.newInstance(body.getClass()); // 获取上下文对象
		Marshaller marshaller = context.createMarshaller(); // 根据上下文获取marshaller对象

		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); // 设置编码字符集
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // 格式化XML输出，有分行和缩进

		// marshaller.marshal(body, System.out); //打印到控制台

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshaller.marshal(body, baos);
		String xmlObj = new String(baos.toByteArray()); // 生成XML字符串
		System.out.println(xmlObj);

		Document doc = DocumentHelper.parseText(xmlObj);
		Node node = doc.selectSingleNode(".//list");

		conventback(xmlObj);
	}

	private static void conventback(String xml) throws JAXBException {
		JAXBContext context1 = JAXBContext.newInstance(S04Body.class);
		// 进行将Xml转成对象的核心接口
		Unmarshaller unmarshaller = context1.createUnmarshaller();
		StringReader sr = new StringReader(xml);
		Object xmlObject = unmarshaller.unmarshal(sr);
		System.out.println(xmlObject instanceof S04Body);
	}

}
