package com.ivj.squirrelgame.commandParser;

public class Command implements Executable {
	private CommandTypeInfo commandType;
	private Object[] params = null;

	public Command(CommandTypeInfo commandType, Object... params) {
		this.setCommandType(commandType);
		this.setParams(params);
	}

	public CommandTypeInfo getCommandType() {
		return commandType;
	}

	public void setCommandType(CommandTypeInfo commandType) {
		this.commandType = commandType;
	}

	public Object[] getParams() {
		if (params == null) {
			return null;
		}
		Object[] dest = new Object[params.length];
		System.arraycopy(params, 0, dest, 0, params.length);
		return dest;
	}

	public void setParams(Object[] params) {
		if (params != null) {
			this.params = new Object[params.length];
			System.arraycopy(params, 0, this.params, 0, params.length);
		}

	}

	public Object execute() {

		return null;
	}

	// works only if all types used as params overwrite
	// equals
	@Override
	public boolean equals(Object o) {
		if (o instanceof Command) {
			Command command = (Command) o;
			if (command.getCommandType().equals(this.getCommandType())) {
				if (params.length == command.getParams().length) {
					Object[] cmdParams = command.getParams();
					for (int i = 0; i < params.length; i++) {
						if (!cmdParams[i].equals(params[i])) {
							return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}
}
