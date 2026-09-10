package com.wingo1.example.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AccountAspect {
	@Pointcut("execution(public boolean Account.withdraw(int))&& args(amount) && target(acc)")
	// @Pointcut("call(public boolean Account.withdraw(int))&& args(amount) &&
	// target(acc)")
	public void callWithDraw(int amount, Account acc) {
	}

	@Before("call(public boolean Account.withdraw(int))&& args(amount)")
	public void beforeCall(int amount) {
		if (amount > 5) {
			return;
		}
	}

	@Around("callWithDraw(amount,acc)")
	public boolean around(int amount, Account acc, ProceedingJoinPoint pjp) throws Throwable {
		System.out.println(amount + ":" + acc + ":" + pjp);
		if (acc.balance < amount) {
			return false;
		}
		/**
		 * In code style, the proceed method has the same signature as the advice, any
		 * reordering of actual arguments to the joinpoint that is done in the advice
		 * signature must be respected. Annotation style is different. The proceed(..)
		 * call takes, in this order:
		 * 
		 * If 'this()' was used in the pointcut for binding, it must be passed first in
		 * proceed(..). If 'target()' was used in the pointcut for binding, it must be
		 * passed next in proceed(..) - it will be the first argument to proceed(..) if
		 * this() was not used for binding. Finally come all the arguments expected at
		 * the join point, in the order they are supplied at the join point. Effectively
		 * the advice signature is ignored - it doesn't matter if a subset of arguments
		 * were bound or the ordering was changed in the advice signature, the
		 * proceed(..) calls takes all of them in the right order for the join point.
		 */
		return (boolean) pjp.proceed(new Object[] { acc, amount });
	}
}
