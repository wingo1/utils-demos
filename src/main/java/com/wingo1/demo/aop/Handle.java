package com.wingo1.demo.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class Handle implements InvocationHandler {
	private Object obj;

	public Handle(Object o) {
		obj = o;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Log log = method.getAnnotation(Log.class);
		if (log.value().equals(Log.PARAMS)) {
			String[] strs = (String[]) args[0];
			for (String string : strs) {
				System.out.println(string);
			}
			System.out.println("AOP切入了:");
			strs[0] = "4";
			return method.invoke(obj, args);

		} else if (log.value().equals(Log.RESULT)) {
			Object object = method.invoke(obj, args);
			System.out.println(object);
			return "not reporting";
		}
		return null;

	}

}
