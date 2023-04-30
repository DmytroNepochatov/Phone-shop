package ua.com.webservice.model.accessory;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ua.com.webservice.model.phone.PhoneDescription;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Processor {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @NotBlank
    @Column(unique = true)
    private String name;

    @NotNull
    private int numberOfCores;

    @NotNull
    private float coreFrequency;

    @OneToMany(mappedBy = "processor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhoneDescription> phoneDescriptions;
}
