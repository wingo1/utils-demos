package com.wingo1.example.rabbitmq;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.RecoveryListener;

/**
 * 发送接收，并可监视自动恢复
 * 
 * @author cdatc-wingo1
 *
 */
public class RabbitMqClient {
	private static String QUEUE_NAME = "testQueue";

	public static void main(String[] args) throws IOException, TimeoutException {
		// 发送端
		new Thread(() -> {

			ConnectionFactory factory = new ConnectionFactory();
			// "guest"/"guest" by default, limited to localhost connections
			factory.setUsername("test");
			factory.setPassword("test");
			factory.setVirtualHost("/");
			factory.setHost("192.168.226.179");
			factory.setPort(5672);
			try (Connection conn = factory.newConnection();) {
				Channel channel = conn.createChannel();
				channel.queueDeclare(QUEUE_NAME, false, false, false, null);
				Scanner scanner = new Scanner(System.in);
				while (true) {
					String nextLine = scanner.nextLine();
					channel.basicPublish("", QUEUE_NAME, null, nextLine.getBytes());
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}).start();
		// 接收端
		ConnectionFactory factory = new ConnectionFactory();
		factory.setRequestedHeartbeat(10);
		factory.setAutomaticRecoveryEnabled(true);// 默认就是true
		// "guest"/"guest" by default, limited to localhost connections
		factory.setUsername("test");
		factory.setPassword("test");
		factory.setVirtualHost("/");
		factory.setHost("192.168.226.179");
		factory.setPort(5672);
		Connection conn = factory.newConnection();
		Channel channel = conn.createChannel();
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		channel.basicQos(1);
		String consumerTag = channel.basicConsume(QUEUE_NAME, false, new DeliverCallback() {
			@Override
			public void handle(String consumerTag, Delivery message) throws IOException {
				System.out.println(consumerTag + ":" + new String(message.getBody()));
				channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
			}
		}, _consumerTag -> {
			System.out.println("cancel");
		});
		// 可以自动恢复
		Recoverable recoverable = (Recoverable) conn;
		recoverable.addRecoveryListener(new RecoveryListener() {
			@Override
			public void handleRecoveryStarted(Recoverable recoverable) {
				System.out.println("recover start!");

			}

			@Override
			public void handleRecovery(Recoverable recoverable) {
				System.out.println("recover over!");

			}
		});

		System.out.println(consumerTag);
		new Thread(() -> {
			while (true) {
				try {
					// 自动恢复后会变true
					System.out.println("open?" + conn.isOpen());
					TimeUnit.SECONDS.sleep(10);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}).start();
	}

}
