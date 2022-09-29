package ua.com.alevel.model.check;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.model.user.RegisteredUser;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ClientCheck {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_user_id")
    private RegisteredUser registeredUser;

    @NotNull
    private LocalDate created;

    @NotNull
    private int totalPrice;

    @OneToMany(mappedBy = "clientCheck", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Phone> phones;

    private boolean isClosed;
}
