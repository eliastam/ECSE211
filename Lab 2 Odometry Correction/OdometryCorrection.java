package ca.mcgill.ecse211.lab2;

import static ca.mcgill.ecse211.lab2.Resources.*;
import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

/**
 * This class is used to correct (x, y, Theta) with respect to the grid lines by using the values that the light sensor returns
 */
public class OdometryCorrection implements Runnable {
  private static final long CORRECTION_PERIOD = 10;
  private SampleProvider lscolor = colorSensor.getMode("Red");
  private float color[] = new float[Resources.colorSensor.sampleSize()];
  private float lastColor;
  private double lastPosition[] = new double[3];
  int lines = 1;
  private double[] position;
  int colorId;
  String colorName = "";
  int last_id;
  private boolean vertical = true;
  private boolean positive = true;
  double correction = 0;
  
  /*
   * This method is used to correct (x, y, Theta) with respect to the grid lines by using the values that the light sensor returns
   * @author Adrian Wang
   * @author Elias Tamraz
   */
  public void run() {
    //lastPosition = odometer.getXYT();
    //lscolor.fetchSample(color, 0);
    long correctionStart, correctionEnd;
    lastColor = color[0];
    while (true) {
      correctionStart = System.currentTimeMillis();
      //Resources.colorSensor.fetchSample(color,0);
      lastPosition = odometer.getXYT();
      lscolor.fetchSample(color, 0);
      
      if (color[0] < 0.25) {
          Sound.beep();
          if(lastPosition[2] > 340 || lastPosition[2] < 20) {
        	  //up
        	  ynumLines++;
        	  correction = TILE_SIZE*ynumLines-6.5;
        	  odometer.setY(correction);
          }
          else if(lastPosition[2] > 70 && lastPosition[2] < 110) {
        	  xnumLines++;
        	  correction = TILE_SIZE*xnumLines-6.5;
        	  odometer.setX(correction);
          }
          else if(lastPosition[2] > 160 && lastPosition[2] < 200) {
        	  correction = TILE_SIZE*ynumLines+8.5;
        	  odometer.setY(correction);
        	  ynumLines--;
          }
          else if(lastPosition[2] > 250 && lastPosition[2] < 290) {
        	  correction = TILE_SIZE*xnumLines+6.5;
        	  odometer.setX(correction);
        	  xnumLines--;
          }
      }
          //numLines ++;
          //increase number of lines
         // numLines ++;
          
          //get robots current position
//          position = odometer.getXYT();
//          if (vertical) {
//            //robot is moving up
//            if (positive) {
//                //correct at each line based on tile size
//                if(numLines == 1) {
//                    odometer.setY(TILE_SIZE);
//                }
//                else if(numLines == 2) {
//                    odometer.setY(2*TILE_SIZE);
//                }
//                else if(numLines == 3) {
//                    odometer.setY(3*TILE_SIZE);
//                    numLines = 0;
//                    vertical = false;
//                }
//            }
//            //robot is moving down
//            else {
//                //correct at each line based on tile size
//                if(numLines == 1) {
//                    odometer.setY(3*TILE_SIZE);
//                }
//                else if(numLines == 2) {
//                    odometer.setY(2*TILE_SIZE);
//                }
//                else if(numLines == 3) {
//                    odometer.setY(TILE_SIZE);
//                    numLines = 0;
//                    vertical = false;
//                }
//            }
//        }
//        //horizontal
//        else {
//            //robot is moving right
//            if (positive) {
//                //correct at each line based on tile size
//                if (numLines == 1) {
//                    odometer.setX(TILE_SIZE);
//                }
//                else if (numLines == 2) {
//                    odometer.setX(2*TILE_SIZE);
//                }
//                else if (numLines == 3) {
//                    odometer.setX(3*TILE_SIZE);
//                    numLines = 0;
//                    vertical = true;
//                    positive = false;
//                }
//            }
//            //robot is moving left
//            else {
//                //correct at each line based on tile size
//                if(numLines == 1) {
//                    odometer.setX(3*TILE_SIZE);
//                }
//                else if(numLines == 2) {
//                    odometer.setX(2*TILE_SIZE);
//                }
//                else if(numLines == 3) {
//                    odometer.setX(TILE_SIZE);
//                    numLines = 0;
//                    vertical = true;
//                }
//            }
//            
//        
//        }
//      }
      
      
     
   

      
    
     

      // put your correction code here
     
      //Get intensity sample
      // TODO Trigger correction (When do I have information to correct?)
      
      // TODO Calculate new (accurate) robot  

      // TODO Update odometer with new calculated (and more accurate) values, eg:
      //odometer.setXYT(0.3, 19.23, 5.0);

      // this ensures the odometry correction occurs only once every period
      correctionEnd = System.currentTimeMillis();
      if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
        Main.sleepFor(CORRECTION_PERIOD - (correctionEnd - correctionStart));
      }
    }
  }
}
  
