package ca.mcgill.ecse211.lab2;

import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;

/**
 * This class is used to define static resources in one place for easy access and to avoid 
 * cluttering the rest of the codebase. All resources can be imported at once like this:
 * 
 * <p>{@code import static ca.mcgill.ecse211.lab3.Resources.*;}
 */
public class Resources {
  
	
	public static int xnumLines = 0;
	public static int ynumLines = 0;
  /**
   * The wheel radius in centimeters.
   */
  public static final double WHEEL_RAD = 2.1;
  
  /**
   * The robot width in centimeters.
   * *****************************************original = 15
   * large track large turn
   * 
   * test 1. track 11.47 reding 19 actual 15  to up
   * test 2. track 11.49 reading 20 actual 16.5 to up
   * test 3. track 11.51 reading 20 actual 16
   */
  public static final double TRACK = 11.51;
  
  /**
   * The speed at which the robot moves forward in degrees per second.
   */
  public static final int FORWARD_SPEED = 150;
  
  /**
   * The speed at which the robot rotates in degrees per second.
   */
  public static final int ROTATE_SPEED = 150;
  
  /**
   * The motor acceleration in degrees per second squared.
   */
  public static final int ACCELERATION = 400;
  
  /**
   * Timeout period in milliseconds.
   */
  public static final int TIMEOUT_PERIOD = 3000;
  
  /**
   * The tile size in centimeters.
   * *********************************************original = 30.48
   */
  public static final double TILE_SIZE = 30.48;
  
  /**
   * The left motor.
   */
  public static final EV3LargeRegulatedMotor leftMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

  /**
   * The right motor.
   */
  public static final EV3LargeRegulatedMotor rightMotor =
      new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
  
  /**
   * The color sensor.
   */
  public static final EV3ColorSensor colorSensor = new EV3ColorSensor(SensorPort.S1);
  

  /**
   * The LCD.
   */
  public static final TextLCD LCD = LocalEV3.get().getTextLCD();
  
  /**
   * The odometer.
   */
  public static Odometer odometer = Odometer.getOdometer();
  
}
