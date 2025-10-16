package by.astakhau.repairdep.secondlab.repositories;

import by.astakhau.repairdep.secondlab.entities.EmployeePosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeePositionRepository extends JpaRepository<EmployeePosition, Integer> {
}
