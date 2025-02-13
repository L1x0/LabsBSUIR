package by.astakhau.carsimulator.model.wheels;

public class Wheel {
    WheelTypes orientation;

    double diameter;
    double width;

    int angle;

    public Wheel(WheelTypes orientation, double diameter, double width, int angle) {
        this.orientation = orientation;
        this.diameter = diameter;
        this.width = width;
        this.angle = 90;
    }

    public Wheel(WheelTypes orientation) {
        this.orientation = orientation;
        this.diameter = 16;
        this.width = 20.05;
        this.angle = 90;
    }

    public WheelTypes getOrientation() {
        return orientation;
    }

    public void setOrientation(WheelTypes orientation) {
        this.orientation = orientation;
    }

    public double getDiameter() {
        return diameter;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }
}
