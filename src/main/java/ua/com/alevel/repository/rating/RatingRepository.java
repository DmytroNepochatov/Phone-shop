package ua.com.alevel.repository.rating;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.rating.Rating;

@Repository
public interface RatingRepository extends CrudRepository<Rating, String> {

    @Modifying
    @Query("update Rating rating set rating.numberOfPoints = ?1, rating.totalPoints = ?2 where rating.id = ?3")
    void updateRating(int numberOfPoints, int totalPoints, String id);
}
