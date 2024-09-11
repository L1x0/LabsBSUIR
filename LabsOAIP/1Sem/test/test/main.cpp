#include <iostream>
#include <math.h>
using namespace std;

double factorial(double num)
{
    if (num == 1 || num == 0)
        return 1;
    else
        return (num * (num - 1)); //Нахождение факториала рекурсией
}
long FindS(int n, double x)//Нахождение фунции S(x)
{
    long S = 0;
    for(int k = 0; k <= n; k++)
    {
        S += pow(-1, k)*((2*pow(2, k) + 1)/factorial(2*k))*pow(x,2*k);
    }
    return S;
}

double FindY(double x) //Нахождение функции Y(x)
{
    double Y;
    Y = (1 - (x*x/2))*cos(x) - (x/2)*sin(x);
    return Y;
}

int main(int argc, const char * argv[])
{
    int n = 0;
    double x, error_rate;
    error_rate = 1;
    cout << "погрешность равна: " << error_rate;
    cout  << "\nВведите значения х:";
    while(!(cin >> x))//Проверка на численный ввод
    {
        cout << "\nНужно ввести ЧИСЛА\n";
        cout  << "Введите знвчение х:";
        cin.clear();
        fflush(stdin);
    }
    while(true)//Считаем кол-во шагов суммы
    {
        if(abs(FindS(n, x) - FindY(x)) <= error_rate)
            break;
        n = n + 1;
    }
    cout << "Количество шагов вычисления суммы n = " << n + 1 << endl;
    return 0;
}
