#include <iostream>
#include <fstream>
#include <unistd.h>
#include <sys/mman.h>      // Для общей области памяти
#include <sys/wait.h>      // Для ожидания завершения дочерних процессов
#include <fcntl.h>         // Для работы с семафорами
#include <semaphore.h>     // Для использования семафоров
#include <cstring>         // Для работы со строками
#include <signal.h>

// Размер буфера для передачи данных
const int BUFFER_SIZE = 1024;

int main() {
    const char* sourceFile = "input.txt";
    const char* destinationFile = "output.txt";

    // Открытие исходного файла
    std::ifstream src(sourceFile, std::ios::binary);
    if (!src.is_open()) {
        std::cerr << "Ошибка открытия исходного файла.\n";
        return 1;
    }

    // Открытие файла назначения
    std::ofstream dest(destinationFile, std::ios::binary);
    if (!dest.is_open()) {
        std::cerr << "Ошибка открытия файла назначения.\n";
        return 1;
    }

    // Создание общей области памяти
    void* sharedMemory = mmap(nullptr, BUFFER_SIZE, PROT_READ | PROT_WRITE, MAP_SHARED | MAP_ANONYMOUS, -1, 0);
    if (sharedMemory == MAP_FAILED) {
        std::cerr << "Ошибка создания общей области памяти.\n";
        return 1;
    }

    // Создание семафоров для синхронизации
    sem_t* readSemaphore = sem_open("/read_sem", O_CREAT, 0644, 1);   // Разрешает чтение
    sem_t* writeSemaphore = sem_open("/write_sem", O_CREAT, 0644, 0); // Разрешает запись
    if (readSemaphore == SEM_FAILED || writeSemaphore == SEM_FAILED) {
        std::cerr << "Ошибка создания семафоров.\n";
        return 1;
    }

    pid_t readerPid = fork();  // Создание дочернего процесса для чтения
    if (readerPid == 0) {
        // Читающий процесс
        char* buffer = static_cast<char*>(sharedMemory);
        while (!src.eof()) {
            sem_wait(readSemaphore);  // Ожидаем разрешения на чтение

            src.read(buffer, BUFFER_SIZE);
            std::streamsize bytesRead = src.gcount();

            // Если произошла ошибка чтения
            if (src.bad()) {
                std::cerr << "Ошибка при чтении файла.\n";
                exit(1);  // Завершаем процесс с ошибкой
            }

            if (bytesRead > 0) {
                // В конце файла могут быть не полностью заполненные блоки
                memset(buffer + bytesRead, 0, BUFFER_SIZE - bytesRead);
            }

            sem_post(writeSemaphore);  // Разрешаем запись

            if (bytesRead < BUFFER_SIZE) break;  // Прерываем цикл, если конец файла
        }

        exit(0);  // Завершаем процесс чтения
    }

    pid_t writerPid = fork();  // Создание дочернего процесса для записи
    if (writerPid == 0) {
        // Пишущий процесс
        char* buffer = static_cast<char*>(sharedMemory);
        while (true) {
            sem_wait(writeSemaphore);  // Ожидаем разрешения на запись

            // Если буфер пуст, значит достигнут конец файла
            if (strlen(buffer) == 0) break;

            dest.write(buffer, BUFFER_SIZE);
            if (!dest.good()) {
                std::cerr << "Ошибка при записи в файл.\n";
                exit(1);  // Завершаем процесс с ошибкой
            }

            sem_post(readSemaphore);  // Разрешаем чтение
        }

        exit(0);  // Завершаем процесс записи
    }

    // Обработка ошибок дочерних процессов в родительском процессе
    int status;
    if (waitpid(readerPid, &status, 0) > 0 && WIFEXITED(status) && WEXITSTATUS(status) != 0) {
        // Завершение пишущего процесса, если произошла ошибка в читающем процессе
        kill(writerPid, SIGKILL);
        std::cerr << "Читающий процесс завершился с ошибкой. Копирование прервано.\n";
    } else if (waitpid(writerPid, &status, 0) > 0 && WIFEXITED(status) && WEXITSTATUS(status) != 0) {
        // Завершение читающего процесса, если произошла ошибка в пишущем процессе
        kill(readerPid, SIGKILL);
        std::cerr << "Пишущий процесс завершился с ошибкой. Копирование прервано.\n";
    }

    // Ожидаем завершения дочерних процессов
    waitpid(readerPid, nullptr, 0);
    waitpid(writerPid, nullptr, 0);

    // Закрытие файлов
    src.close();
    dest.close();

    // Освобождение ресурсов
    munmap(sharedMemory, BUFFER_SIZE);
    sem_unlink("/read_sem");
    sem_unlink("/write_sem");
    sem_close(readSemaphore);
    sem_close(writeSemaphore);

    std::cout << "Копирование завершено.\n";

    return 0;
}
