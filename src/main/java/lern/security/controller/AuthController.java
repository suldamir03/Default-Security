package lern.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lern.security.exception.UserAlreadyExistException;
import lern.security.model.Role;
import lern.security.model.User;
import lern.security.model.auth.RegistrationDto;
import lern.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;


    @GetMapping("/login")
    public String login(){
        return "login";
    }


    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(null);
        return "redirect:/login";
    }

    @GetMapping("/reg")
    public String reg (Model model){
        model.addAttribute("user",new RegistrationDto());
        return "reg";
    }

    @PostMapping("/reg")
    public String createUser(@ModelAttribute("user") RegistrationDto userDto,HttpServletRequest request, HttpServletResponse response){
        try {

            User newUser = userService.registerNewUserAccount(userDto);
            if(newUser == null){
                return "redirect:/reg?wrong=true";
            }

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(newUser.getUsername(),newUser.getPassword()));
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,securityContext);

            return "redirect:/home";

        } catch (UserAlreadyExistException e){
            System.out.println(e);
            return "redirect:/reg?error";
        }
    }




    //    //Пример собственной авторизации Post запрос
//    @PostMapping("/signin")
//    public ResponseEntity<String> authenticateUser(@RequestBody User loginDto){
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//                loginDto.getUsername(), loginDto.getPassword()));
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        return new ResponseEntity<>("User signed-in successfully!.", HttpStatus.OK);
//    }
}
