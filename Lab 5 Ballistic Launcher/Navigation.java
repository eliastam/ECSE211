package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.ACCELERATION;
import static ca.mcgill.ecse211.lab5.Resources.FORWARD_SPEED;
import static ca.mcgill.ecse211.lab5.Resources.LCD;
import static ca.mcgill.ecse211.lab5.Resources.ROTATE_SPEED;
import static ca.mcgill.ecse211.lab5.Resources.TILE_SIZE;
import static ca.mcgill.ecse211.lab5.Resources.TRACK;
import static ca.mcgill.ecse211.lab5.Resources.WHEEL_RAD;
import static ca.mcgill.ecse211.lab5.Resources.WPOINT_RAD;
import static ca.mcgill.ecse211.lab5.Resources.leftMotor;
import static ca.mcgill.ecse211.lab5.Resources.odometer;
import static ca.mcgill.ecse211.lab5.Resources.rightMotor;

import lejos.hardware.Sound;

public class Navigation {
	// integers to hold which square the robot is currently in
	public static int xTile = 0;
	public static int yTile = 0;

	/**
	 * Lets other methods know if the robot is currently navigating to a waypoint.
	 */
	private static boolean isNavigating;

	/**
	 * An array containing the current X, Y, and theta of the robot, as given by the
	 * odometer.
	 */
	private static double[] position;
	/**
	 * Vector in the x direction from the robots current position to the waypoint.
	 */
	private static double vectorX;

	/**
	 * Vector in the y direction from the robots current position to the waypoint.
	 */
	private static double vectorY;

	/**
	 * The angle the robot needs to go to so that it is a straight line to the
	 * waypoint.
	 */
	private static double heading;

	/**
	 * Constructor for the Navigation class.
	 */
	public Navigation() {
		isNavigating = false;
		leftMotor.setAcceleration(ACCELERATION);
		rightMotor.setAcceleration(ACCELERATION);
	}

