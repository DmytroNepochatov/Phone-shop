package ua.com.webservice.model.phone;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class View implements Comparable<View> {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @NotBlank
    private String color;

    @NotBlank
    private String phoneFrontAndBack;

    @NotBlank
    private String leftSideAndRightSide;

    @NotBlank
    private String upSideAndDownSide;

    @NotBlank
    private String colorForWhichPhone;

    @OneToMany(mappedBy = "view", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Phone> phones;

    @Override
    public int compareTo(View o) {
        String current = this.colorForWhichPhone +" "+ this.color;
        String comparable = o.colorForWhichPhone +" "+ o.color;

        return current.compareTo(comparable);
    }
}
