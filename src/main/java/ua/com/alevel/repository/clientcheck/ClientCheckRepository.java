package ua.com.alevel.repository.clientcheck;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.check.ClientCheck;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientCheckRepository extends CrudRepository<ClientCheck, String> {

    @Query("select check from ClientCheck check where check.registeredUser.id = ?1 and check.created = ?2")
    Optional<ClientCheck> findClientCheckForUserIdForNewOrder(String userId, Date date);

    @Query("select check from ClientCheck check where check.isClosed = false and check.registeredUser.id = ?1")
    List<ClientCheck> findAllNoClosedChecksForUserId(String userId);

    @Query("select check from ClientCheck check where check.isClosed = true and check.registeredUser.id = ?1")
    List<ClientCheck> findAllClosedChecksForUserId(String userId);

    @Modifying
    @Query("update ClientCheck check set check.isClosed = ?1 where check.id = ?2")
    void updateCheckClosed(boolean isClosed, String id);
}
