#include <iostream>
#include "memory_manager.h"




int main() {
    try {
        MemoryManager manager(1024); // Инициализация с 1024 байтами памяти

        // Выделение блоков
        void *block1 = manager.allocate(128);
        void *block2 = manager.allocate(256);

        // Запись данных
        std::string data = "Пример данных";
        manager.write(block1, data.c_str(), data.size() + 1);

        // Чтение данных
        char buffer[128];
        manager.read(block1, buffer, data.size() + 1);
        std::cout << "Прочитанные данные: " << buffer << "\n";

        // Удаление блока
        manager.deallocate(block1);
        manager.deallocate(block2);

        // Состояние памяти
        manager.printStatus();
    } catch (const std::exception &e) {
        std::cerr << "Ошибка: " << e.what() << "\n";
    }

    return 0;
}


