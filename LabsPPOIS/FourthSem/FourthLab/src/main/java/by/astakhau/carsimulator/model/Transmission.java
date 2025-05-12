package by.astakhau.carsimulator.model;

import by.astakhau.carsimulator.model.wheels.Wheel;
import by.astakhau.carsimulator.model.wheels.WheelTypes;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
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
            if (!frontWheels.get(i).getOrientation().equals(WheelTypes.FRONT)) {
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
        frontWheels.add(new Wheel(WheelTypes.FRONT));
        frontWheels.add(new Wheel(WheelTypes.FRONT));
    }

    public boolean isBreading() {
        return isBreading;
    }

}
