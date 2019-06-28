package com.ivj.squirrelgame.logging;

import java.lang.reflect.Proxy;

public final class LoggingProxyFactory {
	@SuppressWarnings("unchecked")
	public static <T> T createLogger(T inst, String loggerName) {
		ClassLoader classLoader = inst.getClass().getClassLoader();
		LoggingInvocationHandler<T> loggingHandler = new LoggingInvocationHandler<>(
				loggerName, inst);

		return (T) Proxy.newProxyInstance(classLoader,
				inst.getClass().getInterfaces(), loggingHandler);
	}
}
