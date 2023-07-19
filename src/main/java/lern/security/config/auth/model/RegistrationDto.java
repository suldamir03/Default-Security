package lern.security.config.auth.model;

import lombok.Data;

@Data
public class RegistrationDto {
    private String email;
    private String username;
    private String password;

}
