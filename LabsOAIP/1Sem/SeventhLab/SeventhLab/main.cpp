//
//  main.cpp
//  SeventhLab
//
//  Created by Артём Астахов on 28.09.23.
//

#include <iostream>
#include <fstream>
#include <string>
using namespace std;

void write_to_file(string str) //Запись в файл
{
    fstream file;
    file.open("output.txt", ios::in | ios::out );
    file << str << endl;
    file.close();
}

string read_by_file(string new_string) //Чтение из файла
{
    ifstream file;
    file.open("output.txt");
    while(!file.eof()) //До окончания файла
    {
        string temp;
        file >> temp;
        new_string += temp;
        new_string += " ";
    }
    file.close();
    return new_string;
}

string encryption(string new_string) //Шифрование
{
    for (int i = 1; i < new_string.length(); i++)
    {
        if (new_string[i] == ' ' || new_string[i-1] == ' ') //Пропускаем пробел
        {
            continue;
        }
        if (new_string[i] == '\x8f') //Если буква я
        {
            new_string[i-1] = new_string[i-1] - 1; //меняем /321 на /320
            new_string[i] = new_string[i] + 33; // меняем на код букыв "а"
        }
        else if (new_string[i] == '\277')
        {
            new_string[i-1] = '\321';
            new_string[i] = '\200';
        }
        else if (new_string[i] == '\265')//Защищаем все места где \320 меняется на \321 и наоборот
        {
            new_string[i-1] = '\321';
            new_string[i] = '\221';
        }
        else if (new_string[i] == '\221')
        {
            new_string[i-1] = '\320';
            new_string[i] = '\266';
        }
        else
        {
            new_string[i] = new_string[i] + 1; //переходим к следующей по коду букве
        }
        i++;
    }
    return new_string;
}

string decoding(string new_string) //Расшифровка
{
    for (int i = 1; i < new_string.length(); i++)
    {
        if (new_string[i] == ' ' || new_string[i-1] == ' ')
        {
            continue;
        }
        if (new_string[i] == '\260')
        {
            new_string[i-1] = new_string[i-1] + 1; //меняем "а" на "я"
            new_string[i] = new_string[i] - 33;
        }
        else if (new_string[i] == '\200')
        {
            new_string[i-1] = '\320';
            new_string[i] = '\277'; //Защищаем все места где \320 меняется на \321 и наоборот
        }
        else if (new_string[i] == '\221')
        {
            new_string[i-1] = '\320';
            new_string[i] = '\265';
        }
        else if (new_string[i] == '\266')
        {
            new_string[i-1] = '\321';
            new_string[i] = '\221';
        }
        else
        {
            new_string[i] = new_string[i] - 1; //переходим к предидущей по коду букве
        }
        i++;
    }
    return new_string;
}

int main(int argc, const char * argv[]) {
    string str;
    string new_string;
    cout << "Введите строку на русском языке: ";
    getline(cin, str);
    write_to_file(str);
    new_string = read_by_file(new_string);
    new_string = encryption(new_string);
    cout << '\n' << "зашифрованная строка: " << new_string << endl;
    new_string = decoding(new_string);
    cout << "Расшифрованная строка: " << new_string << endl;
    return 0;
}
