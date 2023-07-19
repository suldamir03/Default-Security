package lern.security.config.auth.event;

import lern.security.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;
@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    public OnRegistrationCompleteEvent(
      User user, Locale locale, String appUrl) {
        super(user);
        
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
    
    // standard getters and setters
}