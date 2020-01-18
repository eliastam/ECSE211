package ca.mcgill.ecse211.lab1;

import static ca.mcgill.ecse211.lab1.Resources.*;

public class BangBangController extends UltrasonicController {
public static final int MOTOR_DELTA = 30;
public static final int BACKWARD_ROTATION = -60;
public static final int LEFT_MOTOR_ROTATION = 110;
public static final int RIGHT_MOTOR_ROTATION = -130;
public static final int TIMES_TWO = 2;
  public BangBangController() {
    LEFT_MOTOR.setSpeed(MOTOR_HIGH); // Start robot moving forward
    RIGHT_MOTOR.setSpeed(MOTOR_HIGH);
    LEFT_MOTOR.forward();
    RIGHT_MOTOR.forward();
  }
  private int error;
  @Override
  public void processUSData(int distance) {
    filter(distance);
    error = BAND_CENTER - distance;
    if(Math.abs(error) <= BAND_WIDTH) { //within acceptable distance from the wall
        LEFT_MOTOR.setSpeed(MOTOR_LOW); // continue moving forward
        RIGHT_MOTOR.setSpeed(MOTOR_LOW);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
    }else if(error > 5) { // distance with the wall is less than 5 cm
        LEFT_MOTOR.rotate(BACKWARD_ROTATION,true);
        RIGHT_MOTOR.rotate(BACKWARD_ROTATION);
        LEFT_MOTOR.rotate(LEFT_MOTOR_ROTATION,true);
        RIGHT_MOTOR.rotate(RIGHT_MOTOR_ROTATION);
    }else if(error > 0 && error < 5) {
        LEFT_MOTOR.setSpeed(MOTOR_LOW + MOTOR_DELTA); // close to the wall, turn right
        RIGHT_MOTOR.setSpeed(MOTOR_LOW);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
    }else if (error<=-20 && error>-300) { // far from the wall more than 50 cm, 
        LEFT_MOTOR.setSpeed(MOTOR_LOW); 
        RIGHT_MOTOR.setSpeed(MOTOR_LOW + MOTOR_DELTA*TIMES_TWO);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
    }else if (error >-20 && error<0) { // far from the wall less than 50 cm, 
        LEFT_MOTOR.setSpeed(MOTOR_LOW); 
        RIGHT_MOTOR.setSpeed(MOTOR_LOW + MOTOR_DELTA);
        LEFT_MOTOR.forward();
        RIGHT_MOTOR.forward();
    }
  }
  @Override
  public int readUSDistance() {
    return this.distance;
  }
}
