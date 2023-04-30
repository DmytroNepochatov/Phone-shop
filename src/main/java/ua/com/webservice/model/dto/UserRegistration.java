package ua.com.webservice.model.dto;

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
    private String dateOfBirth;
    private String phoneNumber;
    private String emailAddress;
    private String password;
    private String secondPassword;
    private String errorString;
    private String userId;
}
