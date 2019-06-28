package com.ivj.squirrelgame.core;

import com.ivj.squirrelgame.commandParser.CommandTypeInfo;

public enum GameCommandType implements CommandTypeInfo {

	HELP("help", "showHelp", " * get help"),
	EXIT("exit", "exit", " * exit  game"),
    ALL("all", "all", ""),
    LEFT("a", "movLeft", ""),
	UP("w", "movUp", ""),
    DOWN("s", "movDown", ""),
	RIGHT("d", "movRight", ""),
    SPAWN_MINI("spmini", "spawnMiniSquirrel", "<energy> spawns a MiniSquirrel", int.class),
	MASTER_ENERGY("energy", "getMasterEnergy", "");

    private final String name;
    private final String methodName;
    private final String helpText;
    private final Class<?>[] paramTypes;

    GameCommandType(String name, String methodName, String helpText, Class<?>... paramTypes) {

        this.name = name;
        this.methodName = methodName;
        this.helpText = helpText;
        this.paramTypes = paramTypes;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getHelpText() {
        return helpText;
    }

    
    @Override
    public Class<?>[] getParamTypes() {
        Class<?>[] copy = new Class<?>[paramTypes.length];
        System.arraycopy(paramTypes, 0, copy, 0, paramTypes.length);
        return copy;
    }
    
}
