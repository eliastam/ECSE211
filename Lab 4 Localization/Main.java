package ca.mcgill.ecse211.lab4;

import lejos.hardware.Button;

import ca.mcgill.ecse211.lab4.Display;
import ca.mcgill.ecse211.lab4.USLocalizer.LocalizationType;
import static ca.mcgill.ecse211.lab4.Resources.odometer;

//static import to avoid duplicating variables and make the code easier to read
import static ca.mcgill.ecse211.lab4.Resources.*;
/*
 * main class is a class that takes input from the user and runs threads
 * accordingly
 * @author Elias Tamraz
 * @author Adrian Wang
 */
public class Main {
  
  
  public static void main(String[] args) {
  int buttonChoice;
    buttonChoice = FallingOrRisingedge();
    new Thread(odometer).start(); 
    new Thread(new Display()).start();
    
    
    if (buttonChoice == Button.ID_LEFT){ //do falling edge
     usl.locType = LocalizationType.FALLING_EDGE;
     
      }
    
    else { //
      
      usl.locType = LocalizationType.RISING_EDGE;
      }
      
    
 
    usl.doLocalization();
    while(Button.waitForAnyPress() != Button.ID_ENTER);
    ll.doLocalization();
  
    
    
    
    
    
    
    
  
  while (Button.waitForAnyPress() != Button.ID_ESCAPE);
  System.exit(0);
 
      }
    

/*
 * NavigationOrAvoid is a method that returns the ID of the pressed button
 */
  private static int FallingOrRisingedge() {
    int buttonChoice;
    Display.showText("< Left | Right >",
                     "       |        ",
                     "Falling| Rsing  ",
                     "Edge  |  Edge ");
    
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
