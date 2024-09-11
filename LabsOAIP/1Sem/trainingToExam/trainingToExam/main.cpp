#include <iostream>
#include <math.h>
using namespace std;


int main(int argc, const char * argv[]) {
    double x, x_final, h , s , y, temp;
    x = 0.3;
    x_final = 1.4;
    h = 0.093;
    for (; x < x_final + h/2; x+=h)
    {
        y = log(1/(2 + 2*x + x*x));
        temp = -(1+x)*(1+x);
        s = temp;
        for(int i = 1; i <= 835; i++)
        {
            temp *= -(1+x)*(1+x)*i/(i+1);
            s += temp;
        }
        cout << x << '\t' << y << '\t' << s << endl;
    }
    return 0;
}
