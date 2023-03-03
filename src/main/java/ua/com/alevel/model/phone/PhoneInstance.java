package ua.com.alevel.model.phone;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.shoppingcart.ShoppingCart;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PhoneInstance implements Comparable<PhoneInstance> {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_id")
    private ClientCheck clientCheck;

    @NotBlank
    private String imei;

    @NotNull
    private double price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shopping_cart_id")
    private ShoppingCart shoppingCart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "phone_id")
    private Phone phone;

    @Override
    public String toString() {
        return ", imei='" + imei + '\'' + '}';
    }

    @Override
    public int compareTo(PhoneInstance o) {
        return this.phone.compareTo(o.phone);
    }
}