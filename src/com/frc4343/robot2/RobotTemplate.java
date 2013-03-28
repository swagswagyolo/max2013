package com.frc4343.robot2;

import com.frc4343.robot2.Systems.DriveSystem;
import com.frc4343.robot2.Systems.FiringSystem;
import com.frc4343.robot2.Systems.GyroSystem;
import com.frc4343.robot2.Systems.JoystickSystem;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStationLCD.Line;
import edu.wpi.first.wpilibj.HiTechnicColorSensor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;

public class RobotTemplate extends IterativeRobot {

    // Various systems
    public FiringSystem firingSystem = new FiringSystem(this);
    public JoystickSystem joystickSystem = new JoystickSystem(this);
    public GyroSystem gyroSystem = new GyroSystem(this);
    public DriveSystem driveSystem = new DriveSystem(this);
    // Miscellaneous robot components/helpers
    Logger logger = new Logger();
    Piston climbingPiston = new Piston(Mappings.CLIMBING_PISTON_SOLENOID_ONE, Mappings.CLIMBING_PISTON_SOLENOID_TWO, Mappings.CLIMBING_PISTON_EXTENDED_BY_DEFAULT);
    Compressor compressor = new Compressor(1, 1);
    Timer climbTimer = new Timer();

    private void resetRobot() {
        compressor.start();
        // Reset the climber piston to its initial position.
        climbingPiston.extend();
        // Initialize and/or reset the various systems.
        firingSystem.switchMode();
        gyroSystem.switchMode();
        driveSystem.switchMode();
    }

    public void teleopInit() {
        resetRobot();

        climbTimer.reset();
        climbTimer.start();
    }

    public void autonomousInit() {
        resetRobot();
    }

    public void testInit() {
        resetRobot();
    }

    public void teleopPeriodic() {
        firingSystem.run();
        gyroSystem.run();
        driveSystem.run();

        climbingHandler();

        // Print the debug output the the DriverStation console.
        printConsoleOutput();
    }

    public void autonomousPeriodic() {
        firingSystem.run();
        gyroSystem.run();
        driveSystem.run();

        // Print the debug output the the DriverStation console.
        printConsoleOutput();
    }

    public void testPeriodic() {
        firingSystem.run();
        gyroSystem.run();
        driveSystem.run();

        climbingHandler();

        // Print the debug output the the DriverStation console.
        printConsoleOutput();
    }

    private void climbingHandler() {
        if (joystickSystem.getJoystick(1).getRawButton(Mappings.EXTEND_CLIMBING_PISTONS)) {
            climbingPiston.extend();
        } else if (joystickSystem.getJoystick(1).getRawButton(Mappings.RETRACT_CLIMBING_PISTONS)) {
            climbingPiston.retract();
        } else if (climbTimer.get() >= Mappings.AUTO_CLIMB_TIME) {
            climbingPiston.retract();
        }
    }

    private void printConsoleOutput() {
        // Clears driverStation text.
        logger.clearWindow();
        // Prints State of Launcher Motor
        logger.printLine(Line.kUser1, "Indexer Motor: " + (firingSystem.getIndexerMotorState() ? "ON" : "OFF"));
        // Print the speed of the launcher motor.
        logger.printLine(Line.kUser2, "Launcher Speed: " + (byte) (firingSystem.getLauncherSpeed() * 100) + "%");
        // Prints the state of various systems
        logger.printLine(Line.kUser3, "FSS: " + firingSystem.getState());
        logger.printLine(Line.kUser4, "GSS: " + gyroSystem.getState());
        logger.printLine(Line.kUser5, "DSS: " + driveSystem.getState());
        // Updates the output window.
        logger.updateLCD();
        // Prints the current gyro angle.
        //logger.printLine(Line.kUser1, "Gyro value: " + (gyroSystem.gyro.getAngle()));
        //logger.printLine(Line.kUser2, "RPM: " + firingSystem.getRPM());
        // Prints State of Launcher Motor
        //logger.printLine(Line.kUser2, "Launcher Motor: " + (firingSystem.getLauncherMotorState() ? "ON" : "OFF"));
        // Print the tank pressurization state.
        //logger.printLine(Line.kUser5, "Tanks Full: " + (compressor.getPressureSwitchValue() ? "YES" : "NO"));
    }
}
