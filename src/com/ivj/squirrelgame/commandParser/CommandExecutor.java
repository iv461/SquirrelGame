package com.ivj.squirrelgame.commandParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandExecutor {
	private final Object targetObject;

	public CommandExecutor(Object target) {
		this.targetObject = target;
	}

	public Object execute(Command command) throws InvocationTargetException,
			IllegalAccessException, NoSuchMethodException {
		CommandTypeInfo commandTypeInfo = command.getCommandType();
		if (commandTypeInfo == null) {
			return null;
		}

		String methodName = commandTypeInfo.getMethodName();
		Class<?>[] paramTypes = commandTypeInfo.getParamTypes();

		Method targetMethod = targetObject.getClass()
				.getDeclaredMethod(methodName, paramTypes);
		targetMethod.setAccessible(true);
		return targetMethod.invoke(targetObject, command.getParams());
	}
}
