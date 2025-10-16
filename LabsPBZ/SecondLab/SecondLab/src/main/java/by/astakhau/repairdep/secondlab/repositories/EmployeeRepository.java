package by.astakhau.repairdep.secondlab.repositories;

import by.astakhau.repairdep.secondlab.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByFirstNameAndPhone(String firstName, String phone);
}
