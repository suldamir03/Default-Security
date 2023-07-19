package lern.security.service.impl;

import lern.security.exception.UserAlreadyExistException;
import lern.security.model.Role;
import lern.security.model.User;
import lern.security.config.auth.model.RegistrationDto;
import lern.security.config.auth.model.Token;
import lern.security.repository.RoleRepository;
import lern.security.repository.TokenRepository;
import lern.security.repository.UserRepository;
import lern.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements UserDetailsService, AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        if (usernameOrEmail == null) throw new UsernameNotFoundException("Username is null");
        usernameOrEmail = usernameOrEmail.trim();


        String finalUsernameOrEmail = usernameOrEmail;
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email: " + finalUsernameOrEmail));


        Set<Role> authorities = user
                .getRoles()
                .stream()
                .map((role) -> new Role(role.getAuthority())).collect(Collectors.toSet());
        user.setRoles(authorities);


        System.err.println("---- user ----");
        System.err.println(user.getUsername());
        System.err.println(user.getRoles());
        System.err.println("---- user ----");

        return user;
    }


    @Override
    public User registerNewUserAccount(RegistrationDto userDto) throws UserAlreadyExistException {
        if (emailExists(userDto.getUsername())) {
            throw new UserAlreadyExistException("There is an account with that username address: "
                    + userDto.getUsername());
        }
        User user = User.builder()
                .enabled(false)
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .roles(Collections.singleton(roleRepository.findByName("USER").get()))
                .build();

        return userRepository.save(user);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        Token verificationToken= new Token();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setType("VerificationToken");
        verificationToken.setExpiryDate(verificationToken.calculateExpiryDate(10));
        tokenRepository.save(verificationToken);
    }

    @Override
    public Token getVerificationToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail).orElseThrow();
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        Token verificationToken= new Token();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setType("PasswordResetToken");
        verificationToken.setExpiryDate(verificationToken.calculateExpiryDate(10));
        tokenRepository.save(verificationToken);
    }

    private boolean emailExists(String username) {
        return userRepository.existsByEmail(username);
    }

    public String validatePasswordResetToken(String token) {
        final Token passToken = tokenRepository.findByToken(token);

        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    @Override
    public Optional<User> getUserByPasswordResetToken(String token) {
        return userRepository.getUserByPasswordResetToken(token, "PasswordResetToken");
    }

    @Override
    public void changeUserPassword(User user, String newPassword) {
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    private boolean isTokenFound(Token passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(Token passToken) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Date.valueOf(LocalDate.now()));
        return passToken.getExpiryDate().before(cal.getTime());
    }
}
