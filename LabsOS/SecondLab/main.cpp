#include <iostream>
#include <unistd.h>
#include <sys/wait.h>

void printProcessInfo(const std::string &message) {
    pid_t pid = getpid();
    pid_t ppid = getppid();
    std::cout << message << " (PID: " << pid << ", PID родителя: " << ppid << ")" << std::endl;
}

void waitForAllChildren() {
    while (wait(NULL) > 0);
}

int main() {
    printProcessInfo("Основной процесс запущен");

    pid_t pid_1 = fork();

    if (pid_1 == 0) {
        printProcessInfo("Дочерний процесс 1 запущен");

        pid_t pid_2 = fork();
        if (pid_2 == 0) {
            printProcessInfo("Дочерний процесс 2 запущен");

            pid_t pid_3 = fork();
            if (pid_3 == 0) {
                printProcessInfo("Дочерний процесс 3 запущен");

                pid_t pid_5 = fork();
                if (pid_5 == 0) {
                    printProcessInfo("Дочерний процесс 5 запущен");

                    pid_t pid_7 = fork();
                    if (pid_7 == 0) {
                        printProcessInfo("Дочерний процесс 7 запущен");
                        _exit(0);
                    }

                    waitForAllChildren();
                    _exit(0);
                }

                waitForAllChildren();
                _exit(0);
            }

            pid_t pid_4 = fork();
            if (pid_4 == 0) {
                printProcessInfo("Дочерний процесс 4 запущен");

                pid_t pid_6 = fork();
                if (pid_6 == 0) {
                    printProcessInfo("Дочерний процесс 6 запущен");
                    _exit(0);
                }

                waitForAllChildren();
                _exit(0);
            }

            waitForAllChildren();
            _exit(0);
        }

        execlp("pwd", "pwd", NULL);

        waitForAllChildren();
        printProcessInfo("Дочерний процесс 1 завершает работу");
        _exit(0);
    }

    waitForAllChildren();
    printProcessInfo("Основной процесс завершает работу");
    return 0;
}
