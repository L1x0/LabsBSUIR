#include <iostream>
#include <unistd.h>
#include <sys/wait.h>
#include <vector>

void printProcessInfo(const std::string& message) {
    pid_t pid = getpid();
    pid_t ppid = getppid();
    std::cout << message << " (PID: " << pid << ", PID родителя: " << ppid << ")" << std::endl;
}

int main() {
    std::vector<int> tree = {0, 0, 1, 1, 2, 3, 4, 5};

    pid_t pids[tree.size()];
    printProcessInfo("Основной процесс запущен");

    for (size_t i = 0; i < tree.size(); ++i) {
        if (i == 0 || pids[tree[i]] == getpid()) {
            pid_t pid = fork();
            if (pid == 0) {
                printProcessInfo("Дочерний процесс " + std::to_string(i) + " запущен");

                if (i == 1) {
                    std::cout << "Процесс " << i << " будет выполнять команду pwd позже." << std::endl;
                }

                pids[i] = getpid();
            } else if (pid > 0) {
                pids[i] = pid;
                wait(NULL);
            }
        }
    }

    if (pids[1] == getpid()) {
        std::cout << "Процесс 1 выполняет команду pwd:" << std::endl;
        execl("/bin/pwd", "pwd", (char *)0);
        _exit(0);
    }

    printProcessInfo("Основной процесс завершает работу");
    return 0;
}
