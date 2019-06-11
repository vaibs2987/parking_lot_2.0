package com.parkinglot;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.parkinglot.exception.Message;
import com.parkinglot.exception.ParkingException;
import com.parkinglot.model.Car;
import com.parkinglot.service.ParkingService;
import com.parkinglot.service.impl.ParkingServiceImpl;


public class MainTestCases {

	private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	private int parkingLevel;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void init() {
		parkingLevel = 1;
		System.setOut(new PrintStream(outputStream));
	}

	@After
	public void cleanUp() {
		System.setOut(null);
	}

	@Test
	public void createParkingLot() throws Exception {
		ParkingService parkingService = new ParkingServiceImpl();
		parkingService.createParkingLot(parkingLevel, 65);
		assertTrue("createdparkinglotwith65slots".equalsIgnoreCase(outputStream.toString().trim().replace(" ", "")));
		parkingService.doCleanup();
	}

	@Test
	public void testStatus() throws Exception {
		ParkingService parkingService = new ParkingServiceImpl();
		exception.expect(ParkingException.class);
		exception.expectMessage(is(Message.PARKING_NOT_EXIST_ERROR.getMessage()));
		parkingService.getStatus(parkingLevel);
		assertEquals("Sorry,CarParkingDoesnotExist", outputStream.toString().trim().replace(" ", ""));
		parkingService.createParkingLot(parkingLevel, 8);
		parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
		parkingService.getStatus(parkingLevel);
		assertTrue(
				"Sorry,CarParkingDoesnotExist\ncreatedparkinglotwith8slots\nAllocatedslotnumber:1\nAllocatedslotnumber:2\nSlotNo.\tRegistrationNo.\tColor\n1\tKA-01-HH-1234\tWhite\n2\tKA-01-HH-9999\tWhite"
						.equalsIgnoreCase(outputStream.toString().trim().replace(" ", "")));
		parkingService.doCleanup();

	}

	@Test
	public void alreadyExistParkingLot() throws Exception {
		ParkingService parkingService = new ParkingServiceImpl();
		parkingService.createParkingLot(parkingLevel, 65);
		assertTrue("createdparkinglotwith65slots".equalsIgnoreCase(outputStream.toString().trim().replace(" ", "")));
		exception.expect(ParkingException.class);
		exception.expectMessage(is(Message.PARKING_ALREADY_EXIST.getMessage()));
		parkingService.createParkingLot(parkingLevel, 65);
		parkingService.doCleanup();
	}

	@Test
	public void testParkingCapacity() throws Exception {
		ParkingService instance = new ParkingServiceImpl();
		exception.expect(ParkingException.class);
		exception.expectMessage(is(Message.PARKING_NOT_EXIST_ERROR.getMessage()));
		instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		assertEquals("Sorry,CarParkingDoesnotExist", outputStream.toString().trim().replace(" ", ""));
		instance.createParkingLot(parkingLevel, 11);
		instance.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		instance.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
		instance.park(parkingLevel, new Car("KA-01-BB-0001", "Black"));
		assertEquals(3, instance.getAvailableSlotsCount(parkingLevel));
		instance.doCleanup();
	}

	@Test
	public void testEmptyParkingLot() throws Exception {
		ParkingService parkingService = new ParkingServiceImpl();
		exception.expect(ParkingException.class);
		exception.expectMessage(is(Message.PARKING_NOT_EXIST_ERROR.getMessage()));
		parkingService.getStatus(parkingLevel);
		assertTrue("Sorry,CarParkingDoesnotExist".equalsIgnoreCase(outputStream.toString().trim().replace(" ", "")));
		parkingService.createParkingLot(parkingLevel, 6);
		parkingService.getStatus(parkingLevel);
		assertTrue(
				"Sorry,CarParkingDoesnotExist\ncreatedparkinglotwith6slots\nSlotNo.\tRegistrationNo.\tColor\nSorry,parkinglotisempty."
						.equalsIgnoreCase(outputStream.toString().trim().replace(" ", "")));
		parkingService.doCleanup();
	}

	@Test
	public void testParkingLotIsFull() throws Exception {
		ParkingService parkingService = new ParkingServiceImpl();
		exception.expect(ParkingException.class);
		exception.expectMessage(is(Message.PARKING_NOT_EXIST_ERROR.getMessage()));
		parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		assertEquals("Sorry,CarParkingDoesnotExist", outputStream.toString().trim().replace(" ", ""));
		parkingService.createParkingLot(parkingLevel, 2);
		parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
		parkingService.park(parkingLevel, new Car("KA-01-BB-0001", "Black"));
		assertTrue("createdparkinglotwith2slots\\nAllocatedslotnumber:1\nAllocatedslotnumber:2\nSorry,parkinglotisfull"
				.equalsIgnoreCase(outputStream.toString().trim().replace(" ", "")));
		parkingService.doCleanup();
	}

	@Test
	public void testNearestSlotAllotment() throws Exception {
		ParkingService parkingService = new ParkingServiceImpl();
		exception.expect(ParkingException.class);
		exception.expectMessage(is(Message.PARKING_NOT_EXIST_ERROR.getMessage()));
		parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		assertEquals("Sorry,CarParkingDoesnotExist", outputStream.toString().trim().replace(" ", ""));
		parkingService.createParkingLot(parkingLevel, 5);
		parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
		parkingService.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1234");
		parkingService.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-9999");
		assertTrue("createdparkinglotwith5slots\nAllocatedslotnumber:1\nAllocatedslotnumber:2"
				.equalsIgnoreCase(outputStream.toString().trim().replace(" ", "")));
		parkingService.doCleanup();
	}

