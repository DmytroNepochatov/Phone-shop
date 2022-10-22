package ua.com.alevel.model.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRegistration {
    private String lastName;
    private String firstName;
    private String middleName;
    private int age;
    private String phoneNumber;
    private String emailAddress;
    private String password;
    private String secondPassword;
    private String errorString;
}
