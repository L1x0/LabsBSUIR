package by.astakhau.bonuslab.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Table(name = "employees")
public class Employee {
    @Id
    public Long id;

    @Column(value = "name")
    public String name;

    @Column(value = "age")
    public Integer age;

    @Column(value = "salary")
    public long salary;

    @Column(value = "experience")
    public Integer experience;

    private String department;

    public Employee(Long id, String name, Integer age,
                    long salary, Integer experience, Departments department) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.experience = experience;
        this.department = department.toStringValue();
    }
}