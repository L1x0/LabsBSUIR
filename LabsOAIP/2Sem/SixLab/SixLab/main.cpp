//
//  main.cpp
//  SixLab
//
//  Created by Артём Астахов on 21.03.24.
//

#include <iostream>
using namespace std;

struct Student {
    string lastname;
    string group;
    int mark;
};

int hashFunc(int key) {
    return key % 11;
}

int adress(int key, Student hashmap[15]) {
    int adress = hashFunc(key);
    
    if (hashmap[adress].lastname != "" && hashmap[adress].group != "")
    {
        int i = 1;
        while (hashmap[adress].lastname != "" && hashmap[adress].group != "") {
            adress = adress + i * i;
            i++;
        }
    }
    
    return adress;
}

Student getFromHashMap(int key, Student hashMap[15]) {
    int adress = hashFunc(key), i = 0;
    Student crash;
    
    if (hashMap[adress].lastname == "" && hashMap[adress].group == "") {
        cout << "не найден" << endl;
        return crash;
    }
    
    while(hashMap[adress].mark != key) {
        adress = adress + i * i;
        
        if (hashMap[adress].lastname == "" && hashMap[adress].group == "") {
            cout << "не найден" << endl;
            return crash;
        }
        
        i++;
    }
    
    cout << "\nСтудент " << hashMap[adress].lastname << " группы " << hashMap[adress].group << " получил отметку " << hashMap[adress].mark << endl;
    
    return hashMap[adress];
}


int main(int argc, const char * argv[]) {
    Student array[6];
    Student hashMap[15];
    
    for (int i = 0; i < 6; i++) {
        cout << "\nВведите фамилию студента" << endl;
        cin >> array[i].lastname;
        
        cout << "\nВведите номер группы" << endl;
        fflush(stdin);
        getline(cin, array[i].group);
        
        cout << "\nВведите отметку студента" << endl;
        cin >> array[i].mark;
        
        cout << "____________";
    }
    
    
    for (int i = 0; i < 6; i++) {
        hashMap[adress(array[i].mark, hashMap)] = array[i];
    }
    
    
    cout << "\nПо какому ключу вести поиск" << endl;
    int key;
    cin >> key;
    Student result = getFromHashMap(key, hashMap);
    
    cout << "Исходный массив:" << endl;
    for (int i = 0; i < 6; i++) {
        cout << i + 1 << ")Студент " << array[i].lastname << " группы " << array[i].group << " имеет отметку " << array[i].mark << endl;
    }
    
    cout << "Хеш-таблица:" << endl;
    for (int i = 0; i < 15; i++) {
        if (hashMap[i].group != "" && hashMap[i].lastname != "")
            cout << i + 1 << ")Студент " << hashMap[i].lastname << " группы " << hashMap[i].group << " имеет отметку " << hashMap[i].mark << endl;
        else
            cout << i + 1 << ")Пустой элумент" << endl;
    }

    
    return 0;
}
