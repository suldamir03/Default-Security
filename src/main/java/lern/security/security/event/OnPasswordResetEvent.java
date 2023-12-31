package lern.security.security.event;

import java.util.Locale;
import lern.security.db.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnPasswordResetEvent extends ApplicationEvent {

    private String appUrl;
    private Locale locale;
    private User user;

    public OnPasswordResetEvent(User user, Locale locale, String appUrl) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }

    // standard getters and setters
}