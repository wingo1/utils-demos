package com.wingo1.thread;

/**
 * 可见性例子，使用64位JDK试验
 *
 * @author cdatc-wingo1
 *
 */
public class VolatileTest {
	public Boolean isShutdown = null;

	public boolean getShutdown() {
		return isShutdown;
	}

	public void shutdown() {
		Boolean newOne = new Boolean(true);
		isShutdown = newOne;
	}

	public class ReaderThread extends Thread {
		@Override
		public void run() {
			try {
				isShutdown = new Boolean(false);
				System.out.println("开始循环");
				while (!isShutdown) {

				}
				;
				System.out.println("结束循环");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public class WatchThread extends Thread {
		@Override
		public void run() {
			shutdown();
		}
	}

	public static void main(String[] args) {
		try {
			VolatileTest volatileTest = new VolatileTest();
			volatileTest.new ReaderThread().start();
			// 让主线程睡眠一秒，确保另一个线程调用shutdown方法时死循环已经开始
			Thread.sleep(1000);

			volatileTest.new WatchThread().start();
			// 此刻的睡眠是为了确保shutdown方法对isShutdown变量的修改已经同步到主内存
			Thread.sleep(1000);
			// 打印isShutdown的值
			System.out.println("getShutdown:" + volatileTest.getShutdown());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
