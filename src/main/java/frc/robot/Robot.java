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
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;


public class Robot extends TimedRobot {
  private DifferentialDrive m_myRobot;
  //Define controllers. 
  private XboxController gamepadDrive;
  private XboxController gamepadOperator;

  
  private WPI_TalonSRX leftMotorControllerCIM1;
  private WPI_TalonSRX leftMotorControllerCIM2;
  private WPI_TalonSRX rightMotorControllerCIM1;
  private WPI_TalonSRX rightMotorControllerCIM2;

  private SpeedControllerGroup leftMotorGroup;
  private SpeedControllerGroup rightMotorGroup;  
  
  private WPI_VictorSPX conveyorMotorCIM1;
  private WPI_VictorSPX conveyorMotorCIM2;
  private SpeedControllerGroup conveyorMotorGroup;  

  private DigitalInput colorWheelArmLowerLimit;
  private DigitalInput colorWheelArmUpperLimit;
  private WPI_VictorSPX colorWheelDrive;
  private WPI_VictorSPX colorWheelArm;


  //private ColorWheelSystem colorWheelSystem;

  @Override
  public void robotInit() {
   

  //Set up the motor controllers according to the spreadsheet here:  https://docs.google.com/spreadsheets/d/1-l5YZYubWAp52MwDntlmeQ8fC4OWeWa1os5C94XbTL8/edit?usp=sharing
      leftMotorControllerCIM1 = new WPI_TalonSRX(0);
      leftMotorControllerCIM2 = new WPI_TalonSRX(1);
      leftMotorGroup = new SpeedControllerGroup(leftMotorControllerCIM1,leftMotorControllerCIM2);

      rightMotorControllerCIM1 = new WPI_TalonSRX(2);
      rightMotorControllerCIM2 = new WPI_TalonSRX(3);
      rightMotorGroup = new SpeedControllerGroup(rightMotorControllerCIM1,rightMotorControllerCIM2);

      
      leftMotorGroup.setInverted(true);

      conveyorMotorCIM1 = new WPI_VictorSPX(6);
      conveyorMotorCIM2 = new WPI_VictorSPX(7);
      conveyorMotorGroup = new SpeedControllerGroup(conveyorMotorCIM1,conveyorMotorCIM2);

      colorWheelDrive = new WPI_VictorSPX(8);
      colorWheelArm = new WPI_VictorSPX(9);

    //Create a differential drive using the left motor group and right motor groups.
      m_myRobot = new DifferentialDrive(leftMotorGroup, rightMotorGroup);
      m_myRobot.setRightSideInverted(false);
      gamepadDrive = new XboxController(0);
      gamepadOperator = new XboxController(1);
 

      colorWheelArmLowerLimit = new DigitalInput(0);
      colorWheelArmUpperLimit = new DigitalInput(1);
  }

  @Override
  public void teleopPeriodic() {
  
  //**********DRIVE CONTROL**********//
  //Set the drive motors according to the coordinates of the right joystick on the drive controller
    
    double rightY = gamepadDrive.getY(Hand.kRight);
    double rightX = gamepadDrive.getX(Hand.kRight)*-1;
    

    m_myRobot.arcadeDrive(rightY,rightX);


//**********SMART DASHBOARD CONTROL**********//
//This line sends things to the smart dashboard. We can use this to get any information we might want from the system.
    SmartDashboard.putNumber("leftMotor", leftMotorControllerCIM1.get());
    SmartDashboard.putNumber("rightMotor", rightMotorControllerCIM1.get());
    SmartDashboard.putNumber("conveyorMotor", conveyorMotorCIM1.get());
    SmartDashboard.putNumber("colorWheelArm", colorWheelArm.get());
    SmartDashboard.putNumber("colorWheelDrive", colorWheelDrive.get());
    SmartDashboard.putBoolean("colorWheelArmUpperLimit", colorWheelArmLowerLimit.get());
    SmartDashboard.putBoolean("colorWheelArmLowerLimit", colorWheelArmUpperLimit.get());


//**********CONVEYOR CONTROL**********//

//left button is full intake, bottom button is full stop, right button is full dump
//If button X is pressed on the operator control...
    if(gamepadOperator.getXButton()){
      //Set the conveyor to full forward
      conveyorMotorGroup.set(1.0);
    } 
    else
    //if button B is pressed
    if(gamepadOperator.getBButton()){
      //Set the conveyor to full backward
      conveyorMotorGroup.set(-1.0);
    }
    else{
      //...otherwise turn it off.
      conveyorMotorGroup.set(0.0);
    }
    


//**********COLOR WHEEL ROTATION CONTROL**********//
//If top right bumper button is pressed, turn the color wheel drive motor
    if (gamepadOperator.getRawButton(6)){
      colorWheelDrive.set(-1);
    }
    else{
      //...otherwise turn it off.
      colorWheelDrive.set(0.0);
    }



//**********COLOR WHEEL ARM CONTROL**********//
//top left bumper button arms the color wheel mechanism 
//bottom left trigger button retracts the color wheel mechanism (use limit switches to control)

    //if top left bumper button is pressed and the upper limit switch is not pressed, raise the color wheel arm
    if (gamepadOperator.getBumper(Hand.kLeft)){
      //Check if colorWheelArmUpperLimit switch is not pressed before running motor
      if(!colorWheelArmUpperLimit.get()) {
        colorWheelArm.set(.5);
    } else {
      colorWheelArm.set(0);
      }
    }

    if (gamepadOperator.getRawButton(2)){
      //Check if colorWheelArmLowerLimit switch is not pressed before running motor
      if(!colorWheelArmLowerLimit.get()) {
        colorWheelArm.set(-.5);
    } else {
      colorWheelArm.set(0);
      }
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



///second operator is counting the cells to make sure there aren't more than 5

//top right bumper button rotates color wheel between 3 - 5 times
//bottom right trigger button sets color to value from FMS

//dashboard shows sequence complete to say that stage 1 color wheel is complete
//read color output from FMS
//D pad for hanging deploy sequence
//safety button for deploy

//use rumble feature to show that we are in position against the trench



