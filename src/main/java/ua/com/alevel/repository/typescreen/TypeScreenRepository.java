package ua.com.alevel.repository.typescreen;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.accessory.TypeScreen;
import java.util.List;
import java.util.Optional;

@Repository
public interface TypeScreenRepository extends CrudRepository<TypeScreen, String> {

    Optional<TypeScreen> findFirstByName(String name);

    @Query("select typeScreen.name from TypeScreen typeScreen order by typeScreen.name asc")
    List<String> findAllTypeScreensNames();

    @Query("select typeScreen from TypeScreen typeScreen order by typeScreen.name asc")
    List<TypeScreen> findAllTypeScreensForAdmin();
}
