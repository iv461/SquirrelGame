package com.ivj.squirrelgame.commandParser;

public interface CommandTypeInfo {
	String getName();

	String getHelpText();

	String getMethodName();

	Class<?>[] getParamTypes();

}
