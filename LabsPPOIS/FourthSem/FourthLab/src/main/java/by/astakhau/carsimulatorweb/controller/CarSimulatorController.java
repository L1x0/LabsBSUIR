package by.astakhau.carsimulatorweb.controller;

import by.astakhau.carsimulatorweb.service.impl.GarageServiceImpl;
import by.astakhau.carsimulatorweb.service.interfaces.CarControlService;
import by.astakhau.carsimulatorweb.service.interfaces.CarMaintenanceService;
import by.astakhau.carsimulatorweb.service.interfaces.DriverService;
import by.astakhau.carsimulatorweb.service.interfaces.GarageService;
import by.astakhau.carsimulator.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/car-simulator")
public class CarSimulatorController {

    private final DriverService driverService;
    private final CarControlService carControlService;
    private final CarMaintenanceService carMaintenanceService;
    private final GarageService garageService;
    private final GarageServiceImpl garageServiceImpl;

    @Autowired
    public CarSimulatorController(
            DriverService driverService,
            CarControlService carControlService,
            CarMaintenanceService carMaintenanceService,
            GarageService garageService,
            GarageServiceImpl garageServiceImpl) {
        this.driverService = driverService;
        this.carControlService = carControlService;
        this.carMaintenanceService = carMaintenanceService;
        this.garageService = garageService;
        this.garageServiceImpl = garageServiceImpl;
    }


    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Car currentCar = carControlService.getCurrentCar();
        
        Map<String, Object> response = new HashMap<>();
        response.put("driverName", driverService.getDriver().getName() + " " + driverService.getDriver().getLastName());
        response.put("carName", currentCar.getName());
        response.put("isRunning", currentCar.isRunning());
        response.put("movementState", currentCar.getMovementState());
        response.put("turnState", currentCar.getTurnState());
        response.put("fuelLevel", currentCar.getLocalFuel().getQuantity());
        response.put("maxFuel", currentCar.getMaxFuel().getQuantity());
        response.put("oilLevel", currentCar.getEngine().getEngineOilQuantity());
        response.put("maxOilLevel", currentCar.getEngine().getMaxEngineOilQuantity());
        response.put("needsRepair", currentCar.getEngine().isBreading());
        
        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/start-engine")
    public ResponseEntity<String> startEngine() {
        Car currentCar = carControlService.getCurrentCar();
        
        if (!currentCar.isRunning()) {
            carControlService.startEngine();
            return ResponseEntity.ok("Двигатель запущен");
        } else {
            return ResponseEntity.badRequest().body("Двигатель уже работает");
        }
    }

    @PostMapping("/stop-engine")
    public ResponseEntity<String> stopEngine() {
        Car currentCar = carControlService.getCurrentCar();
        
        if (currentCar.isRunning()) {
            carControlService.stopEngine();
            return ResponseEntity.ok("Двигатель остановлен");
        } else {
            return ResponseEntity.badRequest().body("Двигатель уже остановлен");
        }
    }

    @PostMapping("/move/{direction}")
    public ResponseEntity<String> move(@PathVariable String direction) {
        Car currentCar = carControlService.getCurrentCar();
        
        if (!currentCar.isRunning()) {
            return ResponseEntity.badRequest().body("Сначала запустите двигатель");
        }

        return switch (direction.toLowerCase()) {
            case "forward" -> {
                carControlService.moveForward();
                yield ResponseEntity.ok("Машина движется вперед");
            }
            case "backward" -> {
                carControlService.moveBackward();
                yield ResponseEntity.ok("Машина движется назад");
            }
            case "stop" -> {
                carControlService.stopCar();
                yield ResponseEntity.ok("Машина остановлена");
            }
            default -> ResponseEntity.badRequest().body("Неизвестное направление");
        };
    }

    @PostMapping("/turn/{direction}")
    public ResponseEntity<String> turn(@PathVariable String direction) {
        return switch (direction.toLowerCase()) {
            case "left" -> {
                carControlService.turnLeft();
                yield ResponseEntity.ok("Руль повернут влево");
            }
            case "right" -> {
                carControlService.turnRight();
                yield ResponseEntity.ok("Руль повернут вправо");
            }
            case "straight" -> {
                carControlService.turnStraight();
                yield ResponseEntity.ok("Руль выровнен");
            }
            default -> ResponseEntity.badRequest().body("Неизвестное направление поворота");
        };
    }
    
    @PostMapping("/repair")
    public ResponseEntity<String> repair() {
        carMaintenanceService.repairCar();
        return ResponseEntity.ok("Автомобиль отремонтирован");
    }
    
    @PostMapping("/refuel")
    public ResponseEntity<String> refuel(@RequestParam int amount) {
        if (amount <= 0) {
            return ResponseEntity.badRequest().body("Количество топлива должно быть положительным");
        }
        
        Car currentCar = carControlService.getCurrentCar();
        int currentFuel = currentCar.getLocalFuel().getQuantity();
        int maxFuel = currentCar.getMaxFuel().getQuantity();
        
        if (currentFuel >= maxFuel) {
            return ResponseEntity.badRequest().body("Бак уже полный");
        }
        
        carMaintenanceService.refuel(amount);
        return ResponseEntity.ok("Автомобиль заправлен");
    }
    
    @GetMapping("/check-oil")
    public ResponseEntity<Map<String, Object>> checkOil() {
        Car currentCar = carControlService.getCurrentCar();
        boolean isOilOk = carMaintenanceService.checkOil();
        
        Map<String, Object> response = new HashMap<>();
        response.put("oilLevel", currentCar.getEngine().getEngineOilQuantity());
        response.put("maxOilLevel", currentCar.getEngine().getMaxEngineOilQuantity());
        response.put("isOilOk", isOilOk);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/garage")
    public ResponseEntity<List<Map<String, Object>>> getGarage() {
        return ResponseEntity.ok(garageService.getCarsList());
    }
    
    @PostMapping("/garage/add")
    public ResponseEntity<String> addCar(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Название автомобиля не может быть пустым");
        }
        
        garageService.addCar(name);
        return ResponseEntity.ok("Автомобиль " + name + " добавлен в гараж");
    }
    
    @PostMapping("/garage/add-sports")
    public ResponseEntity<String> addSportsCar(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Название автомобиля не может быть пустым");
        }
        
        garageServiceImpl.addSportsCar(name);
        return ResponseEntity.ok("Спортивный автомобиль " + name + " добавлен в гараж");
    }
    
    @PostMapping("/garage/add-full-tank")
    public ResponseEntity<String> addFullTankCar(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Название автомобиля не может быть пустым");
        }
        
        garageServiceImpl.addFullTankCar(name);
        return ResponseEntity.ok("Автомобиль " + name + " с полным баком добавлен в гараж");
    }
    
    @DeleteMapping("/garage/{carId}")
    public ResponseEntity<String> removeCar(@PathVariable int carId) {
        boolean removed = garageService.removeCar(carId);
        
        if (removed) {
            return ResponseEntity.ok("Автомобиль удален из гаража");
        } else {
            return ResponseEntity.badRequest().body("Не удалось удалить автомобиль. Возможно, это единственный или текущий автомобиль.");
        }
    }
    
    @PostMapping("/garage/switch/{carId}")
    public ResponseEntity<String> switchCar(@PathVariable int carId) {
        boolean switched = garageService.switchCar(carId);
        
        if (switched) {
            Car car = carControlService.getCurrentCar();
            return ResponseEntity.ok("Выбран автомобиль: " + car.getName());
        } else {
            return ResponseEntity.badRequest().body("Не удалось выбрать автомобиль");
        }
    }
} 