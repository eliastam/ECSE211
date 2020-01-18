package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.usLocalizer;
import static ca.mcgill.ecse211.lab5.Resources.usPoller;
import static ca.mcgill.ecse211.lab5.Resources.shooterMotor;
import static ca.mcgill.ecse211.lab5.Resources.SHOOTER_MOTOR_SPEED;
import lejos.hardware.Button;
import lejos.hardware.Sound;

/**
 * The main driver class for the odometry lab.
 */
public class Main {
	public static final int TARGETX = 6;
	public static final int TARGETY = 3;

	/**
	 * The main entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// ***Part one of the demo: static shooting*** //
		int shots = 0;
		while (shots < 5) {
			shooterMotor.rotate(-190); // cock the launcher
			Sound.twoBeeps(); // beep for dramatic effect
			shooterMotor.rotate(240); // shoot
			shooterMotor.rotate(-50); // reset angle
			Button.waitForAnyPress(); // wait for reload
			shots++;
		}

		// ***Part two of the demo: move to a square and launch the ball*** //

		// ==== Phase 1: localize using US sensor and rammer ==== //
		Thread pollerThread = new Thread(usPoller);
		Thread localizerThread = new Thread(usLocalizer);
		Thread localizerDisplayThread = new Thread(new UltrasonicLocalizerDisplay());
		pollerThread.start();
		localizerThread.start();
		localizerDisplayThread.start();
		Button.waitForAnyPress();

		// ==== Phase 2: Navigate to position and take aim ==== //
		// killing all previous threads
		UltrasonicPoller.kill = true;
		UltrasonicLocalizerDisplay.kill = true;
		try {
			pollerThread.join(5000);
			localizerThread.join(5000);
			localizerDisplayThread.join(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		// Navigate
		Navigation.getReadyToShoot(TARGETX, TARGETY);

		// ==== Phase 3: launch the ball ==== //
		shots = 0;
		while (shots < 5) {
			shooterMotor.rotate(-190); // cock the launcher
			Sound.twoBeeps(); // beep for dramatic effect
			shooterMotor.rotate(240); // shoot
			shooterMotor.rotate(-50); // reset angle
			Button.waitForAnyPress(); // wait for reload
			shots++;
		}

		System.exit(0);
	}

	public static void sleepFor(long duration) {
		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			// There is nothing to be done here
		}
	}
}
