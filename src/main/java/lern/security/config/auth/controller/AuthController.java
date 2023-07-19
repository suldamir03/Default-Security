package lern.security.config.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lern.security.config.auth.event.OnRegistrationCompleteEvent;
import lern.security.config.auth.model.RegistrationDto;
import lern.security.config.auth.model.Token;
import lern.security.exception.UserAlreadyExistException;
import lern.security.model.User;
import lern.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;


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

            User newUser = authService.registerNewUserAccount(userDto);
            if(newUser == null){
                return "redirect:/reg?wrong=true";
            }
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(newUser,
                    request.getLocale(), appUrl));

            return "redirect:/login?email=true";

        } catch (UserAlreadyExistException e){
            System.out.println(e);
            return "redirect:/reg?error";
        }
    }


    @GetMapping("/regitrationConfirm")
    public String confirmRegistration
            (WebRequest request, Model model, @RequestParam("token") String token, HttpServletRequest servletRequest) {
        System.out.println("Дошел до контроллера");

        Token verificationToken = authService.getVerificationToken(token);
        if (verificationToken == null) {
            return "redirect:/reg?error=true";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        cal.setTime(Date.valueOf(LocalDate.now()));

        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            System.out.println("Время истекло");
            model.addAttribute("message", "Время истекло");
            return "redirect:/reg?error=true";
        }

        user.setEnabled(true);
        authService.saveRegisteredUser(user);

//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        securityContext.setAuthentication(authentication);
//        HttpSession session = servletRequest.getSession(true);
//        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,securityContext);

        return "redirect:/login";
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