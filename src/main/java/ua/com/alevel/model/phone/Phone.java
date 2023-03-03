package ua.com.alevel.model.phone;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import ua.com.alevel.model.comment.Comment;
import ua.com.alevel.model.rating.Rating;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Phone implements Comparable<Phone> {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rating_id")
    private Rating rating;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "view_id")
    private View view;

    @NotNull
    private int amountOfBuiltInMemory;

    @NotNull
    private int amountOfRam;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "phone_description_id")
    private PhoneDescription phoneDescription;

    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;

    @OneToMany(mappedBy = "phone", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhoneInstance> phoneInstances;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Phone phone = (Phone) o;
        return this.phoneDescription.getBrand().getName().equals(phone.phoneDescription.getBrand().getName()) &&
                this.phoneDescription.getName().equals(phone.phoneDescription.getName()) &&
                this.phoneDescription.getSeries().equals(phone.phoneDescription.getSeries()) &&
                this.amountOfBuiltInMemory == phone.amountOfBuiltInMemory &&
                this.amountOfRam == phone.amountOfRam;
    }

    @Override
    public int compareTo(Phone o) {
        return this.phoneDescription.compareTo(o.phoneDescription);
    }
}
