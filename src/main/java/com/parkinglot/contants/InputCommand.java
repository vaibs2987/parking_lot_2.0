package com.parkinglot.contants;

public enum InputCommand {
	CREATE_PARKING_LOT("create_parking_lot"),
	PARK("park"),
	STATUS("status"),
	LEAVE("leave"),
	SLOTS_NUMBER_FOR_CARS_WITH_COLOR("slot_numbers_for_cars_with_colour"),
	REG_NUMBER_FOR_CARS_WITH_COLOR("registration_numbers_for_cars_with_colour"),
	SLOTS_NUMBER_FOR_REG_NUMBER("slot_number_for_registration_number");
	
	private String command;

	private InputCommand(String command) {
		this.setCommand(command);
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public static InputCommand getCommand(String str) {
		for (InputCommand command : InputCommand.values()) {
			if (command.getCommand().equalsIgnoreCase(str)) {
				return command;
			}
		}
		return null;
	}
	
}
