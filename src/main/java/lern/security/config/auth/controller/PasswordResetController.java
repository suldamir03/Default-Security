package lern.security.config.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lern.security.config.auth.event.OnPasswordResetEvent;
import lern.security.config.auth.model.PasswordDto;
import lern.security.model.User;
import lern.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {
    private final AuthService authService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping("/forgotPassword")
    public String forgotPassword() {
        return "forgotPassword";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(HttpServletRequest request,
                                @RequestParam("email") String userEmail) {
        User user = authService.findByEmail(userEmail);
        if (user == null) {
            throw new UsernameNotFoundException("No user with this email");
        }
        String token = UUID.randomUUID().toString();
        String appUrl = request.getContextPath();
        authService.createPasswordResetTokenForUser(user, token);
        eventPublisher.publishEvent(new OnPasswordResetEvent(user, request.getLocale(), appUrl));

        return "redirect:/forgotPassword?email=true";
    }

    @GetMapping("/user/changePassword")
    public String showChangePasswordPage(Model model,
                                         @RequestParam("token") String token) {
        String result = authService.validatePasswordResetToken(token);
        if (result == null) {
            return "redirect:/login?token=true";
        } else {
            PasswordDto passwordDto = new PasswordDto();
            passwordDto.setToken(token);
            model.addAttribute("passDto", passwordDto);
            return "updatePassword";
        }
    }

    @PostMapping("/user/savePassword")
    public String savePassword( @ModelAttribute("passDto") PasswordDto passwordDto) {
        String result = authService.validatePasswordResetToken(passwordDto.getToken());
        if (result == null) {
            return "redirect:/forgotPassword?expired=true";
        }
        Optional<User> user = authService.getUserByPasswordResetToken(passwordDto.getToken());
        user.ifPresent(value -> authService.changeUserPassword(value, passwordDto.getNewPassword()));
        return "redirect:/login?pass=true";
    }
}
