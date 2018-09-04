/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team3617.robot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
//import edu.wpi.first.wpilibj.hal.AllianceStationID;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;


//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
@SuppressWarnings({ "deprecation" })
public class Robot extends IterativeRobot {
	
	DifferentialDrive difDrive;
	RobotDrive robotDrive;
	Timer timer;
	
	Joystick driveStick;
	Joystick shootStick;
		JoystickButton clawGrabButton;
		JoystickButton clawWheelForwardButton;
		JoystickButton clawWheelBackwardButton;
		JoystickButton clawHingeButton;
		
	XboxController controller;
	
	Spark frontLeft, frontRight, backLeft, backRight;
	
	Victor clawWheel_L, clawWheel_R, clawHinge;
	boolean clawIsUp;
	Solenoid clawPistons;
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//AS SOON AS THE ROBOT TURNS ON:
	@Override
	public void robotInit() {
		timer = new Timer();
		
		//DRIVE TRAIN:
		frontLeft = new Spark(6);
		frontRight = new Spark(7);
		backLeft = new Spark(8);
		backRight = new Spark(9);
		
		robotDrive = new RobotDrive(frontLeft,frontRight,backLeft,backRight);
		
		SpeedControllerGroup leftDrive = new SpeedControllerGroup(frontLeft,backLeft);
		SpeedControllerGroup rightDrive = new SpeedControllerGroup(frontRight,backRight);
		
		difDrive = new DifferentialDrive(leftDrive, rightDrive);

		//CONTROLE SYSTEM:
		driveStick = new Joystick(0);
		shootStick = new Joystick(1);
			clawGrabButton = new JoystickButton(shootStick,1);
			clawWheelForwardButton = new JoystickButton(shootStick,2);
			clawWheelBackwardButton = new JoystickButton(shootStick,3);
			clawHingeButton = new JoystickButton(shootStick,4);
			
		controller = new XboxController(0);
			//A Button - claw grad / release
			//B Button - claw hinge
			//X Button - claw wheel forward
			//Y Button - claw wheel backward
			//L. Triger - shoot cube
		
		//CLAW:
		clawWheel_L = new Victor(1);
		clawWheel_R = new Victor(2);
		
		clawHinge = new Victor(3);
		clawIsUp = true;
		
		clawPistons = new Solenoid(1);
	}
	public void robotPeriodic() {}
	
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//DISABLED:
	public void disabledInit() {}
	public void disabledPeriodic() {}

	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//AUTONOMOUS:
	@Override
	public void autonomousInit() {
		timer.reset();
		timer.start();
	}
	@Override
	public void autonomousPeriodic() {
		
	}

	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//TELEOPORATED: 
	@Override
	public void teleopInit() {}
	@SuppressWarnings("static-access")
	@Override
	public void teleopPeriodic() {
		robotDrive.arcadeDrive(driveStick);
		robotDrive.arcadeDrive(controller);
		
			//if the 'claw hinge' button is pushed
		if (clawHingeButton.get() == true || controller.getBButton() == true) {
				//if the claw is up, move it down
			if (clawIsUp) {clawHinge.set(0.5); clawIsUp = false;}
				//otherwise move the claw up
			else {clawHinge.set(-0.5); clawIsUp = true;}
		}
		
			//if the 'grab' button is being pushed and the claw is not up
		if ((clawGrabButton.get() == true || controller.getAButton() == true) && !clawIsUp) {
				//and the pistons are not already out
			if (clawPistons.get() == false) {
					//extend the pistons
				clawPistons.set(true);
			}
				//otherwise, the pistons ARE already out
			else {
					//so bring them back in
				clawPistons.set(false);
			}
		}
		
			//if the 'wheel forward' button is being pushed
		if (clawWheelForwardButton.get() == true || controller.getXButton() == true) {
				//then spin the wheels forward
			clawWheel_L.set(0.5);
			clawWheel_R.set(-0.5);
		}
			//else if the 'wheel backward' button is being pushed
		else if (clawWheelBackwardButton.get() == true || controller.getYButton() == true) {
				//then spin the wheels backward
			clawWheel_L.set(-0.5);
			clawWheel_R.set(0.5);
		}
			//else if the 'trigger' is pressed and the claw is up
		else if ((shootStick.getTrigger() == true || controller.getBumper(Hand.kLeft) == true) && clawIsUp) {
				//then spin the claw wheels forward 100% to shoot cube
			clawWheel_L.set(1.0);
			clawWheel_R.set(-1.0);
				//spin for 1/2 second
			timer.delay(0.5);
		}
			//otherwise the wheels should be stopped.
		else {
			clawWheel_L.stopMotor();
			clawWheel_R.stopMotor();
		}
		
	}


	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//TEST
	@Override
	public void testPeriodic() {
		LiveWindow.run();
		
		//DIFFERENTIAL DRIVE:
			//currently set to half speed:
		difDrive.arcadeDrive(0.5, 0.0); 
		
		//TANK DRIVE:
		//robotDrive.tankDrive(driveStick,shootStick);
		//robotDrive.tankDrive(controller.getX(Hand.kLeft), controller.getY(Hand.kLeft));
		
		//MECHANUM DRIVE (for omni wheels):
		//robotDrive.mecanumDrive_Cartesian(driveStick.getX(), driveStick.getY(), driveStick.getTwist(), 0);
		//robotDrive.mecanumDrive_Cartesian(controller.getX(Hand.kLeft), controller.getY(Hand.kLeft), controller.getX(Hand.kRight), 0);
	}
}
