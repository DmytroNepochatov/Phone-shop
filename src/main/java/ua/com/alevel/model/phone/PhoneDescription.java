package ua.com.alevel.model.phone;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ua.com.alevel.model.accessory.*;
import ua.com.alevel.model.country.Country;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PhoneDescription implements Comparable<PhoneDescription> {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "charge_type_id")
    private ChargeType chargeType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "communication_standard_id")
    private CommunicationStandard communicationStandard;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "operation_system_id")
    private OperationSystem operationSystem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "processor_id")
    private Processor processor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_screen_id")
    private TypeScreen typeScreen;

    @NotBlank
    private String name;

    @NotBlank
    private String series;

    @NotNull
    private float diagonal;

    @NotBlank
    private String displayResolution;

    @NotNull
    private int screenRefreshRate;

    @NotNull
    private int numberOfSimCards;

    @NotNull
    private int numberOfFrontCameras;

    @NotBlank
    @Column(length = 5000)
    private String infoAboutFrontCameras;

    @NotNull
    private int numberOfMainCameras;

    @NotBlank
    @Column(length = 5000)
    private String infoAboutMainCameras;

    @NotNull
    private float weight;

    @NotNull
    private float height;

    @NotNull
    private float width;

    @NotBlank
    private String degreeOfMoistureProtection;

    private boolean isHaveNfc;

    @NotNull
    private Date dateAddedToDatabase;

    @NotNull
    private int guaranteeTimeMonths;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Country country;

    @OneToMany(mappedBy = "phoneDescription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Phone> phones;

    @Override
    public String toString() {
        return "Phone{" +
                "brand=" + brand.getName() +
                ", name='" + name + '\'' +
                ", series='" + series + '\'';
    }

    @Override
    public int compareTo(PhoneDescription o) {
        String current = this.brand.getName() +" "+ this.series +" "+ this.name;
        String comparable = o.brand.getName() +" "+ o.series +" "+ o.name;

        return current.compareTo(comparable);
    }
}
