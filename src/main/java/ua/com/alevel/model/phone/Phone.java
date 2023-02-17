package ua.com.alevel.model.phone;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ua.com.alevel.model.accessory.*;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.country.Country;
import ua.com.alevel.model.rating.Rating;
import ua.com.alevel.model.shoppingcart.ShoppingCart;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Phone {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_id")
    private ClientCheck clientCheck;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rating_id")
    private Rating rating;

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
    private int amountOfBuiltInMemory;

    @NotNull
    private int amountOfRam;

    @NotNull
    private int numberOfFrontCameras;

    @NotBlank
    @Column(length=5000)
    private String infoAboutFrontCameras;

    @NotNull
    private int numberOfMainCameras;

    @NotBlank
    @Column(length=5000)
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

    @NotBlank
    private String color;

    @NotNull
    private int guaranteeTimeMonths;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Country country;

    @NotBlank
    private String phoneFrontAndBack;

    @NotBlank
    private String leftSideAndRightSide;

    @NotBlank
    private String upSideAndDownSide;

    @NotBlank
    private String imei;

    @NotNull
    private double price;

    @NotBlank
    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_cart_id")
    private ShoppingCart shoppingCart;

    @Override
    public String toString() {
        return "Phone{" +
                "brand=" + brand.getName() +
                ", name='" + name + '\'' +
                ", series='" + series + '\'' +
                ", imei='" + imei + '\'' +
                '}';
    }
}
