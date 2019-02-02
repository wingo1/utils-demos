package com.wingo1.example.override;

public class CounstructorPolymorphismTest {

	public static void main(String[] args) {
		new B();

	}

}

class A {
	private String str = "A";

	public A() {
		System.out.println("A constructed!");
		printStr();
	}

	public void printStr() {
		System.out.println("A str:" + str);
	}
}

class B extends A {
	private String str = "B";

	public B() {
		System.out.println("B constructed!");
	}

	@Override
	public void printStr() {
		System.out.println("B str:" + str);
	}

}