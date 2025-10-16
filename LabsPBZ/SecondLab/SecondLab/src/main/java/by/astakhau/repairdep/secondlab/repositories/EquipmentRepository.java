package by.astakhau.repairdep.secondlab.repositories;

import by.astakhau.repairdep.secondlab.entities.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    Optional<Equipment> findByInventoryNumber(String inventoryNumber);
}
