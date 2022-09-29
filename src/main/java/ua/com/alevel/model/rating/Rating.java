package ua.com.alevel.model.rating;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ua.com.alevel.model.phone.Phone;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Rating {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @NotNull
    private int totalPoints;

    @NotNull
    private int numberOfPoints;

    @OneToMany(mappedBy = "rating", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Phone> phones;
}
