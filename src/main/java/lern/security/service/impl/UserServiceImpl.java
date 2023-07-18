package lern.security.service.impl;

import lern.security.exception.UserAlreadyExistException;
import lern.security.model.Role;
import lern.security.model.User;
import lern.security.model.auth.RegistrationDto;
import lern.security.repository.RoleRepository;
import lern.security.repository.UserRepository;
import lern.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserDetailsService, UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null) throw new UsernameNotFoundException("Username is null");
        username = username.trim();

        var user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("User not found: " + username);
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
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .roles(Collections.singleton(roleRepository.findByName("USER").get()))
                .build();

        return userRepository.save(user);
    }

    private boolean emailExists(String username) {
        return userRepository.findByUsername(username) != null;
    }
}
