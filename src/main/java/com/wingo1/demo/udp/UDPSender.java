package com.wingo1.demo.udp;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPSender {

	public static void main(String[] args) throws Exception {
		MulticastSocket socket = new MulticastSocket();
		// socket.setNetworkInterface(NetworkInterface.getByInetAddress(InetAddress.getByName("192.168.44.139")));
		byte[] bytes = new String("hello UDP").getBytes();
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName("230.0.0.1"), 10001);
		socket.send(packet);
	}

}
