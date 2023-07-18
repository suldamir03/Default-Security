package lern.security.service;

import lern.security.exception.UserAlreadyExistException;
import lern.security.model.User;
import lern.security.model.auth.RegistrationDto;

public interface UserService {
    User registerNewUserAccount(RegistrationDto user) throws UserAlreadyExistException;
}
