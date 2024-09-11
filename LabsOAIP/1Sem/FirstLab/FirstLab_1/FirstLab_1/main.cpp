//
//  main.cpp
//  FirstLab_1
//
//  Created by Артём Астахов on 8.09.23.
//

#include <iostream>
#include <math.h>
#define PI 3.1415926
using namespace std;

int main(int argc, const char * argv[]) {
    double z1, z2, alpha;
    cout << "Введите угол alpha: ";
    cin >> alpha;
    z1 = 2*pow(sin(3*PI - 2*alpha), 2)*pow(cos(5*PI +2*alpha),2); //Рассчёт z1
    z2 = 0.25 - 0.25*(sin(2.5*PI - 8*alpha)); //Рассчёт z2
    cout << '\n' << z1 << '\n' << z2 << '\n';
    return 0;
}
