//
//  main.cpp
//  FifthLab
//
//  Created by Артём Астахов on 15.09.23.
//

#include <iostream>
#include <math.h>

using namespace std;

void powmatrix(int n, int degree)
{
    int columns = n;
    int **matrix = new int*[n];
    int **matrix_temp = new int*[n];
    int **matrix_result = new int*[n];
    for(int rows = 0; rows < n; rows++)
    {
        matrix_result[rows] = new int[columns];    //создаём двухмерный динамический массив
    }
    for (int i = 0; i < n; i++)
    {
        for(int j = 0; j < n; j++)     //Заполняем его случайными числами до 10
        {
            matrix_result[i][j] = 0;
        }
    }
    for(int rows = 0; rows < n; rows++)
    {
        matrix[rows] = new int[columns];    //создаём двухмерный динамический массив
    }
    for (int i = 0; i < n; i++)
    {
        for(int j = 0; j < n; j++)     //Заполняем его случайными числами до 10
        {
            matrix[i][j] = (rand()%10);
        }
    }
    for(int rows = 0; rows < n; rows++)
    {
        matrix_temp[rows] = new int[columns];    //создаём временный двухмерный динамический массив
    }
    for (int i = 0; i < n; i++)
    {
        for(int j = 0; j < n; j++)     //Временный массив делаем полностью идентичным исходному
        {
            matrix_temp[i][j] = matrix[i][j];
        }
    }
    cout << "Исходная матрица: \n";
    for (int i = 0; i < n; i++)
    {
        for(int j = 0; j < n; j++)     //Вывод исходной матрицы
        {
            cout << matrix[i][j] << ' ';
        }
        cout << '\n';
    }
    cout << "\n===================================\n\n";
    if (degree == 1)
    {
        for (int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)     //Вывод матрицы при значении степени равной 1
            {
                cout << matrix[i][j] << ' ';
            }
            cout << '\n';
        }
    }
    else
    {
        cout << "Результат:" << endl;
        for (int deg = 1; deg < degree; deg++)
        {
            for (int i = 0; i < n; i++)
            {
                for(int j = 0; j < n; j++)     //Заполняем конечную матрицу нулями
                {
                    matrix_result[i][j] = 0;
                }
            }
            for(int i = 0; i < n; i++)
            {
                
                for (int j = 0; j < n; j++)
                {
                    for(int l = 0; l < n; l++)
                    {
                        
                        matrix_result[i][j] += matrix[i][l] * matrix_temp[l][j]; //Производим умножение матриц
                    }
                }
            }
            for (int i = 0; i < n; i++)
            {
                for(int j = 0; j < n; j++)     //Переносим конечную матрицу в основную
                {
                    matrix[i][j] = matrix_result[i][j];
                }
            }
        }
        for (int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)     //Вывод результата
            {
                cout << matrix_result[i][j] << ' ';
            }
            cout << '\n';
        }
    }
    for(int i = 0; i < n; i++)
    {
        delete [] matrix[i];
    }
    delete [] matrix;
    for(int i = 0; i < n; i++)
    {                                       //очистка памяти занятой динамическими массивами
        delete [] matrix_temp[i];
    }
    delete [] matrix_temp;
    for(int i = 0; i < n; i++)
    {
        delete [] matrix_result[i];
    }
    delete [] matrix_result;
}

int main(int argc, const char * argv[]) {
    int n, degree;
    cout << "Введите порядок матрицы и степень: ";
    while(!(cin >> n >> degree))  //Проверка на численный ввод
    {
        cout << "Это должны быть целые числа\n";
        cin.clear();
        fflush(stdin);
        cout << "Введите порядок матрицы и степень: ";
    }
    powmatrix(n, degree);
    return 0;
}
