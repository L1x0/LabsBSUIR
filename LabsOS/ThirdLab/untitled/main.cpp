#include <iostream>
#include <vector>

int main() {
    return 0;
}

class Magazine {
private:
    std::string name;
    std::string header;

public:
    Magazine(std::string name, std::string header) : name(name), header(header) {
    }

    ~Magazine() {
        std::cout << name << " " << header << std::endl;
    }

    std::string getName() const {
        return name;
    }

    std::string getHeader() const{
        return header;
    }

    std::string forCompare() const {
        return header;
    }
};

class Book {
private:
    std::string name;
    std::string author;

public:
    Book(std::string name, std::string author) : name(name), author(author) {
    }

    std::string getName() const {
        return name;
    }

    std::string getAuthor() const {
        return author;
    }

    std::string forCompare() const {
        return author;
    }
};

template<typename T>
class Shelf {
private:
    const size_t size;
    size_t actualSize;
    std::vector<T> objects;
public:
    Shelf(size_t size) : size(size) {}

    void add(T object) {
        if (actualSize <= size) {
            objects.push_back(object);
            actualSize++;
        } else {
            throw std::out_of_range("The size is greater than size of array");
        }
    }
    void remove(T object) {
        if (actualSize == 0) {
            throw std::out_of_range("The array is empty");
        } else {
            objects.erase(std::remove(objects.begin(), objects.end(), object), objects.end());
        }
    }
};

class Cabinet
