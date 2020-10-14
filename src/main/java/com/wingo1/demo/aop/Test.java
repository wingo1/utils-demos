package com.wingo1.demo.aop;

public class Test {
	public static void main(String[] args) {
		Operator operator = FactoryWorker.getInstance();
		System.out.println(operator.operate("1", "2", "3"));
		System.out.println(operator.report());
	}
}
