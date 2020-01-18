package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.US_SENSOR;

import java.util.Arrays;

/**
 * A poller for the ultrasonic sensor. It runs continuously in its own thread,
 * polling the sensor about every 50 ms. After getting a value from the sensor,
 * it will convert the distance into centimeters and assign it to the distance
 * variable. This variable can be accessed by other classes by calling
 * getDistance().
 */
public class UltrasonicPoller implements Runnable {
	private int distance;
	private float[] usData;
	private static final short BUFFER_SIZE = 21;
	private int[] filterBuffer = new int[BUFFER_SIZE];
	public static boolean kill = false;

	public UltrasonicPoller() {
		usData = new float[US_SENSOR.sampleSize()];
	}

	/*
	 * Sensors now return floats using a uniform protocol. Need to convert US result
	 * to an integer [0,255] (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		int reading;
		int count = 0;

		while (true) {
			if (kill)
				break;
			US_SENSOR.getDistanceMode().fetchSample(usData, 0); // acquire distance data in meters
			reading = (int) (usData[0] * 100.0); // extract from buffer, convert to cm, cast to int
			// filling up the median filter and returning -1 as reading
			if (count < BUFFER_SIZE) {
				filterBuffer[count] = reading;
				distance = -1;
				count++;
			} else { // median filter
				shiftArray(filterBuffer, reading);
				int[] sample = filterBuffer.clone();
				Arrays.sort(sample); // cloning and sorting to preseve the buffer array
				distance = sample[BUFFER_SIZE / 2]; // reading median value
			}
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			} // Poor man's timed sampling
		}
	}

	/**
	 * this method shifts the array by one position and enters a new integer ad the
	 * [0] position
	 * 
	 * @param arr  array
	 * @param newI new integer to be added
	 */
	void shiftArray(int[] arr, int newI) {
		int size = arr.length;
		for (int i = 0; i < size - 1; i++) {
			arr[i] = arr[i + 1];
		}
		arr[size - 1] = newI;
	}

	/**
	 * get the filtered distance reading
	 * 
	 * @return filtered reading of distance
	 */
	public int getDistance() {
		return this.distance;
	}

}
