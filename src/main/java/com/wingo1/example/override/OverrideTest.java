package com.wingo1.example.override;

public class OverrideTest {
	class A {
		public String show(D obj) {
			return "A and D";
		}

		public String show(A obj) {
			return "A and A";
		}
	}

	class B extends A {
		public String show(B obj) {
			return "B and B";

		}

		@Override
		public String show(A obj) {
			return "B and A";

		}
	}

	class C extends B {

	}

	class D extends B {

	}

	public static void main(String[] args) throws InterruptedException {
		OverrideTest oTest = new OverrideTest();
		A a1 = oTest.new A();
		A a2 = oTest.new B();
		B b = oTest.new B();
		C c = oTest.new C();
		D d = oTest.new D();
		System.out.println(a1.show(b));// A A
		System.out.println(a1.show(c));// A A
		System.out.println(a1.show(d));// A D
		System.out.println(a2.show(b));// B A
		System.out.println(a2.show(c));// B A
		System.out.println(a2.show(d));// A D
		System.out.println(b.show(b));// B B
		System.out.println(b.show(c));// B B
		System.out.println(b.show(d));// A D
		// ����ֻ��Ϊ�˲���Զ�̵��Զ���
		int exitFlag = 0;
		while (exitFlag == 0) {
			System.out.println("flag=" + exitFlag);
			Thread.sleep(500);

		}

	}

}
