package ca.mcgill.ecse211.lab3;

import static ca.mcgill.ecse211.lab3.Resources.*;
import lejos.hardware.Sound;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.Encoder;
import lejos.robotics.SampleProvider;

/*
 * this class implements Navigation with Bang-Bang controller to avoid obstacles
 * it rotates us sensor to detect obstacles
 * @author Elias Tamraz
 * @author Adrian Wang 
 */
public class Avoid extends Thread {
  
  
  private double currentTheta, currentX, currentY, xDest, yDest;
  private boolean navigating;
  private double Position[] = new double[3];
  int distance;
  SampleProvider usDistance = US_SENSOR.getMode("Distance");
  float[] usData = new float[usDistance.sampleSize()];  
  
    public Avoid() {
    navigating = false;
  }
  
  @Override
  public void run() {
    travelTo(TILE_SIZE * (x0),TILE_SIZE * (y0));
    travelTo(TILE_SIZE * (x1),TILE_SIZE * (y1));
    travelTo(TILE_SIZE * (x2),TILE_SIZE * (y2));
    travelTo(TILE_SIZE * (x3),TILE_SIZE * (y3));
    travelTo(TILE_SIZE * (x3),TILE_SIZE * (y3));
    
}
/*
 * This method makes the robot travel to certain point given its x and y
 */
  private void travelTo(double x, double y) {
 
    
    for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {leftMotor, rightMotor}) {
        motor.stop();
        motor.setAcceleration(ACCELERATION);
    }
    this.xDest = x;
    this.yDest = y;
    navigating = true;
    
    synchronized (odometer) {
      Position = odometer.getXYT();
      setcurrentTheta(Position[2]);
      setcurrentX(Position[0]);
      setcurrentY(Position[1]);
      }
    
    //calculates actual angle to turn
    double theta =  (Math.atan2(xDest - getcurrentX(), yDest - getcurrentY()) * 180 / Math.PI) - getcurrentTheta();
    //calculates magnitude to travel
    double distance  = Math.sqrt(Math.pow((y-getcurrentY()), 2) + Math.pow((x-getcurrentX()),2));
    //finds minimum angle to turn 
    if(theta <= -180) {
        turnTo(theta + 360);
    }else if(theta > 180) {
        turnTo(theta - 360);
    } else {
      turnTo(theta);
    }
     goForward(distance);
     US_MOTOR.resetTachoCount(); //reset tacho count of us sensor motor
     US_MOTOR.setSpeed(US_SENSOR_SPEED);
     while (leftMotor.isMoving() || rightMotor.isMoving()) { //checks if the robot is moving
                     while (!US_MOTOR.isMoving()){ 
                       //Rotate the sensor to scan the surroundings if the sensor is not rotating
                     if (US_MOTOR.getTachoCount()>=CRITICAL_ANGLE){
                         US_MOTOR.rotateTo(US_LEFT_ANGLE,true);
                     } else {
                         US_MOTOR.rotateTo(US_RIGHT_ANGLE,true);
                     }
                     }
       US_SENSOR.fetchSample(usData,0);                           // acquire data
       distance=(int)(usData[0]*100.0);                    //get the data and convert it to cm
       filter (distance);
             if(distance <= BAND_CENTER){
                 Sound.beep();   // alarm mode
                 leftMotor.stop(true); // Stop the robot and quit navigation mode
                 rightMotor.stop(false);
                 leftMotor.rotate(-90,true); //turn -90 degrees so the robot is parallel to the obstacle
                 rightMotor.rotate(-90,false);
                 navigating = false;
                 }
             try { Thread.sleep(50); } catch(Exception e){}     
                 }
         
           
     if (!this.isNavigating()){
      DO_BANG_BANG_MOVEMENT(); // Implements bangbang controller to avoid the obstacle
      US_MOTOR.rotateTo(0); 
      navigating = true; //continue navigating
      travelTo(x,y); // go back to the destination
       return;   }
     
