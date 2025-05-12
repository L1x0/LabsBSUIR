package by.astakhau.carsimulator.cotroller;

public enum SimulatorState {
    WAITING,            // Состояние ожидания ввода команды
    VEHICLE_CONTROL,    // Управление автомобилем
    MAINTENANCE,        // Обслуживание (заправка, масло, ремонт)
    GARAGE_MANAGEMENT,  // Управление гаражом (добавление/удаление авто)
    ERROR,             // Состояние ошибки
    EXIT               // Выход из программы
}
