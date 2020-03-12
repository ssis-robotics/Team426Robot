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
import edu.wpi.first.wpilibj.*;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonSRXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.I2C;

import com.revrobotics.*;


public class Robot extends TimedRobot {
  private DifferentialDrive m_myRobot;

  private final Timer m_timer = new Timer();

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

  private WPI_TalonSRX climbMotorCIM1;
  private WPI_TalonSRX climbMotorCIM2;
  private SpeedControllerGroup climbMotorGroup;

  private DigitalInput colorWheelArmLowerLimit;
  private DigitalInput colorWheelArmUpperLimit;
  private WPI_VictorSPX colorWheelDrive;
  private WPI_VictorSPX colorWheelArm;

  private int moveColorWheelUpDown = 1;
  private int colorWheelState = 1;
  private Boolean lastPressed = true;
  private Boolean operatorGamepadLeftTriggerPressed = false;
  private String colorWheelPosition = "DOWN";
  
  //variable for arming the climb system
  private Boolean climbMotorEnabled = false;

  //Add color sensor through the onboard I2C port
  private final I2C.Port i2cPort = I2C.Port.kOnboard;
  private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);
  private final ColorMatch m_colorMatcher = new ColorMatch();

  //Set up variables for the color sensor
  private Color detectedColor = m_colorSensor.getColor();
  private String colorString = "Unknown";
  private ColorMatchResult colorMatch = m_colorMatcher.matchClosestColor(detectedColor);
  private String lastColorString = "";
  private int numberOfColorChanges = 0;

  //These colors have been updated with tests from March 1 in NYC
  //Each color has a decimal value for red, blue, and green in the parentheses.
  
  private final Color kBlue = ColorMatch.makeColor(0.131,0.457,0.412);
  private final Color kGreen = ColorMatch.makeColor(0.169,0.594,0.238);
  private final Color kRed = ColorMatch.makeColor(0.542,0.347,0.112);
  private final Color kYellow = ColorMatch.makeColor(0.323,0.565,0.113);

  private int kColorChangesForStageTwo = 32;
  private boolean stageTwoComplete = false;

  //private ColorWheelSystem colorWheelSystem;

  //Robot navigation 
  private int leftEncoderReading = 159;
  private int rightEncoderReading = 314;
  private PigeonIMU pigeonIMU;
  private double [] pigeonIMUData;

  private double robotHeading;
  
  //Pixy variables
  //private Pixy2 pixy;

  @Override
  public void robotInit() {

//The system inputs/outputs are arranged according to the spreadsheet here:  https://docs.google.com/spreadsheets/d/1-l5YZYubWAp52MwDntlmeQ8fC4OWeWa1os5C94XbTL8/edit?usp=sharing
//Set up the drive motor controllers
      leftMotorControllerCIM1 = new WPI_TalonSRX(0);
      leftMotorControllerCIM2 = new WPI_TalonSRX(1);
      
      leftMotorGroup = new SpeedControllerGroup(leftMotorControllerCIM1,leftMotorControllerCIM2);

      rightMotorControllerCIM1 = new WPI_TalonSRX(2);
      rightMotorControllerCIM2 = new WPI_TalonSRX(3);
      rightMotorGroup = new SpeedControllerGroup(rightMotorControllerCIM1,rightMotorControllerCIM2);

//Create a differential drive system using the left and right motor groups
      m_myRobot = new DifferentialDrive(leftMotorGroup, rightMotorGroup);
      

//Set up the two Xbox controllers. The drive is for driving, the operator is for all conveyor and color wheel controls
      gamepadDrive = new XboxController(0);
      gamepadOperator = new XboxController(1);

     
//Set up conveyor motor controllers
      conveyorMotorCIM1 = new WPI_VictorSPX(6);
      conveyorMotorCIM2 = new WPI_VictorSPX(7);
     // conveyorMotorGroup = new SpeedControllerGroup(conveyorMotorCIM1,conveyorMotorCIM2);

//Set up climb motor controllers
      climbMotorCIM1 = new WPI_TalonSRX(10);
      climbMotorCIM2 = new WPI_TalonSRX(11);
      climbMotorGroup = new SpeedControllerGroup(climbMotorCIM1,climbMotorCIM2);

//Set up the color wheel system motor controllers
      colorWheelDrive = new WPI_VictorSPX(8);
      colorWheelArm = new WPI_VictorSPX(9);

//Set up the color wheel arm limit switches
      colorWheelArmLowerLimit = new DigitalInput(5);
      colorWheelArmUpperLimit = new DigitalInput(4);

//Set up the color matching in the program:
      m_colorMatcher.addColorMatch(kBlue);
      m_colorMatcher.addColorMatch(kGreen);
      m_colorMatcher.addColorMatch(kRed);
      m_colorMatcher.addColorMatch(kYellow);


//Set up the color wheel system motor controllers
      colorWheelDrive = new WPI_VictorSPX(8);
      colorWheelArm = new WPI_VictorSPX(9);


//Set up encoders on the left and right sides of the drive
//
      leftMotorControllerCIM2.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder); 
      leftMotorControllerCIM2.setSensorPhase(true);
      leftMotorControllerCIM2.setSelectedSensorPosition(0);
      rightMotorControllerCIM1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder); 
      rightMotorControllerCIM1.setSelectedSensorPosition(0);

