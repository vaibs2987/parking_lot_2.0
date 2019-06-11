package com.parkinglot.model.validation.service;

import java.util.TreeSet;

public class ParkingValidationServiceImpl implements ParkingValidationService {
	private TreeSet<Integer> freeSlots;

	public ParkingValidationServiceImpl() {
		freeSlots = new TreeSet<Integer>();
	}

	@Override
	public void add(int i) {
		freeSlots.add(i);
	}

	@Override
	public int getSlot() {
		return freeSlots.first();
	}

	@Override
	public void removeSlot(int availableSlot) {
		freeSlots.remove(availableSlot);
	}
}
