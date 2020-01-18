package ca.mcgill.ecse211.lab4;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

import static ca.mcgill.ecse211.lab4.Resources.*;
/**
 * 
 * @author ELias Tamraz
 * @author Adrian Wang
 * This class is responsible for lozalizing the robot to 1,1 after it localized using us lozalizer
 */
public class LightLocalizer {

  
  

  private static final float MOTOR_SPEED = 75;
  

  private static final double CENTER_TO_SENSOR = 4.5;
  
  
  private float color[] = new float[Resources.colorSensor.sampleSize()];
  

  private static final float BLACK = (float) 0.26;
  double [] position = new double [3];
  public LightLocalizer() {

  }
  
 
  public void doLocalization() {
   
      navigator.setSpeeds(MOTOR_SPEED,MOTOR_SPEED);
      
      while (true){
          Resources.colorSensor.fetchSample(color, 0);
          float darkness = color[0];
          if (darkness < BLACK){//Detect the dark lines
          odometer.setY(TILE_SIZE-CENTER_TO_SENSOR);
            
          
              Sound.beep();
              break;
          }
      }
      // extend the sensor arm before stopping the motors
      while (true){
        position = odometer.getXYT();
        
          if (position[1]>= TILE_SIZE/2){
              navigator.setSpeeds(-MOTOR_SPEED, -MOTOR_SPEED);
              break;
          }
      }
      
      
      // Turn the robot 90 degrees towards y-axis, then move to appropriate position
      navigator.turnTo(90);
      navigator.setSpeeds(MOTOR_SPEED,MOTOR_SPEED);
      while (true){
          colorSensor.fetchSample(color, 0);
          float darkness = color[0];
          if (darkness < BLACK){ //Detect the dark lines
           odometer.setX(TILE_SIZE-CENTER_TO_SENSOR);
              
              Sound.beep();
              break;
          }
      }
      // Similar to x-axis operation
      while (true){
        position = odometer.getXYT();
          if (position[0] >=TILE_SIZE){
              navigator.setSpeeds(0, 0);
              break;
          }
      }
      
      
    navigator.travelTo(TILE_SIZE,TILE_SIZE);
      
      
      double whatever = 0;
      
      whatever =odometer.getTheta();
      navigator.turnTo(-whatever);
      
  }
  
  public double wrapAngle (double angle){
      if (angle<0) {
          return angle+360;
      } else if (angle>360){
          return angle-360;
      } else {
          return angle;
      }
  }

}