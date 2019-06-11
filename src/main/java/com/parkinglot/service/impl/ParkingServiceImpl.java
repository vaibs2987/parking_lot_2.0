package com.parkinglot.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.parkinglot.contants.Constants;
import com.parkinglot.dao.ParkingDataDao;
import com.parkinglot.dao.impl.ParkingDataDaoImpl;
import com.parkinglot.exception.Message;
import com.parkinglot.exception.ParkingException;
import com.parkinglot.model.Vehicle;
import com.parkinglot.model.validation.service.ParkingValidationServiceImpl;
import com.parkinglot.model.validation.service.ParkingValidationService;
import com.parkinglot.service.ParkingService;

/**
 * 
 * 
 * This is a Singleton class and used for injecting in RequestProcessor.
 *
 */
public class ParkingServiceImpl implements ParkingService {
	private ParkingDataDao<Vehicle> dataManager = null;

	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	@Override
	public void createParkingLot(int level, int capacity) throws ParkingException {
		if (dataManager != null)
			throw new ParkingException(Message.PARKING_ALREADY_EXIST.getMessage());
		List<Integer> parkingLevels = new ArrayList<>();
		List<Integer> capacityList = new ArrayList<>();
		List<ParkingValidationService> parkingStrategies = new ArrayList<>();
		parkingLevels.add(level);
		capacityList.add(capacity);
		parkingStrategies.add(new ParkingValidationServiceImpl());
		this.dataManager = ParkingDataDaoImpl.getInstance(parkingLevels, capacityList, parkingStrategies);
		System.out.println("Created parking lot with " + capacity + " slots");
	}

	@Override
	public Optional<Integer> park(int level, Vehicle vehicle) throws ParkingException {
		Optional<Integer> value = Optional.empty();
		lock.writeLock().lock();
		validateParkingLot();
		try {
			value = Optional.of(dataManager.parkCar(level, vehicle));
			if (value.get() == Constants.NOT_AVAILABLE)
				System.out.println("Sorry, parking lot is full");
			else if (value.get() == Constants.VEHICLE_ALREADY_EXIST)
				System.out.println("Sorry, vehicle is already parked.");
			else
				System.out.println("Allocated slot number: " + value.get());
		} catch (Exception e) {
			throw new ParkingException(Message.PROCESSING_ERROR.getMessage(), e);
		} finally {
			lock.writeLock().unlock();
		}
		return value;
	}

	private void validateParkingLot() throws ParkingException {
		if (dataManager == null) {
			throw new ParkingException(Message.PARKING_NOT_EXIST_ERROR.getMessage());
		}
	}

	@Override
	public void unPark(int level, int slotNumber) throws ParkingException {
		lock.writeLock().lock();
		validateParkingLot();
		try {

			if (dataManager.leaveCar(level, slotNumber))
				System.out.println("Slot number " + slotNumber + " is free");
			else
				System.out.println("Slot number is Empty Already.");
		} catch (Exception e) {
			throw new ParkingException(Message.INVALID_VALUE.getMessage().replace("{variable}", "slot_number"), e);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void getStatus(int level) throws ParkingException {
		lock.readLock().lock();
		validateParkingLot();
		try {
			System.out.println("Slot No.\tRegistration No.\tColor");
			List<String> statusList = dataManager.getStatus(level);
			if (statusList.size() == 0)
				System.out.println("Sorry, parking lot is empty.");
			else {
				for (String statusSting : statusList) {
					System.out.println(statusSting);
				}
			}
		} catch (Exception e) {
			throw new ParkingException(Message.PROCESSING_ERROR.getMessage(), e);
		} finally {
			lock.readLock().unlock();
		}
	}

	public Optional<Integer> getAvailableSlotsCount(int level) throws ParkingException {
		lock.readLock().lock();
		Optional<Integer> value = Optional.empty();
		validateParkingLot();
		try {
			value = Optional.of(dataManager.getAvailableSlotsCount(level));
		} catch (Exception e) {
			throw new ParkingException(Message.PROCESSING_ERROR.getMessage(), e);
		} finally {
			lock.readLock().unlock();
		}
		return value;
	}

	@Override
	public void getRegNumberForColor(int level, String color) throws ParkingException {
		lock.readLock().lock();
		validateParkingLot();
		try {
			List<String> registrationList = dataManager.getRegNumberForColor(level, color);
			if (registrationList.size() == 0)
				System.out.println("Not Found");
			else
				System.out.println(String.join(",", registrationList));
		} catch (Exception e) {
			throw new ParkingException(Message.PROCESSING_ERROR.getMessage(), e);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void getSlotNumbersFromColor(int level, String color) throws ParkingException {
		lock.readLock().lock();
		validateParkingLot();
		try {
			List<Integer> slotList = dataManager.getSlotNumbersFromColor(level, color);
			if (slotList.size() == 0)
				System.out.println("Not Found");
			StringJoiner joiner = new StringJoiner(",");
			for (Integer slot : slotList) {
				joiner.add(slot + "");
			}
			System.out.println(joiner.toString());
		} catch (Exception e) {
			throw new ParkingException(Message.PROCESSING_ERROR.getMessage(), e);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int getSlotNoFromRegistrationNo(int level, String registrationNo) throws ParkingException {
		int value = -1;
		lock.readLock().lock();
		validateParkingLot();
		try {
			value = dataManager.getSlotNoFromRegistrationNo(level, registrationNo);
			System.out.println(value != -1 ? value : "Not Found");
		} catch (Exception e) {
			throw new ParkingException(Message.PROCESSING_ERROR.getMessage(), e);
		} finally {
			lock.readLock().unlock();
		}
		return value;
	}

	@Override
	public void doCleanup() {
		if (dataManager != null)
			dataManager.doCleanup();
	}
}
