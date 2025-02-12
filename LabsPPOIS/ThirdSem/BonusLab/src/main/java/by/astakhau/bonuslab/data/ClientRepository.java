package by.astakhau.bonuslab.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {
    void deleteByOrderId(Long orderId);
}

