package com.wingo1.demo.aop;

import java.lang.reflect.Proxy;

public class FactoryWorker implements Operator {
	private static Operator instance;

	private FactoryWorker() {
		// TODO Auto-generated constructor stub
	}

	public static Operator getInstance() {
		if (instance != null) {
			return instance;
		}
		Operator op = new FactoryWorker();
		instance = (Operator) Proxy.newProxyInstance(FactoryWorker.class.getClassLoader(),
				new Class<?>[] { Operator.class }, new Handle(op));
		return instance;
	}

	@Override
	public String operate(String... ops) {
		StringBuilder sBuilder = new StringBuilder();
		for (String op : ops) {
			sBuilder.append(op).append("--finished").append("\n");
		}
		return sBuilder.toString();
	}

	@Override
	public String report() {

		return "I am reporting";

	}

}
