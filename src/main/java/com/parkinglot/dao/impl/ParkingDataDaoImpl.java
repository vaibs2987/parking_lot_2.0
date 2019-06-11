/**
 * 
 */
package com.parkinglot.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.parkinglot.dao.ParkingDataDao;
import com.parkinglot.dao.ParkingLevelDataDao;
import com.parkinglot.model.Vehicle;
import com.parkinglot.model.validation.service.ParkingValidationServiceImpl;
import com.parkinglot.model.validation.service.ParkingValidationService;

/**
 * This class is a singleton class to hold and manage the data of parking
 * system.
 * 
 */
public class ParkingDataDaoImpl<T extends Vehicle> implements ParkingDataDao<T> {
	private Map<Integer, ParkingLevelDataDao<T>> levelParkingMap;

	@SuppressWarnings("rawtypes")
	private static ParkingDataDaoImpl parkingDataDaoImpl = null;

	@SuppressWarnings("unchecked")
	public static <T extends Vehicle> ParkingDataDaoImpl<T> getInstance(List<Integer> parkingLevels,
			List<Integer> capacityList, List<ParkingValidationService> parkingStrategies) {
		// Make sure the each of the lists are of equal size
		if (parkingDataDaoImpl == null) {
			synchronized (ParkingDataDaoImpl.class) {
				if (parkingDataDaoImpl == null) {
					parkingDataDaoImpl = new ParkingDataDaoImpl<T>(parkingLevels, capacityList, parkingStrategies);
				}
			}
		}
		return parkingDataDaoImpl;
	}

	private ParkingDataDaoImpl(List<Integer> parkingLevels, List<Integer> capacityList,
			List<ParkingValidationService> parkingStrategies) {
		if (levelParkingMap == null)
			levelParkingMap = new HashMap<>();
		for (int i = 0; i < parkingLevels.size(); i++) {
			levelParkingMap.put(parkingLevels.get(i), ParkingLevelDataDaoImpl.getInstance(parkingLevels.get(i),
					capacityList.get(i), new ParkingValidationServiceImpl()));

		}
	}

	@Override
	public int parkCar(int level, T vehicle) {
		return levelParkingMap.get(level).parkCar(vehicle);
	}

	@Override
	public boolean leaveCar(int level, int slotNumber) {
		return levelParkingMap.get(level).leaveCar(slotNumber);
	}

	@Override
	public List<String> getStatus(int level) {
		return levelParkingMap.get(level).getStatus();
	}

	public int getAvailableSlotsCount(int level) {
		return levelParkingMap.get(level).getAvailableSlotsCount();
	}

	@Override
	public List<String> getRegNumberForColor(int level, String color) {
		return levelParkingMap.get(level).getRegNumberForColor(color);
	}

	@Override
	public List<Integer> getSlotNumbersFromColor(int level, String color) {
		return levelParkingMap.get(level).getSlotNumbersFromColor(color);
	}

	@Override
	public int getSlotNoFromRegistrationNo(int level, String registrationNo) {
		return levelParkingMap.get(level).getSlotNoFromRegistrationNo(registrationNo);
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public void doCleanup() {
		for (ParkingLevelDataDao<T> levelDataManager : levelParkingMap.values()) {
			levelDataManager.doCleanUp();
		}
		levelParkingMap = null;
		parkingDataDaoImpl = null;
	}
}
