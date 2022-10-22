package ua.com.alevel.repository.typescreen;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.accessory.TypeScreen;
import java.util.Optional;

@Repository
public interface TypeScreenRepository extends CrudRepository<TypeScreen, String> {

    Optional<TypeScreen> findFirstByName(String name);
}
