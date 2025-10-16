package by.astakhau.repairdep.secondlab.repositories;

import by.astakhau.repairdep.secondlab.entities.WriteOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WriteOffRepository extends JpaRepository<WriteOff, Integer> {
}
