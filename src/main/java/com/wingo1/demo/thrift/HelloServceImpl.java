package com.wingo1.demo.thrift;

import org.apache.thrift.TException;

import com.wingo1.demo.thrift.stub.Data;
import com.wingo1.demo.thrift.stub.HelloService.Iface;

public class HelloServceImpl implements Iface {

	@Override
	public Data getData(int id) throws TException {
		System.out.println("recv param:" + id);
		Data data = new Data();
		data.setUid(2);
		data.setDesc("hello!");
		return data;
	}

}
