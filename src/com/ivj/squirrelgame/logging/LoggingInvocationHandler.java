package com.ivj.squirrelgame.logging;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingInvocationHandler<T> implements InvocationHandler {

	private Logger logger;
	private final T inst;

	LoggingInvocationHandler(String loggerName, T inst) {
		logger = Logger.getLogger(loggerName);
		this.inst = inst;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(method.getName());
		stringBuilder.append("(");

		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				if (i > 1) {
					stringBuilder.append(",");
				}

				stringBuilder.append(args[i].toString());
			}
		}
		stringBuilder.append(")");
		String methodCallString = stringBuilder.toString();
		logger.fine(methodCallString);

		try {
			return method.invoke(this.inst, args);
		} catch (IllegalAccessException e) {
			logger.log(Level.SEVERE, "failed to delegate method call", e);
			return null;
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
}
