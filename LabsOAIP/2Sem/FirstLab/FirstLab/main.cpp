//
//  main.cpp
//  FirstLab
//
//  Created by Артём Астахов on 12.02.24.
//

#include <iostream>
#include <string>
using namespace std;

string recursive(int num, int sys) {
    string result;
    
    if (num < sys)
        return to_string(num) + result;
    else {
        result += to_string(num % sys);
        num = (num - num % sys) / sys;
        
        return recursive(num, sys) + result;
    }
}

string simple(int num, int sys) {
    string numberInString;
    
    while (num >= sys) {
        numberInString = to_string(num % sys) + numberInString;
        
        num = (num - num % sys) / sys;
    }
    numberInString = to_string(num) + numberInString;
    
    return numberInString;
}

int main(int argc, const char * argv[]) {
    int num, sys;
    cout << "Введите значение в десятичной системе: " << endl;
    cin >> num;
    
    cout << "\nВведите разряд системы счисления в которую нужно перевести число:" << endl;
    cin >> sys;
    
    cout << "\nРезультат обычной функции: " << simple(num, sys) << endl;
    cout << "Результат рекурсивной функции: " << recursive(num, sys) << endl;
    
    return 0;
}
