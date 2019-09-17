package com.wingo1.example.thread;

/**
 * 指令重排序实例<br>
 * 有任何打印信息说明发生了指令重排序，出现概率不确定
 * 
 * @author cdatc-wingo1
 *
 */
public class VoilatileHappenBeforeTest {

	public static void main(String[] args) throws InterruptedException {

		for (int i = 0; i < 500000; i++) {
			VoilatileHappenBeforeTest.State state = new VoilatileHappenBeforeTest.State();
			ThreadA threadA = new ThreadA(state);
			ThreadB threadB = new ThreadB(state);
			threadA.start();
			threadB.start();

			threadA.join();
			threadB.join();
		}
	}

	static class ThreadA extends Thread {

		private final VoilatileHappenBeforeTest.State state;

		ThreadA(VoilatileHappenBeforeTest.State state) {
			this.state = state;
		}

		@Override
		public void run() {
			state.a = 1;
			state.b = 1;
			state.c = 1;
			state.d = 1;
		}
	}

	static class ThreadB extends Thread {

		private final VoilatileHappenBeforeTest.State state;

		ThreadB(VoilatileHappenBeforeTest.State state) {
			this.state = state;
		}

		@Override
		public void run() {
			if (state.b == 1 && state.a == 0) {
				System.out.println("b==1");
			}

			if (state.c == 1 && (state.b == 0 || state.a == 0)) {
				System.out.println("c==1");
			}

			if (state.d == 1 && (state.a == 0 || state.b == 0 || state.c == 0)) {
				System.out.println("d==1");
			}
		}
	}

	static class State {
		public int a = 0;
		public int b = 0;
		public int c = 0;
		public int d = 0;
	}
}
