package com.parkinglot.processor;

import com.parkinglot.contants.CommandInputMap;
import com.parkinglot.exception.ParkingException;
import com.parkinglot.service.AbstractService;

public interface ValidateProcessor {

	public void setService(AbstractService service);

	public void execute(String action) throws ParkingException;

	public default boolean validate(String inputString) {

		boolean valid = true;
		try {
			String[] inputs = inputString.split(" ");
			int params = CommandInputMap.getCommandsParameterMap().get(inputs[0]);
			switch (inputs.length) {
			case 1:
				if (params != 0)
					valid = false;
				break;
			case 2:
				if (params != 1)
					valid = false;
				break;
			case 3:
				if (params != 2)
					valid = false;
				break;
			default:
				valid = false;
			}
		} catch (Exception e) {
			valid = false;
		}
		return valid;
	}
}
