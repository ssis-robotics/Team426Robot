/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;


import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;



public class Robot extends TimedRobot {
  private DifferentialDrive m_myRobot;
  private XboxController gamepad;

  
  private WPI_TalonSRX leftMotorControllerCIM1;
  private WPI_TalonSRX leftMotorControllerCIM2;
  private WPI_TalonSRX rightMotorControllerCIM1;
  private WPI_TalonSRX rightMotorControllerCIM2;

  private SpeedControllerGroup leftMotorGroup;
  private SpeedControllerGroup rightMotorGroup;  
  
  private WPI_TalonSRX conveyorMotorCIM1;
  private WPI_TalonSRX conveyorMotorCIM2;
  private SpeedControllerGroup conveyorMotorGroup;  


  @Override
  public void robotInit() {
   

  //Set up the Talons according to the spreadsheet here:  https://docs.google.com/spreadsheets/d/1-l5YZYubWAp52MwDntlmeQ8fC4OWeWa1os5C94XbTL8/edit?usp=sharing
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
      gamepad = new XboxController(0);
 
  }

  @Override
  public void teleopPeriodic() {
  

    //Set the drive motors according to the coordinates of the left joystick
    double leftY = gamepad.getY(Hand.kLeft);
    double leftX = gamepad.getX(Hand.kLeft);
    double rightY = gamepad.getY(Hand.kRight);
    double rightX = gamepad.getX(Hand.kRight);
    

    m_myRobot.arcadeDrive(rightY,rightX);
    SmartDashboard.putNumber("leftMotor", leftMotorControllerCIM1.get());
    SmartDashboard.putNumber("rightMotor", rightMotorControllerCIM1.get());
    SmartDashboard.putNumber("conveyorMotor", conveyorMotorCIM1.get());
    //If button X is pressed...
    if(gamepad.getXButton()){
      //Set the conveyor to full forward
      conveyorMotorGroup.set(1.0);
    }
    else{
      //...otherwise turn it off.
      conveyorMotorGroup.set(0.0);
    }
    //if button B is pressed
    if(gamepad.getBButton()){
      //Set the conveyor to full backward
      conveyorMotorGroup.set(-1.0);
    }
    else{
      //...otherwise turn it off.
      conveyorMotorGroup.set(0.0);
    }
    
  }
  public double abs(double number){
    if(number > 0){
      return number;
    }
    else{
      return -number;
    }
  }
}
//on first gamepad:
//left stick is dumper forward, right stick is intake forward
//right joystick currently the one being used 
//

//on second gamepad

//left button is full intake, bottom button is full stop, right button is full dump
///second operator is counting the cells to make sure there aren't more than 5

//top left bumper button arms the color wheel mechanism 
//bottom left bumper button retracts the color wheel mechanism (use limit switches to control)

//top right bumper button rotates color wheel between 3 - 5 times
//bottom right bumper button sets color to value from FMS
//dashboard shows sequence complete to say that stage 1 color wheel is complete
//read color output from FMS
//D pad for hanging deploy sequence
//safety button for deploy

//use rumble feature to show that we are in position against the trench



