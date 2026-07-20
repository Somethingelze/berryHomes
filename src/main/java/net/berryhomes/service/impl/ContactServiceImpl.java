package net.berryhomes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.aop.Loggable;
import net.berryhomes.exception.business.ContactNotFoundException;
import net.berryhomes.mapper.ContactMapper;
import net.berryhomes.model.ContactStatus;
import net.berryhomes.model.ContactType;
import net.berryhomes.model.dto.ContactDto;
import net.berryhomes.model.entity.Contact;
import net.berryhomes.repository.ContactRepository;
import net.berryhomes.service.ContactService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Loggable
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final MailService mailService;

    @Override
    @Transactional
    public ContactDto saveContact(ContactDto contactDto) {
        log.info("Processing new contact request from email: {}", contactDto.email());

        Contact contact = contactMapper.toEntity(contactDto);
        contact.setCreatedAt(ZonedDateTime.now());
        contact.setStatus(ContactStatus.NEW);
        Contact savedContact = contactRepository.save(contact);
        mailService.sendContactEmail(contactMapper.toDto(savedContact));

        log.info("Successfully saved contact with generated ID: {}", savedContact.getId());
        return contactMapper.toDto(savedContact);
    }

    @Override
    public ContactDto getById(UUID id, Pageable pageable) {
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ContactNotFoundException("Contact with id " + id + "was not found"));
        return contactMapper.toDto(contact);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactDto> getByName(String name, Pageable pageable) {
        log.info("Fetching page of contacts by name: {}", name);
        return contactRepository.findAllByNameContainingIgnoreCase(name, pageable).orElseThrow(() -> {
                    log.info("No contact with name {} was found", name);
                    return new ContactNotFoundException("No contact with name " + name);
                })
                .map(contactMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ContactDto> getByEmail(String email, Pageable pageable) {
        log.info("Fetching page of contacts by email: {}", email);
        return contactRepository.findAllByEmailContainingIgnoreCase(email, pageable).orElseThrow(() -> {
                    log.info("No contact found with email: {}", email);
                    throw new ContactNotFoundException("Contact with email " + email + " was not found");
                })
                .map(contactMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactDto> getByPhone(String phone, Pageable pageable) {
        log.info("Fetching page of contacts by phone: {}", phone);
        return contactRepository.findAllByPhoneContainingIgnoreCase(phone, pageable).orElseThrow(() -> {
                    log.info("No contact found with phone: {}", phone);
                    throw new ContactNotFoundException("Contact with phone " + phone + " was not found");
                })
                .map(contactMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ContactDto> getAll(Pageable pageable) {
        return contactRepository.findAll(pageable)
                .map(contactMapper::toDto);
    }

    @Transactional
    @Override
    public ContactDto updateContactStatus(UUID id, ContactStatus contactStatus) {
        log.info("Start updating contact by id: {} to status: {}", id, contactStatus);

        Contact contact = contactRepository.findById(id).orElseThrow(() -> {
            log.info("Try to update not existed contact with id {}", id);
            return new ContactNotFoundException("Contact with id " + id + " was not found");
        });
        contact.setStatus(contactStatus);
        return contactMapper.toDto(contactRepository.save(contact));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactDto> filterContacts(ContactType type, ContactStatus status, Pageable pageable) {
        if (type != null && status != null) {
            return contactRepository.findByTypeAndStatus(type, status, pageable)
                    .orElseThrow(() -> {
                        log.info("No contact with type {} and status {} was found", type, status);
                        return new ContactNotFoundException("Contacts with type " + type +
                                " and status " + status + " was not found");
                    })
                    .map(contactMapper::toDto);
        }
        if (type != null) {
            return contactRepository.findByType(type, pageable)
                    .orElseThrow(() -> {
                        log.info("No contact with type {} was found", type);
                        return new ContactNotFoundException("Contacts with type " + type + "was not found");
                    })
                    .map(contactMapper::toDto);
        }
        if (status != null) {
            return contactRepository.findByStatus(status, pageable)
                    .orElseThrow(() -> {
                        log.info("No contact with staus {} was found", status);
                        return new ContactNotFoundException("Contacts with status " + status + "was not found");
                    })
                    .map(contactMapper::toDto);
        }
        return contactRepository.findAll(pageable)
                .map(contactMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<ContactDto> searchContacts(String search, Pageable pageable) {
        String query = search.trim();
        if (query.contains("@")) {
            return contactRepository.findAllByEmailContainingIgnoreCase(query, pageable).orElseThrow(() -> {
                log.info("No contact found with email: {}", search);
                return new ContactNotFoundException("Contact with email " + search + " was not found");
            })
                    .map(contactMapper::toDto);
        } else if (query.matches(".*\\d+.*")) {
            return contactRepository.findAllByPhoneContainingIgnoreCase(query, pageable).orElseThrow(() -> {
                log.info("No contact found with phone: {}", search);
                throw new ContactNotFoundException("Contact with phone " + search + " was not found");
                    })
                    .map(contactMapper::toDto);
        } else {
            return contactRepository.findAllByNameContainingIgnoreCase(query, pageable).orElseThrow(() ->    {
                log.info("No contact with name {} was found", search);
                return new ContactNotFoundException("No contact with name " + search + " was not found");
                    })
                    .map(contactMapper::toDto);
        }
    }

    @Override
    public long countByStatus(ContactStatus status) {
        return contactRepository.countByStatus(status);
    }

    @Override
    public long countByType(ContactType type) {
        return contactRepository.countByType(type);
    }

    @Override
    public List<ContactDto> getRecentLeads(Pageable pageable) {
        return contactRepository.findAll(pageable).getContent().stream()
                .map(contactMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteContact(UUID id) {
        contactRepository.deleteById(id);
        log.info("Deleted contact with id: {}", id);
    }
}
