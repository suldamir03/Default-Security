package lern.security.security.service;

import lern.security.security.exception.UserAlreadyExistException;
import lern.security.db.entity.User;
import lern.security.security.model.RegistrationDto;
import lern.security.db.entity.Token;

import java.util.Optional;

public interface AuthService {
    User registerNewUserAccount(RegistrationDto user) throws UserAlreadyExistException;

    void createVerificationToken(User user, String token);

    Token getVerificationToken(String token);

    void saveRegisteredUser(User user);

    User findByEmail(String userEmail);

    void createPasswordResetTokenForUser(User user, String token);

    String validatePasswordResetToken(String token);

    Optional<User> getUserByPasswordResetToken(String token);

    void changeUserPassword(User o, String newPassword);
}
