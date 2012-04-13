package sdu.dsa.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class DeviceSimulator extends Thread {

	private Process process;
	private String processString;
	private boolean processRunning;
	private List<String> command;

	private static File currentDirectory = new File(".");

	public static void setDirectory(String directory) {
		currentDirectory = new File(directory); 
	}

	public DeviceSimulator(String ... command) throws IOException {
		this(Arrays.asList(command));
	}

	public DeviceSimulator(List<String> command) throws IOException {
		this.command = command;
		processRunning = true;
		start();
	}
	
	public void stopDevice() {
		processRunning = false;
		process.destroy();
	}

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

	private void printInfo(String error) {
		System.out.println(processString + ": " + error);
	}
}
