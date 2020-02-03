/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;


import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.*;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;


public class Robot extends TimedRobot {
  private DifferentialDrive m_myRobot;
  private Joystick gamepad;

  
  private WPI_TalonSRX leftMotorControllerCIM1;
  private WPI_TalonSRX leftMotorControllerCIM2;
  private WPI_TalonSRX rightMotorControllerCIM1;
  private WPI_TalonSRX rightMotorControllerCIM2;

  private SpeedControllerGroup leftMotorGroup;
  private SpeedControllerGroup rightMotorGroup;  
  
  private WPI_TalonSRX conveyorMotorCIM1;
  private WPI_TalonSRX conveyorMotorCIM2;
  private SpeedControllerGroup conveyorMotorGroup;  

  private Talon leftMotor;
  private Talon rightMotor;
  private Talon conveyorMotor;

 
  @Override
  public void robotInit() {
   

  //Set up the Talons according to the spreadsheet here:  https://docs.google.com/spreadsheets/d/1-l5YZYubWAp52MwDntlmeQ8fC4OWeWa1os5C94XbTL8/edit?usp=sharing
      
  if(RobotBase.isReal()){
    leftMotorControllerCIM1 = new WPI_TalonSRX(0);
      leftMotorControllerCIM2 = new WPI_TalonSRX(1);
      leftMotorGroup = new SpeedControllerGroup(leftMotorControllerCIM1,leftMotorControllerCIM2);

      rightMotorControllerCIM1 = new WPI_TalonSRX(2);
      rightMotorControllerCIM2 = new WPI_TalonSRX(3);
      rightMotorGroup = new SpeedControllerGroup(rightMotorControllerCIM1,rightMotorControllerCIM2);

      
      leftMotorGroup.setInverted(true);
      conveyorMotorCIM1 = new WPI_TalonSRX(6);
      conveyorMotorCIM2 = new WPI_TalonSRX(7);
      conveyorMotorGroup = new SpeedControllerGroup(conveyorMotorCIM1,conveyorMotorCIM2);

    //Create a differential drive using the left motor group and right motor groups.
      m_myRobot = new DifferentialDrive(leftMotorGroup, rightMotorGroup);
      m_myRobot.setRightSideInverted(false);
  }
  //If not in a simulation, use regular Talons and no groupings
  else{
    leftMotor = new Talon(0);
    rightMotor = new Talon(2);
    conveyorMotor = new Talon(6);
    
  //Create a simulated differential drive using the left motor and right motors.
  m_myRobot = new DifferentialDrive(leftMotor, rightMotor);

  }
      
      
  gamepad = new Joystick(0);
 
  }

  @Override
  public void teleopPeriodic() {
    //right joystick currently the one being used
    //we could duplicate the input on the other side too

    //Set the drive motors according to the coordinates of the left joystick
    m_myRobot.arcadeDrive(gamepad.getY(),gamepad.getX());

    //If button 1 is pressed...
    if(RobotBase.isReal()){
      if(gamepad.getRawButton(1)){
        //Set the conveyor to full forward
        conveyorMotorGroup.set(1.0);
      }
      else{
        //...otherwise turn it off.
        conveyorMotorGroup.set(0.0);
      }

    }
    else{
      if(gamepad.getRawButton(1)){
        //Set the conveyor to full forward
        conveyorMotor.set(1.0);
      }
      else{
        //...otherwise turn it off.
        conveyorMotor.set(0.0);
      }

    }
    
    
    
  }
}