	@Test
	public void leave() throws Exception {
		ParkingService parkingService = new ParkingServiceImpl();
		exception.expect(ParkingException.class);
		exception.expectMessage(is(Message.PARKING_NOT_EXIST_ERROR.getMessage()));
		parkingService.unPark(parkingLevel, 2);
		assertEquals("Sorry,CarParkingDoesnotExist", outputStream.toString().trim().replace(" ", ""));
		parkingService.createParkingLot(parkingLevel, 6);
		parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
		parkingService.park(parkingLevel, new Car("KA-01-BB-0001", "Black"));
		parkingService.unPark(parkingLevel, 4);
		assertTrue(
				"Sorry,CarParkingDoesnotExist\ncreatedparkinglotwith6slots\nAllocatedslotnumber:1\nAllocatedslotnumber:2\nAllocatedslotnumber:3\nSlotnumber4isfree"
						.equalsIgnoreCase(outputStream.toString().trim().replace(" ", "")));
		parkingService.doCleanup();
	}

	@Test
	public void testWhenVehicleAlreadyPresent() throws Exception {
		ParkingService parkingService = new ParkingServiceImpl();
		exception.expect(ParkingException.class);
		exception.expectMessage(is(Message.PARKING_NOT_EXIST_ERROR.getMessage()));
		parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		assertEquals("Sorry,CarParkingDoesnotExist", outputStream.toString().trim().replace(" ", ""));
		parkingService.createParkingLot(parkingLevel, 3);
		parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		assertTrue(
				"Sorry,CarParkingDoesnotExist\ncreatedparkinglotwith3slots\nAllocatedslotnumber:1\nSorry,vehicleisalreadyparked."
						.equalsIgnoreCase(outputStream.toString().trim().replace(" ", "")));
		parkingService.doCleanup();
	}

	@Test
	public void testWhenVehicleAlreadyPicked() throws Exception {
		ParkingService parkingService = new ParkingServiceImpl();
		exception.expect(ParkingException.class);
		exception.expectMessage(is(Message.PARKING_NOT_EXIST_ERROR.getMessage()));
		parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		assertEquals("Sorry,CarParkingDoesnotExist", outputStream.toString().trim().replace(" ", ""));
		parkingService.createParkingLot(parkingLevel, 99);
		parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
		parkingService.unPark(parkingLevel, 1);
		parkingService.unPark(parkingLevel, 1);
		assertTrue(
				"Sorry,CarParkingDoesnotExist\ncreatedparkinglotwith99slots\nAllocatedslotnumber:1\nAllocatedslotnumber:2\nSlotnumberisEmptyAlready."
						.equalsIgnoreCase(outputStream.toString().trim().replace(" ", "")));
		parkingService.doCleanup();
	}

	@Test
	public void testGetSlotsByRegNo() throws Exception {
		ParkingService parkingService = new ParkingServiceImpl();
		exception.expect(ParkingException.class);
		exception.expectMessage(is(Message.PARKING_NOT_EXIST_ERROR.getMessage()));
		parkingService.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1234");
		assertEquals("Sorry,CarParkingDoesnotExist", outputStream.toString().trim().replace(" ", ""));
		parkingService.createParkingLot(parkingLevel, 10);
		parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
		parkingService.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1234");
		assertEquals("Sorry,CarParkingDoesnotExist\n" + "Createdparkinglotwith6slots\n" + "\n"
				+ "Allocatedslotnumber:1\n" + "\n" + "Allocatedslotnumber:2\n1",
				outputStream.toString().trim().replace(" ", ""));
		parkingService.getSlotNoFromRegistrationNo(parkingLevel, "KA-01-HH-1235");
		assertEquals("Sorry,CarParkingDoesnotExist\n" + "Createdparkinglotwith10slots\n" + "\n"
				+ "Allocatedslotnumber:1\n" + "\n" + "Allocatedslotnumber:2\n1\nNotFound",
				outputStream.toString().trim().replace(" ", ""));
		parkingService.doCleanup();
	}

	@Test
	public void testGetSlotsByColor() throws Exception {
		ParkingService parkingService = new ParkingServiceImpl();
		exception.expect(ParkingException.class);
		exception.expectMessage(is(Message.PARKING_NOT_EXIST_ERROR.getMessage()));
		parkingService.getRegNumberForColor(parkingLevel, "white");
		assertEquals("Sorry,CarParkingDoesnotExist", outputStream.toString().trim().replace(" ", ""));
		parkingService.createParkingLot(parkingLevel, 7);
		parkingService.park(parkingLevel, new Car("KA-01-HH-1234", "White"));
		parkingService.park(parkingLevel, new Car("KA-01-HH-9999", "White"));
		parkingService.getStatus(parkingLevel);
		parkingService.getRegNumberForColor(parkingLevel, "Cyan");
		assertEquals(
				"Sorry,CarParkingDoesnotExist\n" + "Createdparkinglotwith7slots\n" + "\n" + "Allocatedslotnumber:1\n"
						+ "\n" + "Allocatedslotnumber:2\nKA-01-HH-1234,KA-01-HH-9999",
				outputStream.toString().trim().replace(" ", ""));
		parkingService.getRegNumberForColor(parkingLevel, "Red");
		assertEquals(
				"Sorry,CarParkingDoesnotExist\n" + "Createdparkinglotwith6slots\n" + "\n" + "Allocatedslotnumber:1\n"
						+ "\n" + "Allocatedslotnumber:2\n" + "KA-01-HH-1234,KA-01-HH-9999,Notfound",
				outputStream.toString().trim().replace(" ", ""));
		parkingService.doCleanup();

	}
}
