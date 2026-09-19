package net.berryhomes.service;

import net.berryhomes.model.dto.ContactDto;
import org.springframework.mail.MailException;

public interface MailService {
    public void sendContactEmail(ContactDto dto);

    public void recover(MailException e, ContactDto dto);

}
