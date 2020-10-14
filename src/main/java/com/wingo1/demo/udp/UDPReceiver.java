package com.wingo1.demo.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPReceiver {
	public static void main(String[] args) throws Exception {
		System.out.println(3 < 2 - 1 || 5 > 2);

		MulticastSocket socket = new MulticastSocket(10001);
		socket.joinGroup(InetAddress.getByName("230.0.0.1"));
		// socket.setInterface(InetAddress.getByName("192.168.44.139"));
		byte[] bytes = new byte[1024];
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
		System.out.println("I am ready...");
		socket.receive(packet);
		System.out.println(packet.getSocketAddress());
		System.out.println(new String(bytes));
		socket.close();
	}
}
