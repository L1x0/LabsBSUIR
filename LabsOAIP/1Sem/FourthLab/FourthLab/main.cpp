//
//  main.cpp
//  FourthLab
//
//  Created by Артём Астахов on 12.09.23.
//

#include <iostream>
using namespace std;

void func(int N)
{
    int counter = 1; //Считаем количество цифр
    int i = 10;
    while(true)
    {
        if (N / i == 0)
            break;
        else
        {
            i  = i * 10;
            counter++;
        }
            
    }
    int counter1 = 0;
    for (int i = 0; i < counter; i++) //Считаем кол-во цифр кратных 3
    {
        if (((int)(N / pow(10,i)) % 10) != 0
            && ((int)(N / pow(10,i)) % 10) % 3 == 0)
        {
            counter1++;
        }
    }
    int *arr = new int[counter1]; //Создаём динамический массив
    int j =0;
    for (int i = 0; i < counter; i++)// Закидываем все кратные 3 цифры в массив arr
    {
        if (((int)(N / pow(10,i)) % 10) != 0
            && ((int)(N / pow(10,i)) % 10) % 3 == 0)
        {
            arr[j] = (int)(N / pow(10,i)) % 10;
            j++;
        }
    }
    cout << "\nМассив arr: ";
    for (int i = 0; i < counter1; i++) //Вывод массива
    {
        cout << arr[i] << ' ';
    }
    cout << '\n';
    delete[] arr;
    
}

int main(int argc, const char * argv[]) {
    int N;
    cout << "Введите целое число: ";
    while(!(cin >> N))//провека на численный ввод
    {
        cout << "Ошибка введите заново";
        cin.clear();
        fflush(stdin);
    }
    func(N);//вывод массива и вызов функции
    return 0;
}
