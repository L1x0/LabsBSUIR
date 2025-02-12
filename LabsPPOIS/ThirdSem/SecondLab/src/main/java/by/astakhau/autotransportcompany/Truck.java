package by.astakhau.autotransportcompany;

import java.util.LinkedList;

public class Truck extends Car {
    private int maxWeight;
    private int numberOfAxes;
    LinkedList<String> route;

    public Truck(String color, String model, String engine, int maxSpeed,  int weight, int maxWeight, int numberOfAxes) {
        super(color, model, engine, maxSpeed, weight);
        this.maxWeight = maxWeight;
        this.numberOfAxes = numberOfAxes;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public int getNumberOfAxes() {
        return numberOfAxes;
    }

    public void setNumberOfAxes(int numberOfAxes) {
        this.numberOfAxes = numberOfAxes;
    }

    public LinkedList<String> getRoute() {
        return route;
    }

    public void setRoute(LinkedList<String> route) {
        this.route = route;
    }

    public void addCityToRoute(String city) {
        route.add(city);
    }
}
