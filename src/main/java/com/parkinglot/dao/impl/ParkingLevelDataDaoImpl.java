package com.parkinglot.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.parkinglot.contants.Constants;
import com.parkinglot.dao.ParkingLevelDataDao;
import com.parkinglot.model.Vehicle;
import com.parkinglot.model.validation.service.ParkingValidationServiceImpl;
import com.parkinglot.model.validation.service.ParkingValidationService;

/**
 * This class is a singleton class to manage the data of parking system
 * 
 */
public class ParkingLevelDataDaoImpl<T extends Vehicle> implements ParkingLevelDataDao<T> {
	// For Multilevel Parking lot - 0 -> Ground floor 1 -> First Floor etc
	private AtomicInteger level = new AtomicInteger(0);
	private AtomicInteger capacity = new AtomicInteger();
	private AtomicInteger availability = new AtomicInteger();

	private ParkingValidationService parkingValidationService;

	private Map<Integer, Optional<T>> slotVehicleMap;

	@SuppressWarnings("rawtypes")
	private static ParkingLevelDataDaoImpl parkingLevelDataDaoImpl = null;

	@SuppressWarnings("unchecked")
	public static <T extends Vehicle> ParkingLevelDataDaoImpl<T> getInstance(int level, int capacity,
			ParkingValidationService parkingStrategy) {
		if (parkingLevelDataDaoImpl == null) {
			synchronized (ParkingLevelDataDaoImpl.class) {
				if (parkingLevelDataDaoImpl == null) {
					parkingLevelDataDaoImpl = new ParkingLevelDataDaoImpl<T>(level, capacity, parkingStrategy);
				}
			}
		}
		return parkingLevelDataDaoImpl;
	}

	private ParkingLevelDataDaoImpl(int level, int capacity, ParkingValidationService parkingStrategy) {
		this.level.set(level);
		this.capacity.set(capacity);
		this.availability.set(capacity);
		this.parkingValidationService = parkingStrategy;
		if (parkingStrategy == null)
			parkingStrategy = new ParkingValidationServiceImpl();
		slotVehicleMap = new ConcurrentHashMap<>();
		for (int i = 1; i <= capacity; i++) {
			slotVehicleMap.put(i, Optional.empty());
			parkingStrategy.add(i);
		}
	}

	@Override
	public int parkCar(T vehicle) {
		int availableSlot;
		if (availability.get() == 0) {
			return Constants.NOT_AVAILABLE;
		} else {
			availableSlot = parkingValidationService.getSlot();
			if (slotVehicleMap.containsValue(Optional.of(vehicle)))
				return Constants.VEHICLE_ALREADY_EXIST;

			slotVehicleMap.put(availableSlot, Optional.of(vehicle));
			availability.decrementAndGet();
			parkingValidationService.removeSlot(availableSlot);
		}
		return availableSlot;
	}

	@Override
	public boolean leaveCar(int slotNumber) {
		if (!slotVehicleMap.get(slotNumber).isPresent()) // Slot already empty
			return false;
		availability.incrementAndGet();
		parkingValidationService.add(slotNumber);
		slotVehicleMap.put(slotNumber, Optional.empty());
		return true;
	}

	@Override
	public List<String> getStatus() {
		List<String> statusList = new ArrayList<>();
		for (int i = 1; i <= capacity.get(); i++) {
			Optional<T> vehicle = slotVehicleMap.get(i);
			if (vehicle.isPresent()) {
				statusList.add(i + "\t\t" + vehicle.get().getRegistrationNo() + "\t\t" + vehicle.get().getColor());
			}
		}
		return statusList;
	}

	public int getAvailableSlotsCount() {
		return availability.get();
	}

	@Override
	public List<String> getRegNumberForColor(String color) {
		List<String> statusList = new ArrayList<>();
		for (int i = 1; i <= capacity.get(); i++) {
			Optional<T> vehicle = slotVehicleMap.get(i);
			if (vehicle.isPresent() && color.equalsIgnoreCase(vehicle.get().getColor())) {
				statusList.add(vehicle.get().getRegistrationNo());
			}
		}
		return statusList;
	}

	@Override
	public List<Integer> getSlotNumbersFromColor(String colour) {
		List<Integer> slotList = new ArrayList<>();
		for (int i = 1; i <= capacity.get(); i++) {
			Optional<T> vehicle = slotVehicleMap.get(i);
			if (vehicle.isPresent() && colour.equalsIgnoreCase(vehicle.get().getColor())) {
				slotList.add(i);
			}
		}
		return slotList;
	}

	@Override
	public int getSlotNoFromRegistrationNo(String registrationNo) {
		int result = Constants.NOT_FOUND;
		for (int i = 1; i <= capacity.get(); i++) {
			Optional<T> vehicle = slotVehicleMap.get(i);
			if (vehicle.isPresent() && registrationNo.equalsIgnoreCase(vehicle.get().getRegistrationNo())) {
				result = i;
			}
		}
		return result;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	@Override
	public void doCleanUp() {
		this.level = new AtomicInteger();
		this.capacity = new AtomicInteger();
		this.availability = new AtomicInteger();
		this.parkingValidationService = null;
		slotVehicleMap = null;
		parkingLevelDataDaoImpl = null;
	}
}
