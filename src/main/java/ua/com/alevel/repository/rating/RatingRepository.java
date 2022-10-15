package ua.com.alevel.repository.rating;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.rating.Rating;

@Repository
public interface RatingRepository extends CrudRepository<Rating, String> {
}
