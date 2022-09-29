package ua.com.alevel.model.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import ua.com.alevel.model.check.ClientCheck;
import ua.com.alevel.model.comment.Comment;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class RegisteredUser {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @NotBlank
    private String lastName;

    @NotBlank
    private String firstName;

    @NotBlank
    private String middleName;

    @NotNull
    private int age;

    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String emailAddress;

    @NotBlank
    private String password;

    @OneToMany(mappedBy = "registeredUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<Comment> comments;

    @OneToMany(mappedBy = "registeredUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    Set<ClientCheck> checks;
}