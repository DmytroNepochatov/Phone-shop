package ua.com.webservice.model.check;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ua.com.webservice.model.phone.PhoneInstance;
import ua.com.webservice.model.user.RegisteredUser;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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

    private Date closedDate;

    @OneToMany(mappedBy = "clientCheck", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PhoneInstance> phoneInstances;

    private boolean isClosed;

    @NotBlank
    private String deliveryType;

    private String deliveryAddress;

    @NotBlank
    private String paymentType;

    @NotBlank
    private String recipient;

    @NotBlank
    private String recipientNumberPhone;
}
