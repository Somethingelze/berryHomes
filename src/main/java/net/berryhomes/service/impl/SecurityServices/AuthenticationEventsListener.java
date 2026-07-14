package net.berryhomes.service.impl.SecurityServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.aop.Loggable;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Loggable
@RequiredArgsConstructor
public class AuthenticationEventsListener {

    private final LoginAttemptService loginAttemptService;

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        String login = event.getAuthentication().getName();
        log.warn("Зафиксировано событие неудачной аутентификации для логина: {}", login);
        loginAttemptService.processFailedLogin(login);
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String login = event.getAuthentication().getName();
        log.info("Зафиксировано событие успешного входа для логина: {}", login);
        loginAttemptService.resetFailedAttempts(login);
    }
}
