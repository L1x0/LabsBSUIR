package by.astakhau.carsimulatorweb.service.interfaces;

import java.util.List;
import java.util.Map;


public interface GarageService {

    List<Map<String, Object>> getCarsList();
    void addCar(String name);
    boolean removeCar(int carIndex);
    boolean switchCar(int carIndex);
} 