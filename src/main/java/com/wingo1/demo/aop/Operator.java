package com.wingo1.demo.aop;

public interface Operator {
	@Log(Log.PARAMS)
	public String operate(String... ops);

	@Log(Log.RESULT)
	public String report();

}
