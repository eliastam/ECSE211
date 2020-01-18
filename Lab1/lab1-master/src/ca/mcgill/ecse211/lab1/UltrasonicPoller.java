package ca.mcgill.ecse211.lab1;

import static ca.mcgill.ecse211.lab1.Resources.*;

/**
 * Samples the US sensor and invokes the selected controller on each cycle.
 * 
 * Control of the wall follower is applied periodically by the UltrasonicPoller thread. The while
 * loop at the bottom executes in a loop. Assuming that the us.fetchSample, and cont.processUSData
 * methods operate in about 20ms, and that the thread sleeps for 50 ms at the end of each loop, then
 * one cycle through the loop is approximately 70 ms. This corresponds to a sampling rate of 1/70ms
 * or about 14 Hz.
 */
public class UltrasonicPoller implements Runnable {

  private UltrasonicController controller;
  private float[] usData;
  float[] readings = {0,0,0}; //initialize an array that will store readings from the buffer
  public UltrasonicPoller() {
    usData = new float[US_SENSOR.sampleSize()];
    controller = Main.selectedController;
   
  }

  /*
   * Sensors now return floats using a uniform protocol. Need to convert US result to an integer
   * [0,255] (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    int distance;
    while (true) {
      US_SENSOR.getDistanceMode().fetchSample(usData, 0); // acquire distance data in meters
      readings[0]= usData[0] ;
      US_SENSOR.getDistanceMode().fetchSample(usData, 0); // acquire distance data in meters
      readings[1]= usData[0] ;
      US_SENSOR.getDistanceMode().fetchSample(usData, 0); // acquire distance data in meters
      readings[2]= usData[0] ;
      float sum =  (readings[0] + readings[1] + readings[2]);
      float average = sum/3;
      distance = (int) (average * 100); // take the average of three reading, convert to cm, cast to int
      controller.processUSData(distance); // now take action depending on value
      try {
        Thread.sleep(50);
      } catch (Exception e) {
      } 
    }
  }

}
