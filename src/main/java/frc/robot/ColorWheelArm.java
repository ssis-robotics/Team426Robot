package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;


import edu.wpi.first.wpilibj.command.Subsystem;





public class ColorWheelArm extends Subsystem{
  private WPI_TalonSRX m_armMotor = new WPI_TalonSRX(9);
  private WPI_TalonSRX m_rotationMotor = new WPI_TalonSRX(8);
  
  private boolean isRaisingArm = false;
  private boolean isLoweringArm = false;
  private boolean isIdle = true;
 
  
  public void spinColorWheel() {
    m_armMotor.set(1.0);
  }
  public void stopColorWheel() {
    m_armMotor.set(0.0);
  }
  public void reverseColorWheel() {
    m_armMotor.set(-1.0);
  }
  public void raiseColorWheelArm(){
      isRaisingArm = true;
      isLoweringArm = false;
  }

  public void lowerColorWheelArm(){
    isLoweringArm = true;
    isRaisingArm = false;
1}


  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    if(isIdle){
        m_armMotor.set(0);
    }
        
  }
}