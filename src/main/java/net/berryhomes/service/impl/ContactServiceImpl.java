package net.berryhomes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.aop.Loggable;
import net.berryhomes.config.EmailConfig;
import net.berryhomes.mapper.ContactMapper;
import net.berryhomes.model.ContactStatus;
import net.berryhomes.model.dto.ContactDto;
import net.berryhomes.model.entity.Contact;
import net.berryhomes.repository.ContactRepository;
import net.berryhomes.service.ContactService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@Loggable
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final JavaMailSender javaMailSender;
    private final EmailConfig emailConfig;
    private final MailService mailService;

    @Override
    @Transactional
    public ContactDto saveContact(ContactDto contactDto) {
        log.info("Processing new contact request from email: {}", contactDto.email());

        Contact contact = contactMapper.toEntity(contactDto);

        contact.setContactStatus(ContactStatus.NEW);

        Contact savedContact = contactRepository.save(contact);
        log.info("Successfully saved contact with generated ID: {}", savedContact.getId());

        return contactMapper.toDto(savedContact);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactDto> getByName(String name, Pageable pageable) {
        log.info("Fetching page of contacts by name: {}", name);
        return contactRepository.findAllByName(name, pageable)
                .map(contactMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ContactDto> getByEmail(String email, Pageable pageable) {
        log.info("Fetching page of contacts by email: {}", email);
        return contactRepository.findAllByEmail(email, pageable)
                .map(contactMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactDto> getByPhone(String phone, Pageable pageable) {
        log.info("Fetching page of contacts by phone: {}", phone);
        return contactRepository.findAllByPhone(phone, pageable)
                .map(contactMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ContactDto> getAll(Pageable pageable) {
        return contactRepository.findAll(pageable)
                .map(contactMapper::toDto);
    }

    @Override
    @Transactional
    public void deleteContact(UUID id) {
        contactRepository.deleteById(id);
        log.info("Deleted contact with id: {}", id);
    }

    @Override
    public ContactDto sendContactByEmail(ContactDto contactDto) {
        mailService.sendContactEmail(contactDto);
        return contactDto;
    }
}
