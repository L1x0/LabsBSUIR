package by.astakhau.carsimulator.cotroller;

public enum SimulatorEvent {
    // События выбора действий
    IN_VEHICLE,
    SHOW_MENU,
    RETURN_TO_MENU,

    // События управления автомобилем
    START_ENGINE,
    STOP_ENGINE,
    MOVE_FORWARD,
    MOVE_BACKWARD,
    TURN_LEFT,
    TURN_RIGHT,
    SET_WHEELS_STRAIGHT,
    STOP_MOVEMENT,

    // События обслуживания
    IN_MAINTENANCE,
    START_REFUEL,
    CHECK_OIL,
    START_REPAIR,

    // События управления гаражом
    IN_GARAGE,
    ADD_CAR,
    REMOVE_CAR,
    LIST_CARS,
    SWITCH_CAR,

    // Системные события
    EXIT_PROGRAM,
    ERROR_OCCURRED,
}
