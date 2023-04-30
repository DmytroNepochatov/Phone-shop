package ua.com.webservice.repository.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import ua.com.webservice.model.accessory.Brand;
import ua.com.webservice.model.comment.Comment;
import ua.com.webservice.model.user.RegisteredUser;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment, String>, PagingAndSortingRepository<Comment, String> {
    @Query("select comment from Comment comment where comment.phone.phoneDescription.brand.id = ?1 and comment.phone.phoneDescription.name = ?2 and comment.phone.phoneDescription.series = ?3 and comment.phone.amountOfBuiltInMemory = ?4 and comment.phone.amountOfRam = ?5")
    List<Comment> findAllCommentsForPhone(String brand, String name, String series, int amountOfBuiltInMemory, int amountOfRam, Pageable pageable);

    @Query("select comment from Comment comment where comment.phone.phoneDescription.brand = ?1 and comment.phone.phoneDescription.name = ?2 " +
            "and comment.phone.phoneDescription.series = ?3 and comment.phone.amountOfBuiltInMemory = ?4 and comment.phone.amountOfRam = ?5 " +
            "and comment.registeredUser = ?6")
    Optional<Comment> findCommentForPhoneCharacteristicsAndRegisteredUser(Brand brand, String name, String series, int amountOfBuiltInMemory, int amountOfRam, RegisteredUser registeredUser);

    @Query("select count(comment) from Comment comment where comment.phone.phoneDescription.brand.id = ?1 and comment.phone.phoneDescription.name = ?2 and comment.phone.phoneDescription.series = ?3 and comment.phone.amountOfBuiltInMemory = ?4 and comment.phone.amountOfRam = ?5")
    Optional<Integer> countCommentsForPhone(String brand, String name, String series, int amountOfBuiltInMemory, int amountOfRam);
}