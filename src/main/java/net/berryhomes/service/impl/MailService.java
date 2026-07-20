package net.berryhomes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.aop.Loggable;
import net.berryhomes.config.EmailConfig;
import net.berryhomes.model.dto.ContactDto;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Loggable
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final EmailConfig emailConfig;

    @Async
    @Retryable(
            retryFor = {MailException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 4000, multiplier = 2.0)
    )
    public void sendContactEmail(ContactDto dto) {

        log.info("Фоновый поток: подготовка текста для {}", dto.email());

        SimpleMailMessage message = new SimpleMailMessage();

        String textContent = String.format(
                "Новая заявка:\n\n" +
                        "ID: %s\nИмя: %s\nEmail: %s\nТелефон: %s\n" +
                        "Тип: %s\nСтатус: %s\nДата: %s\n\n" +
                        "Сообщение:\n%s",
                dto.id(), dto.name(), dto.email(), dto.phone(),
                dto.type(), dto.status(), dto.createdAt(),
                dto.message()
        );

        message.setTo(emailConfig.getEmail());
        message.setSubject("Заявка от " + dto.name());
        message.setText(textContent);
        message.setFrom("your-email@gmail.com");

        mailSender.send(message);
        log.info("Письмо успешно доставлено на {}", emailConfig.getEmail());
    }

    @Recover
    public void recover(MailException e, ContactDto dto) {
        log.error("Ошибка отправки! Письмо от {} потеряно. Причина: {}", dto.email(), e.getMessage());
    }
}