package com.ivj.squirrelgame.commandParser;

import java.io.BufferedReader;
import java.io.IOException;

public class CommandScanner {

	private BufferedReader inputStream;
	private Command lastCommand = new Command(null, (Object[]) null);
	private CommandTypeInfo[] arrayToSearch;

	public CommandScanner(CommandTypeInfo[] arrayToSearch,
			BufferedReader inputStream) {
		this.inputStream = inputStream;

		this.arrayToSearch = new CommandTypeInfo[arrayToSearch.length];
		System.arraycopy(arrayToSearch, 0, this.arrayToSearch, 0,
				arrayToSearch.length);

	}

	public Command next() throws ScanException {
		String input = null;
		String[] splittedInput = null;
		try {
			input = inputStream.readLine();
			splittedInput = input.split(" ");
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		if (splittedInput == null) {
			return null;
		}
		String cmdName = splittedInput[0].toLowerCase();
		String[] paramsInput = new String[splittedInput.length - 1];
		System.arraycopy(splittedInput, 1, paramsInput, 0,
				splittedInput.length - 1);

		CommandTypeInfo targetInfo = null;
		for (int indexFound = 0; indexFound < arrayToSearch.length; indexFound++) {
			CommandTypeInfo info = arrayToSearch[indexFound];
			if (info.getName().equals(cmdName)) {
				targetInfo = arrayToSearch[indexFound];
			}
		}

		// nothing found
		if (targetInfo == null) {
			throw new ScanException("invalid commandname " + cmdName);
		}

		Object[] convertedParams = convertParams(paramsInput, targetInfo);

		lastCommand.setCommandType(targetInfo);
		lastCommand.setParams(convertedParams);
		return lastCommand;

	}

	private Object[] convertParams(String[] paramsStrings, CommandTypeInfo cti)
			throws ScanException {
		int paramNum = paramsStrings.length;
		Class<?>[] paramTypes = cti.getParamTypes();
		if (paramNum != paramTypes.length) {
			throw new ScanException("number of parameters doesn't match");
		}

		Object[] convertedParams = new Object[paramNum];

		for (int i = 0; i < paramNum; i++) {
			Class<?> paramType = paramTypes[i];
			String paramString = paramsStrings[i];

			try {
				convertedParams[i] = tryParse(paramType, paramString);
			} catch (NumberFormatException e) {
				throw new ScanException("Invalid paramter at index " + i);
			}

			if (convertedParams[i] == null) {
				throw new ScanException("Invalid paramter at index " + i);
			}
		}
		return convertedParams;
	}

	private Object tryParse(Class<?> paramType, String paramString)
			throws ScanException {
		Object ret = null;
		if (paramType == String.class) {
			ret = paramString;
		} else if (paramType == float.class) {
			ret = Float.parseFloat(paramString);
		} else if (paramType == int.class) {
			ret = Integer.parseInt(paramString);
		} else if (paramType == double.class) {
			ret = Double.parseDouble(paramString);
		} else {
			throw new ScanException("Converting to " + paramType.toString()
					+ " not implemented");
		}
		return ret;
	}

}
