#include <iostream>
#include <vector>
#include <unistd.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <sys/resource.h>
using namespace std;

int main()
{
    vector<pid_t> processes;
    setpriority(PRIO_PROCESS, 0, -20);
    int count;
    int timeOfLife;

    cout << "Введите количество процессов для создания" << endl;
    cin >> count;
    cout << "Сколько секунд должны работать процессы?" << endl;
    cin >> timeOfLife;

    for (int i = 0; i < count; i++) {
        pid_t pid = fork();

        if (pid == 0) {
            cout << "Запущен процесс" << endl;

            int fd = open("/dev/null", O_WRONLY);

            dup2(fd, STDOUT_FILENO);
            dup2(fd, STDERR_FILENO);

            close(fd);

            sleep(timeOfLife);

            exit(0);
        }

        if (pid > 0) {
            processes.push_back(pid);
        } else {
            cerr << "ошибка создания" << endl;
            exit(1);
        }

    }
    for (pid_t pid : processes) {
        waitpid(pid, nullptr, 0);
    }

    cout << "Все процессы завершены" << endl;

    return 0;
}
