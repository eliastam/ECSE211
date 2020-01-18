package ca.mcgill.ecse211.lab3;
import static ca.mcgill.ecse211.lab3.Resources.*;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import static ca.mcgill.ecse211.lab3.Odometer.*;
/*
 * this class makes the robot navigate through sequence of point in order
 * @author Elias Tamraz
 * @author Adrian Wang 
 */
public class Navigation extends Thread {


  private double currentTheta, currentX, currentY, xDest, yDest;
  private boolean navigating;
  private double Position[] = new double[3];
  
  public Navigation(Odometer odo) {
odometer = odo;
navigating = false;
}
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
  public void travelTo(final double x, final double y){
    for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] {leftMotor, rightMotor}) {
      motor.stop();
      motor.setAcceleration(ACCELERATION);
  }
    
    
    this.xDest = x;
    this.yDest = y;
    navigating = true;
 
        //gets position. Synchronized to avoid collision
        synchronized (odometer) {
          Position = odometer.getXYT();
            setcurrentTheta(Position[2]);
            setcurrentX(Position[0]);
            setcurrentY(Position[1]);}
        
        //calculates actual angle to turn
        double theta =  (Math.atan2(xDest - getcurrentX(), yDest - getcurrentY()) * 180 / Math.PI) - getcurrentTheta();
        //calculates magnitude to travel
        double distance  = Math.sqrt(Math.pow((y-getcurrentY()), 2) + Math.pow((x-getcurrentX()),2));
        //finds minimum angle to turn (ie: it's easier to turn +90 deg instead of -270)
        
        
        if(theta <= -180)
            turnTo(theta + 360);
        else if(theta > 180)
            turnTo(theta - 360);
        else turnTo(theta);
        
      
        goForward(distance);
        
        navigating = false;
    }

   

  

  void goForward(double distance) {
 // drive forward 
    Resources.leftMotor.setSpeed(FORWARD_SPEED);
    Resources.rightMotor.setSpeed(FORWARD_SPEED+2);
    
    //for isNavigatingMethod    
    Resources.leftMotor.rotate(convertDistance(Resources.WHEEL_RAD, distance), true);
    Resources.rightMotor.rotate(convertDistance(Resources.WHEEL_RAD, distance), false);
    
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
  public boolean isNavigating() {
    return this.navigating;
  }
  private static int convertAngle(double radius, double width, double angle) {
    //(width * angle / radius ) / (2)
    return convertDistance(radius, Math.PI * width * angle / 360.0);}
  
  private static int convertDistance(double radius, double distance) {
    // ( D / R) * (360 / 2PI)
    return (int) ((180.0 * distance) / (Math.PI * radius));}
  
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
