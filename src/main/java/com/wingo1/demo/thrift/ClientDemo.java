package com.wingo1.demo.thrift;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.wingo1.demo.thrift.stub.Data;
import com.wingo1.demo.thrift.stub.HelloService;
import com.wingo1.demo.thrift.stub.HelloService.Client;

public class ClientDemo {
	public static void main(String[] args) throws Exception {
		TTransport transport = new TSocket("localhost", 5006);
		transport.open();
		TProtocol protocol = new TBinaryProtocol(transport);
		Client client = new HelloService.Client(protocol);
		Data data = client.getData(10);
		System.out.println("invoke result:" + data.toString());
	}
}
