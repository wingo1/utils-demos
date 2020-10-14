package com.wingo1.demo.xml.jaxb.marshaller.generic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "root")
public class Table<T> {
	@XmlElementWrapper(name = "rows")
	@XmlAnyElement(lax = true)
	private List<T> list;

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public static void main(String[] args) throws Exception {

		File file = new File("D:\\SCSWorkspace\\doc\\现场配置\\ITWR\\基础资料\\itwr_atype_online.xml");
		System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
		FileInputStream in = new FileInputStream(file);

		// in = new FileInputStream(file);
		JAXBContext context = JAXBContext.newInstance(Table.class, AtypeOnline.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Table<AtypeOnline> xmlObject = (Table<AtypeOnline>) unmarshaller.unmarshal(in);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		context.createMarshaller().marshal(xmlObject, os);
		System.out.println(os.toString());

		System.out.println(xmlObject.getList().get(0).getActlength());
	}
}
