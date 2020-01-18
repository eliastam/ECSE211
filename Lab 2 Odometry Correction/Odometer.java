package ca.mcgill.ecse211.lab2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.lab2.Resources.*;

/**
 * The odometer class keeps track of the robot's (x, y, theta) position.
 * 
 * @author Rodrigo Silva
 * @author Dirk Dubois
 * @author Derek Yu
 * @author Karim El-Baba
 * @author Michael Smith
 * @author Younes Boubekeur
 */


public class Odometer implements Runnable {
  
  /**
   * The x-axis position in cm.
   */
  private volatile double x;
  
  /**
   * The y-axis position in cm.
   */
  private volatile double y; // y-axis position
  
  /**
   * The orientation in degrees.
   */
  private volatile double theta; // Head angle
  
  /**
   * The (x, y, theta) position as an array
   */
  private double[] position;

  // Thread control tools
  /**
   * Fair lock for concurrent writing
   */
  private static Lock lock = new ReentrantLock(true);
  
  /**
   * Indicates if a thread is trying to reset any position parameters
   */
  private volatile boolean isResetting = false;

  /**
   * Lets other threads know that a reset operation is over.
   */
  private Condition doneResetting = lock.newCondition();

  private static Odometer odo; // Returned as singleton

  // Motor-related variables
  private static int leftMotorTachoCount = 0;
  private static int rightMotorTachoCount = 0;
  public static int lastTachoL;// Tacho L at last sample 
  public static int lastTachoR;// Tacho R at last sample
  int changeLeftTacho;
  int changeRightTacho;
  double changeRadLeft;
  double changeRadRight;

  /**
   * The odometer update period in ms.
   */
  private static final long ODOMETER_PERIOD = 25;

  
  /**
   * This is the default constructor of this class. It initiates all motors and variables once.It
   * cannot be accessed externally.
   */
  private Odometer() {
    setXYT(0, 0, 0);
  }

  /**
   * Returns the Odometer Object. Use this method to obtain an instance of Odometer.
   * 
   * @return the Odometer Object
   */
  public synchronized static Odometer getOdometer() {
    if (odo == null) {
      odo = new Odometer();
    }
    
    return odo;
  }

  /**
   * This method uses the tachometer which is the rotation of the servo-motor in degrees to measure the diffrence in (x,y,theta) cordinations at after each tacho reading
   * @author Adrian Wang
   * @author Elias Tamraz
   */
  public void run() {
    long updateStart, updateEnd;
    leftMotor.resetTachoCount();
    rightMotor.resetTachoCount();


    //get first value.
      lastTachoL = leftMotor.getTachoCount();
      lastTachoR = rightMotor.getTachoCount();
    while (true) {
      updateStart = System.currentTimeMillis();
      
      leftMotorTachoCount = leftMotor.getTachoCount();
      rightMotorTachoCount = rightMotor.getTachoCount();
    //find the change in tacho count for each motor (in degrees and radians)
      changeLeftTacho = leftMotorTachoCount - lastTachoL;
      changeRadLeft = changeLeftTacho*2.0*Math.PI/360.0;
      changeRightTacho = rightMotorTachoCount - lastTachoR;
      changeRadRight = changeRightTacho*2.0*Math.PI/360.0;

    //set our last tacho to this current one (for next time in loop)
      lastTachoL = leftMotorTachoCount;
      lastTachoR = rightMotorTachoCount;
      
      //get the arclengths
      double arcLengthL = WHEEL_RAD*changeRadLeft;
      double arcLengthR = WHEEL_RAD*changeRadRight;
      
      //find the change in theta (using formula from slides).
      double changeInTheta =  (arcLengthL - arcLengthR)/TRACK * 180 /Math.PI;
      
      //calculate the center arclength.
      double deltaCenterArclength = (arcLengthR + arcLengthL)/2.0;
      
      //in lock so we don't change x,y,theta anywhere else.
      synchronized (lock) {
          //calculate the change in the X and in Y (using formula).
          double deltaX = deltaCenterArclength*Math.sin(((theta + theta + changeInTheta)/2.0)*Math.PI/180);
          double deltaY = deltaCenterArclength*Math.cos(((theta + theta + changeInTheta)/2.0)*Math.PI/180);
          //update theta, x, y. Theta will be in radians here, but displayed in degrees.
        update(deltaX,deltaY, changeInTheta);
          
      }

      // TODO Calculate new robot position based on tachometer counts
      
      // TODO Update , odometer values with new calculated values, eg
      //odo.update(dx, dy, dtheta);

      // this ensures that the odometer only runs once every period
      updateEnd = System.currentTimeMillis();
      if (updateEnd - updateStart < ODOMETER_PERIOD) {
        try {
          Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
        } catch (InterruptedException e) {
          // there is nothing to be done
        }
      }
    }
  }
  
  // IT IS NOT NECESSARY TO MODIFY ANYTHING BELOW THIS LINE
  
  /**
   * Returns the Odometer data.
   * <p>
   * Writes the current position and orientation of the robot onto the odoData array. {@code odoData[0] =
   * x, odoData[1] = y; odoData[2] = theta;}
   * 
   * @param position the array to store the odometer data
   * @return the odometer data.
   */
  public double[] getXYT() {
    double[] position = new double[3];
    lock.lock();
    try {
      while (isResetting) { // If a reset operation is being executed, wait until it is over.
        doneResetting.await(); // Using await() is lighter on the CPU than simple busy wait.
      }

      position[0] = x;
      position[1] = y;
      position[2] = theta;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }

    return position;
  }

  /**
   * Adds dx, dy and dtheta to the current values of x, y and theta, respectively. Useful for
   * odometry.
   * 
   * @param dx
   * @param dy
   * @param dtheta
   */
  public void update(double dx, double dy, double dtheta) {
    lock.lock();
    isResetting = true;
    try {
      x += dx;
      y += dy;
      theta = (theta + (360 + dtheta) % 360) % 360; // keeps the updates within 360 degrees
      isResetting = false;
      doneResetting.signalAll(); // Let the other threads know we are done resetting
    } finally {
      lock.unlock();
    }

  }

  /**
   * Overrides the values of x, y and theta. Use for odometry correction.
   * 
   * @param x the value of x
   * @param y the value of y
   * @param theta the value of theta in degrees
   */
  public void setXYT(double x, double y, double theta) {
    lock.lock();
    isResetting = true;
    try {
      this.x = x;
      this.y = y;
      this.theta = theta;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites x. Use for odometry correction.
   * 
   * @param x the value of x
   */
  public void setX(double x) {
    lock.lock();
    isResetting = true;
    try {
      this.x = x;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites y. Use for odometry correction.
   * 
   * @param y the value of y
   */
  public void setY(double y) {
    lock.lock();
    isResetting = true;
    try {
      this.y = y;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites theta. Use for odometry correction.
   * 
   * @param theta the value of theta
   */
  public void setTheta(double theta) {
    lock.lock();
    isResetting = true;
    try {
      this.theta = theta;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

}
