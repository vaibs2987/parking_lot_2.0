package com.parkinglot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.parkinglot.exception.Message;
import com.parkinglot.exception.ParkingException;
import com.parkinglot.processor.ValidateProcessor;
import com.parkinglot.processor.RequestProcessor;
import com.parkinglot.service.impl.ParkingServiceImpl;

public class Application {

	public static void main(String[] args) {

		ValidateProcessor processor = new RequestProcessor();
		processor.setService(new ParkingServiceImpl());
		BufferedReader bufferReader = null;
		String input = null;
		try {
			switch (args.length) {
			case 0: // Interactive: command-line input/output
			{
				System.out.println("Please Enter 'exit' to end Execution");
				System.out.println("Input:");
				while (true) {
					try {
						bufferReader = new BufferedReader(new InputStreamReader(System.in));
						input = bufferReader.readLine().trim();
						if (input.equalsIgnoreCase("exit")) {
							break;
						} else {
							isInputProcessed(processor, input);
						}
					} catch (Exception e) {
						throw new ParkingException(Message.INVALID_REQUEST.getMessage(), e);
					}
				}
				break;
			}
			case 1:// File input/output
			{
				File inputFile = new File(args[0]);
				try {
					bufferReader = new BufferedReader(new FileReader(inputFile));
					int lineNo = 1;
					while ((input = bufferReader.readLine()) != null) {
						input = input.trim();
						boolean status = isInputProcessed(processor, input);
						if (!status) {
							System.out.println("Incorrect Command Found at line: " + lineNo + " ,Input: " + input);
						}
						lineNo++;
					}
				} catch (Exception e) {
					throw new ParkingException(Message.INVALID_FILE.getMessage(), e);
				}
				break;
			}
			default:
				System.out.println("Invalid input. Usage Style: java -jar <jar_file_path> <input_file_path>");
			}
		} catch (ParkingException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (bufferReader != null)
					bufferReader.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

	}

	private static boolean isInputProcessed(ValidateProcessor processor, String input) {
		boolean flag = false;
		if (processor.validate(input)) {
			try {
				flag = true;
				processor.execute(input);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return flag;
	}
}
