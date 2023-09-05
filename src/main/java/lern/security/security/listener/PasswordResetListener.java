package lern.security.security.listener;

import lern.security.security.event.OnPasswordResetEvent;
import lern.security.db.entity.User;
import lern.security.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PasswordResetListener implements ApplicationListener<OnPasswordResetEvent> {

    private final AuthService service;
    private final JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnPasswordResetEvent event) {
        this.confirmRegistration(event);
    }

    public void confirmRegistration(OnPasswordResetEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createPasswordResetTokenForUser(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Reset your Password";
        String confirmationUrl
                = event.getAppUrl() + "/user/changePassword?token=" + token;
        String message = "Пройдите по ссылке для изменения пароля";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + "http://localhost:8080" + confirmationUrl);
        mailSender.send(email);
    }
}