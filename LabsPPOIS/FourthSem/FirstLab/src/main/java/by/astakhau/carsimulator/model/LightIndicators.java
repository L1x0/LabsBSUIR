package by.astakhau.carsimulator.model;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class LightIndicators {
    private boolean highBeamOn;
    private boolean leftTurnSignal;
    private boolean rightTurnSignal;
    private boolean brakeLights;
    private boolean fogLightsOn;

    public LightIndicators() {
        highBeamOn = false;
        leftTurnSignal = false;
        rightTurnSignal = false;
        brakeLights = false;
        fogLightsOn = false;
    }
}
