package lern.security.config.auth;

import lombok.Data;

@Data
public class RegistrationDto {
    private String username;
    private String password;
    private String email;

}
