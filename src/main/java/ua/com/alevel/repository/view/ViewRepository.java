package ua.com.alevel.repository.view;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.alevel.model.phone.View;
import java.util.List;

@Repository
public interface ViewRepository extends CrudRepository<View, String> {

    @Query("select view from View view where view.color =?1 and  view.phoneFrontAndBack =?2 and view.leftSideAndRightSide =?3 and view.upSideAndDownSide =?4")
    List<View> findFirstByColorPhoneFrontAndBackLeftSideAndRightSideUpSideAndDownSide(String color, String phoneFrontAndBack, String leftSideAndRightSide, String upSideAndDownSide);
}