package by.astakhau.carsimulator.model;

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

    public boolean isHeadlightsOn() {
        return headlightsOn;
    }

    public void setHeadlightsState(boolean headlightsOn) {
        this.headlightsOn = headlightsOn;
    }

    public boolean isHighBeamOn() {
        return highBeamOn;
    }

    public void setHighBeamState(boolean highBeamOn) {
        this.highBeamOn = highBeamOn;
    }

    public boolean isLeftTurnSignal() {
        return leftTurnSignal;
    }

    public void setLeftTurnSignal(boolean leftTurnSignal) {
        this.leftTurnSignal = leftTurnSignal;
    }

    public boolean isRightTurnSignal() {
        return rightTurnSignal;
    }

    public void setRightTurnSignal(boolean rightTurnSignal) {
        this.rightTurnSignal = rightTurnSignal;
    }

    public boolean isBrakeLights() {
        return brakeLights;
    }

    public void setBrakeLights(boolean brakeLights) {
        this.brakeLights = brakeLights;
    }

    public boolean isFogLightsOn() {
        return fogLightsOn;
    }

    public void setFogLightsState(boolean fogLightsOn) {
        this.fogLightsOn = fogLightsOn;
    }

    public boolean isHazardLightsOn() {
        return hazardLightsOn;
    }

    public void setHazardLightsState(boolean hazardLightsOn) {
        this.hazardLightsOn = hazardLightsOn;
    }
}