//Set up the Pigeon
      pigeonIMU = new PigeonIMU(leftMotorControllerCIM1);
      pigeonIMUData = new double[3];
      pigeonIMU.setFusedHeading(70);
    
//Set up the Pixy
/*
    pixy = Pixy2.createInstance(new SPILink()); // Creates a new Pixy2 camera using SPILink
		pixy.init(); // Initializes the camera and prepares to send/receive data
		pixy.setLamp((byte) 1, (byte) 1); // Turns the LEDs on
		pixy.setLED(200, 30, 255); // Sets the RGB LED to purple
   */   
  }

  @Override
  public void teleopPeriodic() {

  //**********DRIVE CONTROL**********//
  //Set the drive motors according to the coordinates of the right joystick on the drive controller


    double leftY = gamepadDrive.getY(Hand.kLeft)*-1.0;

    double rightX = gamepadDrive.getX(Hand.kRight)*0.7;

    m_myRobot.arcadeDrive(leftY,rightX);



//**********SMART DASHBOARD CONTROL**********//
//This line sends things to the smart dashboard. We can use this to get any information we might want from the system.
    SmartDashboard.putNumber("leftMotor", leftMotorControllerCIM1.get());
    SmartDashboard.putNumber("rightMotor", rightMotorControllerCIM1.get());
    SmartDashboard.putNumber("conveyorMotor", conveyorMotorCIM1.get());
    SmartDashboard.putNumber("climbMotor", climbMotorCIM1.get());
    SmartDashboard.putBoolean("climbMotorEnabled",climbMotorEnabled);

    //Show color wheel information
    SmartDashboard.putNumber("colorWheelArm", colorWheelArm.get());
    SmartDashboard.putNumber("colorWheelDrive", colorWheelDrive.get());
    SmartDashboard.putBoolean("colorWheelArmUpperLimit", colorWheelArmLowerLimit.get());
    SmartDashboard.putBoolean("colorWheelArmLowerLimit", colorWheelArmUpperLimit.get());
    SmartDashboard.putString("colorWheelPosition", colorWheelPosition);
    SmartDashboard.putBoolean("stageTwoComplete", stageTwoComplete);
    SmartDashboard.putString("colorWheelTargetColor", colorWheelTargetColor);

    //Show color sensor information
    SmartDashboard.putNumber("Red",detectedColor.red);
    SmartDashboard.putNumber("Green",detectedColor.green);
    SmartDashboard.putNumber("Blue",detectedColor.blue);
    SmartDashboard.putNumber("Confidence",colorMatch.confidence);
    SmartDashboard.putString("Detected Color",colorString);
    SmartDashboard.putNumber("numberOfColorChanges", numberOfColorChanges);

    //Show robot position data from encoders and Pigeon IMU
    SmartDashboard.putNumber("leftEncoder",leftEncoderReading);
    SmartDashboard.putNumber("rightEncoder",rightEncoderReading);
    //Nam - your code goes in the next line. Talk to Leo if you need help.
    SmartDashboard.putNumber("Robot Heading",robotHeading);
//**********CONVEYOR CONTROL**********//

//left button is full intake, bottom button is full stop, right button is full dump
//If button X is pressed on the operator control...
    if(gamepadOperator.getXButton()){
      //Set the conveyor to full forward
      conveyorMotorCIM1.set(0.75);
    }
    else
    //if button B is pressed
    if(gamepadOperator.getBButton()){
      //Set the conveyor to full backward

      conveyorMotorCIM1.set(-0.75);

    }
    else{
      conveyorMotorCIM1.set(0.0);
      

    }

//left button is full intake, bottom button is full stop, right button is full dump
//If button X is pressed on the operator control...
if(gamepadOperator.getPOV() == 180){
  //Set the conveyor to dump
  conveyorMotorCIM2.set(0.5);
}
else
//if button B is pressed
if(gamepadOperator.getPOV() == 0){
  //Set the conveyor to full backward

  conveyorMotorCIM2.set(-0.2);

}
else{
  conveyorMotorCIM2.set(0.0);
  

}

//**********COLOR WHEEL ROTATION CONTROL**********//
//If top right bumper button is pressed, turn the color wheel drive motor
    if (gamepadOperator.getBumper(Hand.kRight)){
      //Check that the number of color changes is enough to achieve rotation control
      if (numberOfColorChanges > kColorChangesForStageTwo){
        stageTwoComplete = true;
        colorWheelDrive.set(0.0);

      }
      else{
        stageTwoComplete = false;
        colorWheelDrive.set(-1);
      }
    }
    else{
      //...otherwise turn it off.
      colorWheelDrive.set(0.0);
    }



//**********COLOR WHEEL ARM CONTROL**********//
//top left bumper button arms the color wheel mechanism
//bottom left trigger button retracts the color wheel mechanism (use limit switches to control)

    //moveColorWheelUpDown == 1: move color wheel manipulator down
    //moveColorWheelUpDown == 2: move color wheel manipulator up
    //moveColorWheelUpDown == 0: color wheel is stationary in either the up or down position.

    //if top left bumper button is pressed and the upper limit switch is not pressed, raise the color wheel arm
  

    //Set the left trigger on the operator gamepad to act like the left bumper using a boolean variable.
    if (gamepadOperator.getRawAxis(2)>0.5){
     operatorGamepadLeftTriggerPressed = true;
    } else{
      operatorGamepadLeftTriggerPressed = false;
    }

    //Now set the color wheel arm state using either the left bumper or the left trigger.
    if (operatorGamepadLeftTriggerPressed){
      moveColorWheelUpDown = 1;
    } else if (gamepadOperator.getBumperPressed(Hand.kLeft)){
      moveColorWheelUpDown = 2;
    }

    //evaluate and react to the state of color wheel
    switch(moveColorWheelUpDown){

      //Case 0 is the idle state of the color wheel arm. The motor power is set to zero.

      case 0:
        colorWheelArm.set(0);
        break;


      //Case 1 is that the color wheel should be moving into the down position.
      case 1:

      if(!colorWheelArmLowerLimit.get()){
        //If the lower limit switch is hit, it means the arm has arrived at the down position.
        colorWheelPosition = "DOWN";

        //Set the state of the arm to be idle. 
        moveColorWheelUpDown = 0; 
      }
      else{
        //If the limit switch has not been hit, but moveColorWheelUpDown is 1, it means the arm is moving down.
        colorWheelPosition = "MOVING DOWN";
        colorWheelArm.set(-1);

      }
      break;

      //Case 2 is that the color wheel is to be moving into the up position.
      case 2:
        if(!colorWheelArmUpperLimit.get()){
          //If the upper limit switch is hit, it means the arm has arrived at the up position.
          colorWheelPosition = "UP";

          //Set the state of the arm to be idle. 
          moveColorWheelUpDown = 0; 
        }
        else{
          //If the limit switch has not been hit, but moveColorWheelUpDown is 2, it means the arm is still moving up.
          colorWheelPosition = "MOVING UP";
          colorWheelArm.set(1);

        }
        break;
      


    }


  //**********COLOR SENSOR **********//

  //Get the raw readings from the color sensor
  detectedColor = m_colorSensor.getColor();

  //Try to match the color sensor readings to the color readings we set above.

  colorMatch = m_colorMatcher.matchClosestColor(detectedColor);

  //Based on the color match, set the colorString to the corresponding value
  if(colorMatch.color == kBlue){
    colorString = "Blue";
  }
  else if(colorMatch.color == kGreen){
    colorString = "Green";
  }
  else if(colorMatch.color == kRed){
    colorString = "Red";
  }
  else if(colorMatch.color == kYellow){
    colorString = "Yellow";
  }
  else{
    colorString = "Unknown";
  }

  //If the newly detected color is different from the last color detected, add one to the number of changes
  if(colorString != lastColorString){
    numberOfColorChanges += 1;
  }
  //Set the lastColorString equal to the current one so that we can check for changes on the next loop
  lastColorString = colorString;

  //Set up the start button to reset the counter for color changes of the wheel.
  if(gamepadOperator.getStartButtonPressed()){
    //We want to reset both the number and stageTwoComplete because we probably didn't get the rotation count right for stage two. 
    numberOfColorChanges = 0;
    stageTwoComplete = false;
  }

  //**********CLIMB MOTOR CONTROL**********//

//If the drive gamepad A button is pressed, this arms or disarms the climb mechanism.

if (gamepadDrive.getAButtonPressed()){
  if(climbMotorEnabled){
    climbMotorEnabled = false;
  }
  else{
    climbMotorEnabled = true;
  }
}

//These functions will only be active if the climbMotorEnabled variable is true:
if(climbMotorEnabled){
  //If operator A button is pressed, set the climbMotorGroup to be on forward
  if (gamepadOperator.getAButton()){
    climbMotorGroup.set(1.0);
  }
  
  //If operator AYbutton is pressed, set the climbMotorGroup to be on backward
  else if(gamepadOperator.getYButton()){
    climbMotorGroup.set(-1.0);
  }
  else{
    //...otherwise turn it off.
    climbMotorGroup.set(0.0);
  }
}

 //**********ROBOT NAVIGATION DATA**********//
  leftEncoderReading = leftMotorControllerCIM2.getSelectedSensorPosition();
  rightEncoderReading = rightMotorControllerCIM1.getSelectedSensorPosition();
  pigeonIMU.getYawPitchRoll(pigeonIMUData);

  robotHeading = pigeonIMU.getFusedHeading();  

} //End of robotPeriodicTeleop


   /**
   * This function is run once each time the robot enters autonomous mode.
   */
  @Override
  public void autonomousInit() {
    m_timer.reset();
    m_timer.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    // Drive for 2 seconds
    if (m_timer.get() < 2.0) {
      m_myRobot.arcadeDrive(0.5, 0.0); // drive forwards half speed
    } else {
      m_myRobot.stopMotor(); // stop robot
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
private string color90Degrees(string colorOfficial) {
	if (colorOfficial == "R") {
		return "Blue"
	}
	else if (colorOfficial == "G") {
		return "Yellow"
	}
	else if (colorOfficial == "Y") {
		return "Green"
	}
	else {
		return "Red"
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
