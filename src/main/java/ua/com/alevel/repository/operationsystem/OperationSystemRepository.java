package ua.com.alevel.repository.operationsystem;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.accessory.OperationSystem;
import java.util.List;
import java.util.Optional;

@Repository
public interface OperationSystemRepository extends CrudRepository<OperationSystem, String> {

    Optional<OperationSystem> findFirstByName(String name);

    @Query("select operationSystem.name from OperationSystem operationSystem order by operationSystem.name asc")
    List<String> findAllOperationSystemsNames();

    @Query("select operationSystem from OperationSystem operationSystem order by operationSystem.name asc")
    List<OperationSystem> findAllOperationSystemsForAdmin();
}
