package lern.security.service;

import lern.security.exception.UserAlreadyExistException;
import lern.security.model.User;
import lern.security.config.auth.RegistrationDto;
import lern.security.config.auth.VerificationToken;

public interface UserService {
    User registerNewUserAccount(RegistrationDto user) throws UserAlreadyExistException;

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String token);

    void saveRegisteredUser(User user);
}
