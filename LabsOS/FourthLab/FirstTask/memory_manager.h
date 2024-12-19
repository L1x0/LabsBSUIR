//
// Created by Артём Астахов on 15.12.24.
//

#ifndef MEMORY_MANAGER_H
#define MEMORY_MANAGER_H

#include <iostream>
#include <vector>
#include <map>
#include <stdexcept>
#include <cstring>

class MemoryManager {
private:
    size_t totalMemory;
    size_t usedMemory;
    void* baseMemory; // Базовый блок памяти
    std::map<void*, size_t> allocatedBlocks; // Адрес блока -> размер блока
    std::vector<std::pair<void*, size_t>> freeBlocks; // Адрес блока -> размер блока

public:
    explicit MemoryManager(size_t memorySize) : totalMemory(memorySize), usedMemory(0) {
        baseMemory = malloc(memorySize); // Выделение единого блока памяти
        if (!baseMemory) {
            throw std::runtime_error("Ошибка инициализации памяти");
        }
        freeBlocks.emplace_back(baseMemory, memorySize);
    }

    ~MemoryManager() {
        free(baseMemory); // Освобождаем только базовый блок
    }

    // Выделение блока памяти
    void* allocate(size_t size) {
        for (auto it = freeBlocks.begin(); it != freeBlocks.end(); ++it) {
            if (it->second >= size) {
                void* block = it->first;
                size_t remaining = it->second - size;

                // Удаляем текущий свободный блок
                freeBlocks.erase(it);

                // Если осталась свободная память, добавляем её обратно
                if (remaining > 0) {
                    void* remainingBlock = static_cast<char*>(block) + size;
                    freeBlocks.emplace_back(remainingBlock, remaining);
                }

                allocatedBlocks[block] = size;
                usedMemory += size;
                return block;
            }
        }

        throw std::runtime_error("Недостаточно памяти для выделения блока");
    }

    // Освобождение блока памяти
    void deallocate(void* block) {
        auto it = allocatedBlocks.find(block);
        if (it == allocatedBlocks.end()) {
            throw std::runtime_error("Блок не найден или не был выделен");
        }

        size_t size = it->second;
        allocatedBlocks.erase(it);
        usedMemory -= size;

        freeBlocks.emplace_back(block, size);
    }

    // Запись данных в блок памяти
    void write(void* block, const void* data, size_t size) {
        auto it = allocatedBlocks.find(block);
        if (it == allocatedBlocks.end()) {
            throw std::runtime_error("Блок не найден или не был выделен");
        }

        if (size > it->second) {
            throw std::runtime_error("Размер данных превышает размер блока");
        }

        memcpy(block, data, size);
    }

    // Чтение данных из блока памяти
    void read(void* block, void* buffer, size_t size) {
        auto it = allocatedBlocks.find(block);
        if (it == allocatedBlocks.end()) {
            throw std::runtime_error("Блок не найден или не был выделен");
        }

        if (size > it->second) {
            throw std::runtime_error("Размер данных превышает размер блока");
        }

        memcpy(buffer, block, size);
    }

    // Отображение состояния памяти
    void printStatus() const {
        std::cout << "Общая память: " << totalMemory << " байт\n";
        std::cout << "Используемая память: " << usedMemory << " байт\n";
        std::cout << "Свободные блоки: " << freeBlocks.size() << "\n";
        for (const auto& block : freeBlocks) {
            std::cout << "  Адрес: " << block.first << ", Размер: " << block.second << " байт\n";
        }
        std::cout << "Выделенные блоки: " << allocatedBlocks.size() << "\n";
        for (const auto& block : allocatedBlocks) {
            std::cout << "  Адрес: " << block.first << ", Размер: " << block.second << " байт\n";
        }
    }

    // Дополнительный метод для тестов
    size_t getUsedMemory() const {
        return usedMemory;
    }

    size_t getFreeBlocksCount() const {
        return freeBlocks.size();
    }

    size_t getAllocatedBlocksCount() const {
        return allocatedBlocks.size();
    }
};

#endif //MEMORY_MANAGER_H
