package lern.security.security.model;

import lombok.Data;

@Data
public class PasswordDto {
    private String password;
    private String newPassword;
    private String token;
}
