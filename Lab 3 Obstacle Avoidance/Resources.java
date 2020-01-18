package ca.mcgill.ecse211.lab3;


import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;

/**
 * This class is used to define static resources in one place for easy access and to avoid 
 * cluttering the rest of the codebase. All resources can be imported at once like this:
 * 
 * <p>{@code import static ca.mcgill.ecse211.lab3.Resources.*;}
 */
public class Resources {

  /**
   * The wheel radius in centimeters.
   * 2.13 original value
   */
  public static final int FILTER_OUT = 20;
  

  public static final double WHEEL_RAD = 2.15;
  
  /**
   * The robot width in centimeters.
   */
  public static final double TRACK = 11.3;
  
  /**
   * The speed at which the robot moves forward in degrees per second.
   */
  public static final int FORWARD_SPEED = 250;
  

  
  /**
   * The motor acceleration in degrees per second squared.
   */
  public static final int ACCELERATION = 3000;
  
  /**
   * Timeout period in milliseconds.
   */
  public static final int TIMEOUT_PERIOD = 3000;
  
  /**
   * The tile size in centimeters.
   */
  public static final double TILE_SIZE = 30.48;
  
  public static final int BAND_CENTER = 20;
  public static final int BAND_WIDTH = 3;
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
  
  public static final EV3MediumRegulatedMotor US_MOTOR =
      new EV3MediumRegulatedMotor(LocalEV3.get().getPort("C"));

  /**
   * The color sensor.
   */
  public static final EV3UltrasonicSensor US_SENSOR = 
      new EV3UltrasonicSensor(LocalEV3.get().getPort("S1"));
  /**
   * The LCD.
   */
  public static final TextLCD LCD = LocalEV3.get().getTextLCD();
  
  /**
   * The odometer.
   */
  
  public static final int US_SENSOR_SPEED = 175;
  public static final int US_RIGHT_ANGLE = 55;
  public static final int US_LEFT_ANGLE = -55;
  public static final int CRITICAL_ANGLE = 10;
  public static final int MOTOR_SPEED = 250;
  public static final int ROTATE_SPEED = 150;
  public static final int BANG_BANG_ANGLE = 55;
  public static final int BANG_BANG_FWD_SPEED = 150;
  public static final int BANG_BANG_TURN_IN_SPEED = 190;
  public static final int BANG_BANG_TURN_OUT_SPEED = 60;

  
  public static final int x0 = 2,y0 =1 ,x1 =2,y1=2,x2 =3,y2 = 3,x3 =1, y3 =3, x4 =2, y4 =2;
      
  public static Odometer odometer = Odometer.getOdometer();
  public static Navigation navigator = new Navigation(odometer);
  public static final int MIN_DISTANCE = 15;
  public static Avoid avoid = new Avoid();
  
}