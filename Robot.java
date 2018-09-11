/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team3617.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;


public class Robot extends IterativeRobot {

	//CONTROLL SYSTEM
	Joystick driveStick, shootStick;
	JoystickButton clawHingeButt, clawGrabButt, clawWheelForwardButt, clawWheelBackwardButt;
	
	//DRIVE TRAIN
	Spark driveFrontLeft, driveFrontRight, driveBackLeft, driveBackRight;
	SpeedControllerGroup driveLeft, driveRight;
	DifferentialDrive drive;
	
	//CLAW
	Victor clawHingeMotor, clawWheelLeft, clawWheelRight;
	Solenoid clawGrabPiston;
	boolean isClawUp;
	
	
	@Override
	public void robotInit() {
		
		//CONTROLL SYSTEM
		driveStick = new Joystick(0);
		shootStick = new Joystick(1);
			clawHingeButt = new JoystickButton(shootStick, 0);
			clawGrabButt = new JoystickButton(shootStick, 1);
			clawWheelForwardButt = new JoystickButton(shootStick, 2);
			clawWheelBackwardButt = new JoystickButton(shootStick, 3);
			
		//DRIVE TRAIN
		driveFrontLeft = new Spark(6);
		driveFrontRight = new Spark(7);
		driveBackLeft = new Spark(8);
		driveBackRight = new Spark(9);
		
		driveLeft = new SpeedControllerGroup(driveFrontLeft,driveBackLeft);
		driveRight = new SpeedControllerGroup(driveFrontRight,driveBackRight);
		
		drive = new DifferentialDrive(driveLeft,driveRight);
		
		//CLAW
		clawHingeMotor = new Victor(1);
		clawWheelLeft = new Victor(2);
		clawWheelRight = new Victor(3);
		
		clawGrabPiston = new Solenoid(4);
		
		isClawUp = true;
	}
	
	
	//AUTON
	@Override
	public void autonomousInit() {
		
	}

	@Override
	public void autonomousPeriodic() {
		drive.arcadeDrive(0.5, 0.0);
	}

	
	//TELEOP
	@Override
	public void teleopInit() {
		
	}
	
	@Override
	public void teleopPeriodic() {
		drive.arcadeDrive(driveStick.getY(), driveStick.getX());
		
		if(clawHingeButt.get()) {doClawHinge();}
		if(clawGrabButt.get()) {doClawGrab();}
		
		if(clawWheelForwardButt.get()) {doClawWheelForward();}
		else if(clawWheelBackwardButt.get()) {doClawWheelBackward();}
		else if(shootStick.getTrigger()) {doClawShoot();}
		else {doClawWheelsStop();}
	}

	
	//TEST
	@Override
	public void testInit() {
		
	}
	
	@Override
	public void testPeriodic() {
		drive.tankDrive(driveStick.getY(), shootStick.getX());
	}
	
	
	//CLAW METHODS
	private void doClawHinge() {
		if (isClawUp) {
			clawHingeMotor.set(0.5);
		}
		else {
			clawHingeMotor.set(-0.5);
		}
	}
	
	private void doClawGrab() {
		if (clawGrabPiston.get() && !isClawUp) { //If the piston is out and claw is not up
			clawGrabPiston.set(false); //Pull piston in
		}
		else if (!clawGrabPiston.get() && !isClawUp) { //If the piston is in and claw is not up
			clawGrabPiston.set(true); //Push piston out
		}
	}
	
	private void doClawWheelForward() {
		clawWheelLeft.set(0.5);
		clawWheelRight.set(-0.5);
	}
	
	private void doClawWheelBackward() {
		clawWheelLeft.set(-0.5);
		clawWheelRight.set(0.5);
	}
	
	private void doClawWheelsStop() {
		clawWheelLeft.stopMotor();
		clawWheelRight.stopMotor();
	}
	
	private void doClawShoot() {
		if (isClawUp) {
			clawWheelLeft.set(1.0);
			clawWheelRight.set(-1.0);
		}
	}
	
}
