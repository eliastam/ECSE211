package ca.mcgill.ecse211.lab4;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

import static ca.mcgill.ecse211.lab4.Resources.*;

public class USLocalizer {
  /**
   * @author Elis Tamraz
   * @author Adrian Wang 
   * 
   */
    public enum LocalizationType { FALLING_EDGE, RISING_EDGE };

   
    SampleProvider usDistance = US_SENSOR.getMode("Distance");
    float[] usData = new float[usDistance.sampleSize()];  
    public LocalizationType locType;

    
    private boolean noiseZone = false;
    private static final float MAX_DISTANCE = 50;
    private static final float WALL_DISTANCE = 18;
    private static final float EDGE_DISTANCE = (float) 10.4;
    private static final float EDGE_DISTANCE_R = (float) 33.25;
    
    private static final float MOTOR_SPEED = 75;
    
    public USLocalizer(  ) {
        
   
    }
    public void doLocalization() {
      
        double angleA = 0;
        double angleB = 0;
        if (locType == LocalizationType.FALLING_EDGE) {
            
            // rotate the robot until it sees no wall
            
            navigator.setSpeeds(MOTOR_SPEED,-MOTOR_SPEED);
            
            while (true) {
                if (getFilteredData()>=MAX_DISTANCE) {
                    break;
                }
            }
            // keep rotating until the robot sees a wall, then latch the angle
            while (true) {
                if (!noiseZone && getFilteredData()<EDGE_DISTANCE) {
                    angleA = odometer.getTheta();
                    noiseZone = true;
                    Sound.beep();
                odometer.setTheta(0);
                    break;
                }
            }
            // switch direction and wait until it sees no wall
            
            navigator.setSpeeds(-MOTOR_SPEED,MOTOR_SPEED);
            
            while (true) {
                if (getFilteredData()>=MAX_DISTANCE) {
                    break;
                }
            }
            
            // keep rotating until the robot sees a wall, then latch the angle
            

            
            while (true) {
                if (!noiseZone && getFilteredData()<EDGE_DISTANCE) {
                    angleB = odometer.getTheta();
                    noiseZone = true;
                    Sound.beep();
                
                    break;
                }
            }
            
            double correctionangle = angleB/2 -45;
            navigator.turnTo(correctionangle);
            
            
            
            // angleA is clockwise from angleB, so assume the average of the
            // angles to the right of angleB is 45 degrees past 'north'
            
            //double endAngle = getEndAngle(angleA,angleB);
           
            
            // update the odometer position (example to follow:)
            
            odometer.setXYT(0.0,0.0,0.0);
          
            
        } else {

          
            /*
             * 
             * The robot should turn until it sees the wall, then look for the
             * "rising edges:" the points where it no longer sees the wall.
             * This is very similar to the FALLING_EDGE routine, but the robot
             * will face toward the wall for most of it.
             */
         
          navigator.setSpeeds(MOTOR_SPEED,-MOTOR_SPEED);
            
            while (true) {
                if (getFilteredData()<=WALL_DISTANCE) {
                    break;
                }
            }
            
            
            
            while (true) {
                if ( getFilteredData()>EDGE_DISTANCE_R) {
                    angleB = odometer.getTheta();
                  
                    Sound.beep();
                    odometer.setTheta(0);
                    break;
                }
            }
            
            navigator.setSpeeds(-MOTOR_SPEED,MOTOR_SPEED);
            
            while (true) {
                if (getFilteredData()<=WALL_DISTANCE) {
                    break;
                }
            }
            while (true) {
                if ( getFilteredData()>EDGE_DISTANCE_R) {
                    angleA = odometer.getTheta();
                    
                    Sound.beep();
              
                    break;
                }
            }
            double correctangle = 225-angleA/2;
           navigator.turnTo(-correctangle);
            odometer.setTheta(0);
        }
    }
    
    public float getFilteredData() {
      US_SENSOR.fetchSample(usData, 0);
        float distance = usData[0]*100;
        
        if (distance > MAX_DISTANCE) distance = MAX_DISTANCE;
                
        return distance;
    }
}