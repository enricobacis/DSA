package sdu.dsa.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/** Class used to simulate a device and redirect the output and the
 * error streams from the device virtual machine to the caller
 * virtual machine.
 *
 * @author DSA-Project Group [Spring 2012]
 * @version 1.0
 */
public class DeviceSimulator extends Thread {

	private Process process;
	private String processString;
	private boolean processRunning;
	private List<String> command;

	private static File currentDirectory = new File(".");

	/**
	 * Method setDirectory.
	 * @param directory the working directory
	 */
	public static void setDirectory(String directory) {
		currentDirectory = new File(directory); 
	}

	/**
	 * Constructor for DeviceSimulator.
	 * @param command the arguments to pass to the creating process
	 * @throws IOException
	 */
	public DeviceSimulator(String ... command) throws IOException {
		this(Arrays.asList(command));
	}

	/**
	 * Constructor for DeviceSimulator.
	 * @param command the arguments to pass to the creating process
	 * @throws IOException
	 */
	public DeviceSimulator(List<String> command) throws IOException {
		this.command = command;
		processRunning = true;
		start();
	}
	
	/**
	 * Stop the device.
	 */
	public void stopDevice() {
		processRunning = false;
		process.destroy();
	}

	/**
	 * Method run.
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		processString = "Process " + Arrays.toString(command.toArray());

		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.directory(currentDirectory);

		try {
			process = processBuilder.start();
		} catch (IOException e1) {
			printInfo("Cannot create the Process");
			e1.printStackTrace();
		}

		printInfo("started");

		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;

		try {
			while (processRunning && (line = br.readLine()) != null) {
				System.out.println(line);
			}

			int exitValue = process.waitFor();
			br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while (processRunning && (line = br.readLine()) != null) {
				System.err.println(line);
			}
			
			printInfo("terminated [return: " + exitValue + "]");
		} catch (Exception e) {
			printInfo("Problem with the Process");
			e.printStackTrace();
		}
	}

	/**
	 * Method printInfo.
	 * @param error String
	 */
	private void printInfo(String error) {
		System.out.println(processString + ": " + error);
	}
}
