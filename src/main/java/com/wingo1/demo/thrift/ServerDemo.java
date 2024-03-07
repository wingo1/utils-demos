package com.wingo1.demo.thrift;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import com.wingo1.demo.thrift.stub.HelloService;

public class ServerDemo {

	public static void main(String[] args) throws Exception {
		HelloServceImpl helloServiceImpl = new HelloServceImpl();
		TServerTransport serverTransport = new TServerSocket(5006);
		TServer server = new TSimpleServer(
				new Args(serverTransport).processor(new HelloService.Processor<>(helloServiceImpl)));
		System.out.println("Starting the simple server...");
		server.serve();
	}

}
