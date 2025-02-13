package by.astakhau.carsimulator.model;

import by.astakhau.carsimulator.model.wheels.Wheel;
import by.astakhau.carsimulator.model.wheels.WheelTypes;

import java.util.ArrayList;

public class Transmission {
    ArrayList<Wheel> frontWheels;

    Engine engine;

    int actualGear;
    int maxGear;

    boolean isBreading;


    public Transmission(Wheel firstWheel, Wheel secondWheel, Wheel thirdWheel,
                        Wheel fouthWheel, Engine engine, int actualGear, int maxGear) {

        frontWheels = new ArrayList<>();

        frontWheels.add(firstWheel);
        frontWheels.add(secondWheel);
        frontWheels.add(thirdWheel);
        frontWheels.add(fouthWheel);

        for (int i = 0; i < frontWheels.size(); i++) {
            if (!frontWheels.get(i).getOrientation().equals(WheelTypes.front)) {
                frontWheels.remove(i);
            }
        }

        isBreading = frontWheels.size() != 2;


        this.engine = engine;
        this.actualGear = actualGear;
        this.maxGear = maxGear;

        if (actualGear > maxGear) {
            isBreading = true;
        }
    }

    public Transmission(int actualGear, int maxGear) {
        this.actualGear = actualGear;
        this.maxGear = maxGear;

        if (actualGear > maxGear) {
            isBreading = true;
        }

        frontWheels = new ArrayList<>();
        frontWheels.add(new Wheel(WheelTypes.front));
        frontWheels.add(new Wheel(WheelTypes.front));
    }

    public boolean isBreading() {
        return isBreading;
    }

    public void setBreading(boolean breading) {
        isBreading = breading;
    }

    public void setActualGear(int actualGear) {
        this.actualGear = actualGear;
    }

    public int getMaxGear() {
        return maxGear;
    }

    public void setMaxGear(int maxGear) {
        this.maxGear = maxGear;
    }

    public int getActualGear() {
        return actualGear;
    }
}
