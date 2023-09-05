package lern.security.security.model;

import lombok.Data;

@Data
public class RegistrationDto {
    private String email;
    private String username;
    private String password;

}
