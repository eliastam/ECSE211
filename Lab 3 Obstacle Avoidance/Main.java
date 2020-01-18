package ca.mcgill.ecse211.lab3;
import lejos.hardware.Button;

import ca.mcgill.ecse211.lab3.Display;
import static ca.mcgill.ecse211.lab3.Resources.odometer;
//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.lab3.Resources.*;
/*
 * main class is a class that takes input from the user and runs threads
 * accordingly
 * @author Elias Tamraz
 * @author Adrian Wang
 */


public class Main {
  public static void main(String[] args) {
    int buttonChoice;
    buttonChoice = NavigationOrAvoid();
    
    if (buttonChoice == Button.ID_LEFT){ //if left button is pressed do Navigation
      new Thread(odometer).start(); 
      new Thread(new Display()).start();
      navigator.start();
      }
    
    else { //if Right button is pressed do Avoid navigation
      new Thread(odometer).start(); 
      new Thread(new Display()).start();
      avoid.start();
      }
  
  while (Button.waitForAnyPress() != Button.ID_ESCAPE);
  System.exit(0);
 
      }
    

/*
 * NavigationOrAvoid is a method that returns the ID of the pressed button
 */
  private static int NavigationOrAvoid() {
    int buttonChoice;
    Display.showText("< Left | Right >",
                     "       |        ",
                     " To    | Avoid  ",
                     " Loc   |    ");
    
    do {
      buttonChoice = Button.waitForAnyPress(); // left or right press
    } while (buttonChoice != Button.ID_LEFT && buttonChoice != Button.ID_RIGHT);
    return buttonChoice;
  }

  
  public static void sleepFor(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      // There is nothing to be done here
    }
  }
  
}