   US_MOTOR.rotateTo(0);
  }

  /*
   * this method implements bang bang controller
   */
  private void DO_BANG_BANG_MOVEMENT() {
    turnTo(odometer.getTheta()-90);
    
    double prevtheta = odometer.getTheta();
  
    US_MOTOR.rotateTo(BANG_BANG_ANGLE);

   while (Math.abs(odometer.getThetax()-prevtheta)<80  ){
     
     US_SENSOR.fetchSample(usData,0);                           // acquire data
     distance=(int)(usData[0]*100.0);                    // extract from buffer, cast to int
     int Error = (int) (BAND_CENTER - distance);
     
     if (Math.abs(Error)<= BAND_WIDTH){ //moving in straight line
         leftMotor.setSpeed(BANG_BANG_FWD_SPEED);
         rightMotor.setSpeed(BANG_BANG_FWD_SPEED);
         leftMotor.forward();
         rightMotor.forward();
     } else if (Error > 0){ //too close to wall
         leftMotor.setSpeed(BANG_BANG_FWD_SPEED);// Setting the outer wheel to reverse
         rightMotor.setSpeed(BANG_BANG_TURN_OUT_SPEED); 
         leftMotor.backward();
         rightMotor.forward();
     } else if (Error < 0){ // getting too far from the wall
         rightMotor.setSpeed(BANG_BANG_TURN_IN_SPEED);
         leftMotor.setSpeed(BANG_BANG_FWD_SPEED);// Setting the outer wheel to move faster
         rightMotor.forward();
         leftMotor.forward();
     }
   }
  
   Sound.beep();
   leftMotor.stop();
   rightMotor.stop();
   US_MOTOR.rotateTo(0);

  }
/*
 * this method filters the result from us sensor
 */
  private void filter(double distance) {
  //int Error = bandCenter - this.distance;
    
    int filterControl = 0;
    
    // rudimentary filter - copied from TA code on myCourses
        if (distance >= 255 && filterControl < FILTER_OUT) {
            // bad value, do not set the distance var, however do increment the
            // filter value
            filterControl++;
        } else if (distance >= 255) {
            // We have repeated large values, so there must actually be nothing
            // there: leave the distance alone
            this.distance = (int) distance;
        } else {
            // distance went below 255: reset filter and leave
            // distance alone.
            filterControl = 0;
            this.distance = (int) distance;
        }
}
    
  
/*
 * turns the robot to the angle facing the destination point
 */
  void turnTo(double theta) {
    // turn degrees clockwise
       Resources.leftMotor.setSpeed(ROTATE_SPEED);
       Resources.rightMotor.setSpeed(ROTATE_SPEED);
       //calculates angel to turn to and rotates
       Resources.leftMotor.rotate(convertAngle(Resources.WHEEL_RAD, Resources.TRACK, theta), true);
       Resources.rightMotor.rotate(-convertAngle(Resources.WHEEL_RAD, Resources.TRACK, theta), false);
       
     }
/*
 * resturn boolean navigating
 */
  public boolean isNavigating() {
    return this.navigating;
  }
  
  private static int convertAngle(double radius, double width, double angle) {
    //(width * angle / radius ) / (2)
    return convertDistance(radius, Math.PI * width * angle / 360.0);}
  
  private static int convertDistance(double radius, double distance) {
    // ( D / R) * (360 / 2PI)
    return (int) ((180.0 * distance) / (Math.PI * radius));}

  void goForward(double distance) {
    // drive forward 
       Resources.leftMotor.setSpeed(MOTOR_SPEED);
       Resources.rightMotor.setSpeed(MOTOR_SPEED);
       
       //for isNavigatingMethod    
       Resources.leftMotor.rotate(convertDistance(Resources.WHEEL_RAD, distance), true);
       Resources.rightMotor.rotate(convertDistance(Resources.WHEEL_RAD, distance), true);
       
     } 


  public double getcurrentTheta() {return currentTheta;}
  public double getcurrentX() {return currentX;}
  public double getcurrentY() {return currentY;}
  public void setcurrentTheta(double currentTheta) {this.currentTheta = currentTheta;}
  public void setcurrentX(double currentX) {this.currentX = currentX;}
  public void setcurrentY(double currentY) {this.currentY = currentY;}

  public double getXDest() {return this.xDest;}
  public double getYDest() {return this.yDest;}
  public void setNavigating(boolean b) {
    this.navigating = b;
    
  }
}