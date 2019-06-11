package com.parkinglot.contants;

import java.util.HashMap;
import java.util.Map;

public class CommandInputMap {
	private static volatile Map<String, Integer> commandsParameterMap = new HashMap<String, Integer>();

	static {
		commandsParameterMap.put(InputCommand.CREATE_PARKING_LOT.getCommand(), 1);
		commandsParameterMap.put(InputCommand.PARK.getCommand(), 2);
		commandsParameterMap.put(InputCommand.LEAVE.getCommand(), 1);
		commandsParameterMap.put(InputCommand.STATUS.getCommand(), 0);
		commandsParameterMap.put(InputCommand.REG_NUMBER_FOR_CARS_WITH_COLOR.getCommand(), 1);
		commandsParameterMap.put(InputCommand.SLOTS_NUMBER_FOR_CARS_WITH_COLOR.getCommand(), 1);
		commandsParameterMap.put(InputCommand.SLOTS_NUMBER_FOR_REG_NUMBER.getCommand(), 1);
	}

	/**
	 * @return the commandsParameterMap
	 */
	public static Map<String, Integer> getCommandsParameterMap() {
		return commandsParameterMap;
	}

	/**
	 * @param commandsParameterMap the commandsParameterMap to set
	 */
	public static void addCommand(String command, int parameterCount) {
		commandsParameterMap.put(command, parameterCount);
	}

}
