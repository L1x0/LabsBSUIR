package by.astakhau.carsimulatorweb.service.impl;

import by.astakhau.carsimulatorweb.service.interfaces.DriverService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataInitializerService {

    private final DriverService driverService;

    @Autowired
    public DataInitializerService(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostConstruct
    public void init() {
        driverService.initializeDriver();
    }
} 