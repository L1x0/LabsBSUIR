package by.astakhau.carsimulator.model.wheels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
public class Wheel {
    private WheelTypes orientation;

    private double diameter;
    private double width;

    private int angle;

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
}
