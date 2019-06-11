
package com.parkinglot.processor;

import com.parkinglot.contants.InputCommand;
import com.parkinglot.exception.Message;
import com.parkinglot.exception.ParkingException;
import com.parkinglot.model.Car;
import com.parkinglot.service.AbstractService;
import com.parkinglot.service.ParkingService;

public class RequestProcessor implements ValidateProcessor {
	private ParkingService parkingService;

	public void setParkingService(ParkingService parkingService) throws ParkingException {
		this.parkingService = parkingService;
	}

	@Override
	public void execute(String input) throws ParkingException {
		int level = 1;
		String[] inputs = input.split(" ");
		String key = inputs[0];
		InputCommand command = InputCommand.getCommand(key);
		switch (command) {
		case CREATE_PARKING_LOT:
			try {
				int capacity = Integer.parseInt(inputs[1]);
				parkingService.createParkingLot(level, capacity);
			} catch (NumberFormatException e) {
				throw new ParkingException(Message.INVALID_VALUE.getMessage().replace("{variable}", "capacity"));
			}
			break;
		case PARK:
			parkingService.park(level, new Car(inputs[1], inputs[2]));
			break;
		case LEAVE:
			try {
				int slotNumber = Integer.parseInt(inputs[1]);
				parkingService.unPark(level, slotNumber);
			} catch (NumberFormatException e) {
				throw new ParkingException(Message.INVALID_VALUE.getMessage().replace("{variable}", "slot_number"));
			}
			break;
		case STATUS:
			parkingService.getStatus(level);
			break;
		case REG_NUMBER_FOR_CARS_WITH_COLOR:
			parkingService.getRegNumberForColor(level, inputs[1]);
			break;
		case SLOTS_NUMBER_FOR_CARS_WITH_COLOR:
			parkingService.getSlotNumbersFromColor(level, inputs[1]);
			break;
		case SLOTS_NUMBER_FOR_REG_NUMBER:
			parkingService.getSlotNoFromRegistrationNo(level, inputs[1]);
			break;

		}
	}

	@Override
	public void setService(AbstractService service) {
		this.parkingService = (ParkingService) service;
	}
}
