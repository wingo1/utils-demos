package com.wingo1.example.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AccountAspect {
	@Pointcut("call(public boolean Account.withdraw(int))&& args(amount) && target(acc)")
	public void callWithDraw(int amount, Account acc) {
	}

	@Around("callWithDraw(amount,acc)")
	public boolean around(int amount, Account acc, ProceedingJoinPoint pjp) throws Throwable {
		System.out.println(acc.toString() + ":");
		if (acc.balance < amount) {
			return false;
		}
		return (boolean) pjp.proceed(new Object[] { acc });
	}
}
