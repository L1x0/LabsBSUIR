package by.astakhau.carsimulator.model;

import lombok.Getter;
import lombok.Setter;


@Getter @Setter
public class LightIndicators {
    private boolean headlightsOn;
    private boolean highBeamOn;
    private boolean leftTurnSignal;
    private boolean rightTurnSignal;
    private boolean brakeLights;
    private boolean fogLightsOn;
    private boolean hazardLightsOn;

    public LightIndicators() {
        headlightsOn = false;
        highBeamOn = false;
        leftTurnSignal = false;
        rightTurnSignal = false;
        brakeLights = false;
        fogLightsOn = false;
        hazardLightsOn = false;
    }
}
