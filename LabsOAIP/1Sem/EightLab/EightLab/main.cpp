//
//  main.cpp
//  EightLab
//
//  Created by Артём Астахов on 4.10.23.
//

#include <iostream>
#include <string>
using namespace std;

string default_func(string str) //Решение обычной функцией
{
    int x = 0;
    int y = (int)str.length() - 1;
    if (str.length() == 1)
        return "один символ";
    while(true)                 //проверяем первый и последний члены строки
                               // и движемся навстречу от начала к концу и наоборот
    {
        if (str[x] != str[y])
            break;
        else
        {
            if(x == (int)str.length() - 1 && y == 0)
                return "true";
            x++;            //если у принимает изначальное значение х, и наоборот -- строки симметричны
            y--;
        }
    }
    return "false";
}

string recursive_func(string str)
{
    const string empty;
    if(str.empty())
        return "true";  //если строка пуста, строка симметрична
    if(str[0] != str[str.length()- 1])
        return "false";
    if (str.length() == 1)
        return "один символ"; // выводит если изначально у нас один символ
    if(str[0] == str[str.length()- 1])
    {
        str.replace(0, 1, empty);
        str.replace(str.length()-1, 1, empty);
        if(str.length() == 1)                   //если первый и последний символы совпадают удаляем их
            return "true";
    }
    return recursive_func(str); //вызываем функцию рекурсивно
}

int main(int argc, const char * argv[]) {
    string str;
    getline(cin, str);
    if(recursive_func(str) == "true")
    {
        cout << "строка симметрична" << endl;
    }
    else if(recursive_func(str) == "false")
    {
        cout << "Не симметрична" << endl;
    }
    else
        cout << "Один символ" << endl;
    if (recursive_func(str) == default_func(str))
        cout << "\nЗначения функций совпадают" << endl;
    return 0;
}
