package by.astakhau.carsimulator;

import java.text.BreakIterator;

public class Brakes {
    short maxBrakesDiskTemperature;
    short actualBrakesDiskTemperature;

    double brakesDiskDiameter;
    double brakeForce;

    boolean applied;
    boolean absActive;
    boolean handbrakeOn;
    boolean breading;

    public Brakes(short maxBrakesDiskTemperature, double brakesDiskDiameter,
                  short actualBrakesDiskTemperature, double brakeForce) {
        this.maxBrakesDiskTemperature = maxBrakesDiskTemperature;
        this.brakesDiskDiameter = brakesDiskDiameter;
        this.actualBrakesDiskTemperature = actualBrakesDiskTemperature;
        this.brakeForce = brakeForce;

        applied = false;
        absActive = false;
        handbrakeOn = true;
        breading = actualBrakesDiskTemperature <= maxBrakesDiskTemperature;
    }

    public Brakes() {
        maxBrakesDiskTemperature = 138;
        brakesDiskDiameter = 262.6;
        actualBrakesDiskTemperature = 28;
    }

    public short getMaxBrakesDiskTemperature() {
        return maxBrakesDiskTemperature;
    }

    public void setMaxBrakesDiskTemperature(short maxBrakesDiskTemperature) {
        this.maxBrakesDiskTemperature = maxBrakesDiskTemperature;
    }

    public double getBrakesDiskDiameter() {
        return brakesDiskDiameter;
    }

    public void setBrakesDiskDiameter(short brakesDiskDiameter) {
        this.brakesDiskDiameter = brakesDiskDiameter;
    }

    public double getBrakeForce() {
        return brakeForce;
    }

    public void setBrakeForce(double brakeForce) {
        this.brakeForce = brakeForce;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    public boolean isAbsActive() {
        return absActive;
    }

    public void setAbsActive(boolean absActive) {
        this.absActive = absActive;
    }

    public boolean isHandbrakeOn() {
        return handbrakeOn;
    }

    public void setBrakesDiskDiameter(double brakesDiskDiameter) {
        this.brakesDiskDiameter = brakesDiskDiameter;
    }

    public short getActualBrakesDiskTemperature() {
        return actualBrakesDiskTemperature;
    }

    public void setActualBrakesDiskTemperature(short actualBrakesDiskTemperature) {
        this.actualBrakesDiskTemperature = actualBrakesDiskTemperature;
    }

    public boolean isBreading() {
        return breading;
    }

    public void setBreading(boolean breading) {
        this.breading = breading;
    }

    public void setHandbrakeOn(boolean handbrakeOn) {
        this.handbrakeOn = handbrakeOn;
        actualBrakesDiskTemperature += 40;
        if (actualBrakesDiskTemperature > maxBrakesDiskTemperature) {
            setBreading(true);
        }
    }
}
