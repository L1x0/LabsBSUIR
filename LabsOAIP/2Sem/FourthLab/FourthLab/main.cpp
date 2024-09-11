//
//  main.cpp
//  FourthLab
//
//  Created by Артём Астахов on 16.02.24.
//

#include <iostream>
#include <string>
#include <cctype>
using namespace std;

struct Stack {
    string info;
    Stack* next;
} *stack, *solution;

string resultList = "";
string inputList = "";

void inStack(Stack **p, string in) {
    Stack *t = new Stack;
    t -> info = in;
    t -> next = *p;
    
    *p = t;
}

string outStack(Stack **p) {
    string out;
    Stack* t = (*p);
    out = t -> info;
    
    *p = (*p) -> next;
    delete t;
    return out;
}

string firtstInStack(Stack *p) {
    if (p == NULL)
        return "(";
    else
        return p -> info;
}

void converToPrefixForm(Stack **p, string *inputString, string *outString) {
    for (int i = 0; i <= (*inputString).length(); i++) {
        
         if (isdigit((*inputString)[i])) {
            string temp = "";
            
            for(int j = i; isdigit((*inputString)[j]) || (*inputString)[j] == '.'; j++) {
                temp.insert(temp.length(), 1, (*inputString)[j]);
                
                i = j;
            }
            
            (*outString) += temp;
        }
        
        else if ((*inputString)[i] == '(')
            inStack(&stack, "(");
        
        else if ((*inputString)[i] == ')') {
            while (firtstInStack(stack) != "(") {
                *outString += outStack(&stack);
            }
            string temp;
                
            outStack(&stack);
        }
        
        else if ((*inputString)[i] == '+' || (*inputString)[i] == '-' ||
            (*inputString)[i] == '*' || (*inputString)[i] == '/') {
            
            string outStackElement = "";
            
            if ((*inputString)[i] == '+' || (*inputString)[i] == '-') {
                
                    while (firtstInStack(stack) != "(") {
                        string outStackElement = outStack(&stack);
                        
                        (*outString).insert((*outString).length(), outStackElement);
                        
                    }
                string temp(1, (*inputString)[i]);
                    
                inStack(&stack, temp);
                
            } else {
                    while (firtstInStack(stack) != "(" && firtstInStack(stack) != "+" && firtstInStack(stack) != "-") {
                        string outStackElement = outStack(&stack);
                        
                        (*outString).insert((*outString).length(), outStackElement);
                        
                        
                    }
                string temp(1, (*inputString)[i]);
                    
                inStack(&stack, temp);
                
            }
        }
        
            
    }
    
    while (stack != NULL) {
        string outStackElement = outStack(&stack);
        
        (*outString).insert((*outString).length(), outStackElement);
    }
}

double solveInPrefixForm(string str) {
    double result;
    
    for (int i = 0; i < str.length(); i++) {
        if (isdigit(str[i])) {
            string temp;
            
            temp.push_back(str[i]);
            temp.push_back(str[i+1]);
            temp.push_back(str[i+2]);
            
            inStack(&solution, temp);
            i = i + 2;
        }
        
        if (!isdigit(str[i])) {
            double firstOperand, secondOperand;
            
            secondOperand = stold(outStack(&solution));
            firstOperand = stold(outStack(&solution));
            
            if (str[i] == '+')
                inStack(&solution, to_string(firstOperand + secondOperand));
            
            if (str[i] == '-')
                inStack(&solution, to_string(firstOperand - secondOperand));
            
            if (str[i] == '*')
                inStack(&solution, to_string(firstOperand * secondOperand));
            
            if (str[i] == '/')
                inStack(&solution, to_string(firstOperand / secondOperand));
        }
    }
    
    result = stod(outStack(&solution));
    
    return result;
}

int main(int argc, const char * argv[]) {
    cout << "Введите пожалуйста выражение: " << endl;
    getline(cin, inputList);
    
    converToPrefixForm(&stack, &inputList, &resultList);
    cout << "Ваше выражение в ОПЗ: " << resultList << endl;
    
    cout << "Результат вычислений " << solveInPrefixForm(resultList) << endl;
    
    return 0;
}
//8.6/(2.4-5.1)*(0.3+7.9)
