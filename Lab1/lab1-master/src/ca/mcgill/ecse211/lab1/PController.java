package ca.mcgill.ecse211.lab1;

import static ca.mcgill.ecse211.lab1.Resources.*;

public class PController extends UltrasonicController {

  private static final int MOTOR_SPEED = 200;

  public PController() {
    LEFT_MOTOR.setSpeed(MOTOR_SPEED); // Initialize motor rolling forward
    RIGHT_MOTOR.setSpeed(MOTOR_SPEED);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();
  }
  public static final int GAIN_L = 25;
  public static final double GAIN_R = 1.7;
  private int error;
  private int diff;
  private int max_right = 140;
  
  @Override
  public void processUSData(int distance) {
    filter(distance);
    error = BAND_CENTER - distance;
    
    if(Math.abs(error) <= BAND_WIDTH) {
        LEFT_MOTOR.setSpeed(MOTOR_LOW); // continue moving forward
        RIGHT_MOTOR.setSpeed(MOTOR_LOW);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
    }
    else if(error > 0) {  // Too close to the wall, turn right
        diff = (int)(GAIN_L * error);
           if (diff <= 60) {
            LEFT_MOTOR.setSpeed(MOTOR_LOW + diff); 
           }
           else if(diff > 60) { //(closer than 5 cm)
            diff = 40;
            LEFT_MOTOR.setSpeed(MOTOR_LOW + diff); 
           }
    }
    else if(error < 0) { // Too far from the wall, turn left
            if (error <= -100) {
            LEFT_MOTOR.setSpeed(MOTOR_LOW);
            RIGHT_MOTOR.setSpeed(max_right);
            LEFT_MOTOR.forward();
            RIGHT_MOTOR.forward();
            }
            else if(error > -100) {
            diff = (int)(Math.abs(error) * GAIN_R);
            LEFT_MOTOR.setSpeed(MOTOR_LOW);
            RIGHT_MOTOR.setSpeed(MOTOR_LOW + diff);
            LEFT_MOTOR.forward();
            RIGHT_MOTOR.forward();
            }
    }
  }


  @Override
  public int readUSDistance() {
    return this.distance;
  }

}
