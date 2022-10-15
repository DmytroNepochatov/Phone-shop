package ua.com.alevel.repository.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.comment.Comment;
import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, String> {
    @Query("select comment from Comment comment where comment.phone.brand.name = ?1 and comment.phone.name = ?2 and comment.phone.series = ?3 and comment.phone.amountOfBuiltInMemory = ?4 and comment.phone.amountOfRam = ?5")
    List<Comment> findAllCommentsForPhone(String brand, String name, String series, int amountOfBuiltInMemory, int amountOfRam, Pageable pageable);
}
