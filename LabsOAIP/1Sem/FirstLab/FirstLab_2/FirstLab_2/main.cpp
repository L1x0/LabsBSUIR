//
//  main.cpp
//  FirstLab_2
//
//  Created by Артём Астахов on 11.09.23.
//

#include <iostream>
#include <math.h>
using namespace std;

int main(int argc, const char * argv[]) {
    double z1, z2, alpha;
    while(true)
    {
        cout << "Введите угол alpha: ";
        while(!(cin >> alpha))
        {
            cout << "Требуется ввести ЧИСЛО";
            cin.clear();
            fflush(stdin);
        }
        if (cos(alpha) + 1 - 2*pow(sin(2*alpha),2) != 0)
        {
            break;
        }
        cout << "деление на ноль";
    }
    z1 = (sin(2*alpha) + sin(5*alpha) - sin(3*alpha))/(cos(alpha) + 1 - 2*pow(sin(2*alpha),2));
    z2 = 2*sin(alpha);
    cout << '\n' << z1 << '\n' << z2 << endl;
    return 0;
}
