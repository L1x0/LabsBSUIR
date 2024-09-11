//
//  main.cpp
//  SecondLab
//
//  Created by Артём Астахов on 8.09.23.
//

#include <iostream>
using namespace std;

double min(double x, double y) //Функция нахождения минимума
{
    if (x > y)
        return y;
    if (y > x)
        return x;
    else
        return x;
}
double max(double x, double y, double z) //Функция нахождения максимума
{
    if (x > y && x > z)
        return x;
    if (y > x && y > z)
        return y;
    if (z > x && z > y)
        return z;
    else
        return x;
}

int main(int argc, const char * argv[]) {
    double x,y,z;
    //Определение переменных для ввода данных
    cout << "Введите три числа(x,y,z):";
    while(true)
    {
        //Зацикливаем ввод до момента получение корректных значений
        while (!(cin >> x >> y >> z)){
            //пока не будет введено нормальное число, выполняем цикл
            cout << "Требуется ввести ЧИСЛО\n";
            //сообщаем об ошибке ввода
            cin.clear();
            //сбрасываем коматозное состояние cin
            fflush(stdin);
            //очищаем поток ввода
        }
        try
        {
            if (min(x,y) == 0) //Проверка деления на ноль
                throw 1;
            break;
        }
        catch(int e)
        {
            if (e == 1)
            {
                cout << "деление на ноль\n";
            }
        }
    }
    cout << max(x,y,z)/min(x,y) + 5 << '\n';
    return 0;
}
