package ua.com.alevel.model.check;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ua.com.alevel.model.phone.Phone;
import ua.com.alevel.model.user.RegisteredUser;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

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
    private Date created;

    @NotNull
    private double totalPrice;

    @OneToMany(mappedBy = "clientCheck", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Phone> phones;

    private boolean isClosed;
}
