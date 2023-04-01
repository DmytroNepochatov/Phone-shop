package ua.com.alevel.repository.clientcheck;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    @Modifying
    @Query("update ClientCheck check set check.isClosed = ?1, check.closedDate = ?2 where check.id = ?3")
    void updateCheckClosed(boolean isClosed, Date closed, String id);

    @Query("select check from ClientCheck check where check.registeredUser.id = ?1")
    List<ClientCheck> findAllChecksForUserId(String userId);

    @Query("select clientCheck.registeredUser.id from ClientCheck clientCheck where clientCheck.id = ?1")
    String getUserIdForCheckId(String checkId);

    @Transactional
    @Modifying
    @Query("delete from ClientCheck clientCheck where clientCheck.id = ?1")
    void deleteByClientCheckId(String checkId);

    @Query("select clientCheck.id from ClientCheck clientCheck where clientCheck.registeredUser.emailAddress = ?1 and clientCheck.deliveryType = 'default'")
    Optional<String> findDefaultCheckIdForUserEmail(String email);

    @Query("select clientCheck from ClientCheck clientCheck where clientCheck.deliveryType = 'default'")
    List<ClientCheck> findAllDefaultClientChecksForCleaner();

    @Query("select clientCheck from ClientCheck clientCheck where clientCheck.isClosed = true and clientCheck.closedDate between ?1 and ?2")
    List<ClientCheck> findAllClientChecksBetweenDates(Date startDate, Date endDate);
}
