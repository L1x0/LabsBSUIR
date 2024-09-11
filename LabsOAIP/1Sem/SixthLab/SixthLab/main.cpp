//
//  main.cpp
//  SixthLab
//
//  Created by Артём Астахов on 17.09.23.
//

#include <iostream>
#include <string>
using namespace std;


int main(int argc, const char * argv[]) {
   // setlocale(LC_ALL, "Rus");
    cout << "введите строку (не используя русских символов): ";
    char str[1000000];
    gets(str); //принимает строку до нажатия клавиши Enter
    int counter = 0; //счётчик
    int i = 0;
    while(true)
    {
        if (str[i] == '\0') // \0 - конец строки
            break;
        else
        {
            counter++; //считаем кол-во элементов
            i++;
        }
    }
    cout << "\nдлина массива равна " << counter << endl;
    int counter_a = 0;
    i = 0;
    for(i = 0; i < counter; i++) //считаем кол-во вхождений символа "а"
    {
        if (str[i] == 'a')
        {
            counter_a++;
        }
    }
    cout << "количество вхождений буквы 'a' равно " << counter_a << endl;
    return 0;
}