	/**
	 * The main method used to travel to a waypoint. The method will loop at
	 * approximately 20 Hz and make sure the robot is on the correct path towards
	 * the waypoint. It will call turnTo() if the robot needs to make a change in
	 * heading. It will also call avoidObject() if the robot is about to run into an
	 * obstacle.
	 * 
	 * @param x the X coordinate of the waypoint
	 * @param y the Y coordinate of the waypoint
	 */
	public static void travelTo(double x, double y) {

		position = odometer.getXYT();
		vectorX = x - position[0];
		vectorY = y - position[1];

		while (distance(vectorX, vectorY) > WPOINT_RAD) {
			position = odometer.getXYT(); // Get position of the robot from the odometer
			// Update the vectors from the current position to the waypoint
			vectorX = x - position[0];
			vectorY = y - position[1];
			// Update the heading, and ensure it stays between 0 and 360 degrees
			heading = Math.toDegrees(Math.atan2(vectorX, vectorY));
			heading = (heading + 360) % 360;
			LCD.drawString("Heading: " + Double.toString(heading), 0, 3);
			// If the robot isn't too close to the waypoint, allow it to correct its heading
			// by rotating
			if (distance(vectorX, vectorY) > (2 * WPOINT_RAD)) {
				turnTo(heading);
			}

			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);
			leftMotor.forward();
			rightMotor.forward();
			try {
				Thread.sleep(50);
			} catch (Exception e) {
			}

		}
		leftMotor.stop(true);
		rightMotor.stop(false);
		Sound.twoBeeps(); // Beep when it has reached a waypoint
	}

	/**
	 * Rotates the robot to an absolute angle theta. It also ensures the robot turns
	 * the minimal angle to get to theta.
	 * 
	 * @param theta the absolute angle the robot should turn to in degrees
	 */
	public static void turnTo(double theta) {
		double angleDiff = theta - odometer.getXYT()[2];
		// Don't correct the angle if it is within a certain threshold
		if (Math.abs(angleDiff) < 3.0 || Math.abs(angleDiff) > 357.0) {
			return;
		}
		leftMotor.setSpeed(ROTATE_SPEED);
		rightMotor.setSpeed(ROTATE_SPEED);
		// This ensures the robot uses the minimal angle when turning to theta
		if (Math.abs(angleDiff) > 180.0) {
			angleDiff = Math.signum(angleDiff) * 360.0 - angleDiff;
			leftMotor.rotate(convertAngle(-angleDiff), true);
			rightMotor.rotate(convertAngle(angleDiff), false);
		} else {
			leftMotor.rotate(convertAngle(angleDiff), true);
			rightMotor.rotate(convertAngle(-angleDiff), false);
		}
	}

	/**
	 * Returns a boolean of whether or not the robot is currently navigating to a
	 * waypoint.
	 * 
	 * @return true if the robot is currently navigating to a waypoint.
	 */
	public boolean isNavigating() {
		return isNavigating;
	}

	/**
	 * Converts input distance to the total rotation of each wheel needed to cover
	 * that distance.
	 * 
	 * @param distance
	 * @return the wheel rotations necessary to cover the distance
	 */
	public static int convertDistance(double distance) {
		return (int) ((180 * distance) / (Math.PI * WHEEL_RAD));
	}

	/**
	 * Converts input angle to the total rotation of each wheel needed to rotate the
	 * robot by that angle.
	 * 
	 * @param angle
	 * @return the wheel rotations necessary to rotate the robot by the angle
	 */
	public static int convertAngle(double angle) {
		return convertDistance((Math.PI * TRACK * angle) / 360.0);
	}

	/**
	 * Calculates the euclidian distance given an X and Y distance, in cm.
	 * 
	 * @param deltaX X distance
	 * @param deltaY Y distance
	 * @return Euclidean distance
	 */
	private static double distance(double deltaX, double deltaY) {
		return Math.sqrt((Math.pow((deltaX), 2) + Math.pow((deltaY), 2)));
	}

	/**
	 * moves the robot forward by x many tile lengths
	 * 
	 * @param i number of tile lengths
	 */
	public static void moveForwardByTile(double i) {
		leftMotor.rotate(convertDistance(TILE_SIZE * i), true);
		rightMotor.rotate(convertDistance(TILE_SIZE * i), false);
	}

	/**
	 * turns the robot 90 degrees left
	 */
	public static void turnLeft() {
		leftMotor.rotate(convertAngle(-90.0), true);
		rightMotor.rotate(convertAngle(90.0), false);
	}

	/**
	 * turns the robot 90 degrees right
	 */
	public static void turnRight() {
		leftMotor.rotate(convertAngle(90.0), true);
		rightMotor.rotate(convertAngle(-90.0), false);
	}

	/**
	 * returns the nearest available square to shoot from which is N square away
	 * form the target
	 * 
	 * @param targetX x coordinates of the target square, starting from 0
	 * @param targetY y coordinates of the target square, starting from 0
	 * @return the target square coordinates and the angle needed in an int array of
	 *         size [3]
	 */
	public static int[] findTarget(int targetX, int targetY) {
		int[] result = new int[3];
		double shortest_dist = 100;
		int[][] notableSquares = { { 0, 5 }, { 4, 4 }, { 5, 0 }, { 4, -4 }, { 0, -5 }, { -4, -4 }, { -5, 0 },
				{ -4, 4 } };
		int[] thetaOptions = { 180, 225, 270, 315, 0, 45, 90, 135 };

		for (int i = 0; i < notableSquares.length; i++) {
			int[] pair = notableSquares[i];
			boolean ooX = pair[0] + targetX > Resources.ARENA_X || pair[0] + targetX < 0;
			boolean ooY = pair[1] + targetY > Resources.ARENA_Y || pair[1] + targetY < 0;
			if (ooX || ooY) {
				continue;
			} else {
				double dist = Math
						.sqrt((pair[0] + targetX) * (pair[0] + targetX) + (pair[1] + targetY) * (pair[1] + targetY));
				if (dist < shortest_dist) {
					result[0] = pair[0] + targetX;
					result[1] = pair[1] + targetY;
					shortest_dist = dist;
					result[2] = (thetaOptions[i] - 90 + 360) % 360;
				}
			}
		}
		return result;
	}
	
	/**
	 * moves the robot to the optimal square and take aim
	 * @param targetX target square X coordinate
	 * @param targetY target square Y coordinate
	 */
	public static void getReadyToShoot(int targetX, int targetY) {
		int[] destination = findTarget(targetX, targetY);
		moveForwardByTile(destination[1]);
		turnRight();
		moveForwardByTile(destination[0]);
		turnTo(destination[2]);
		if (destination[2] % 90 > 0) {
			moveForwardByTile(0.5); // Minor correction for corner cases
		}
	}
}
