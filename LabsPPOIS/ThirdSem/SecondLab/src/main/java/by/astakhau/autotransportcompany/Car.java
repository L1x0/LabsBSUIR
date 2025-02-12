package by.astakhau.autotransportcompany;

abstract class Car {
    private String color;
    private String model;
    private String engine;
    private int maxSpeed;
    private int weight;

    public Car(String color, String model, String engine, int maxSpeed, int weight) {
        this.maxSpeed = maxSpeed;
        this.color = color;
        this.model = model;
        this.engine = engine;
        this.weight = weight;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
