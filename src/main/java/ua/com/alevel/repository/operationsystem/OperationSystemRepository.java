package ua.com.alevel.repository.operationsystem;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.accessory.OperationSystem;
import java.util.Optional;

@Repository
public interface OperationSystemRepository extends CrudRepository<OperationSystem, String> {

    Optional<OperationSystem> findFirstByName(String name);
}
