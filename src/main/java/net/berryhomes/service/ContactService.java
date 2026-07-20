package net.berryhomes.service;

import net.berryhomes.model.ContactStatus;
import net.berryhomes.model.ContactType;
import net.berryhomes.model.dto.ContactDto;
import net.berryhomes.model.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ContactService {

    ContactDto saveContact(ContactDto contactDto);

    ContactDto getById(UUID id, Pageable pageable);

    Page<ContactDto> getByName(String name, Pageable pageable);

    Page<ContactDto> getByEmail(String email, Pageable pageable);

    Page<ContactDto> getByPhone(String phone, Pageable pageable);

    Page<ContactDto> getAll(Pageable pageable);

    ContactDto updateContactStatus(UUID id, ContactStatus contactStatus);

    Page<Contact> getContacts(String search, ContactType type, ContactStatus status, Pageable pageable);

    long countByStatus(ContactStatus status);

    long countByType(ContactType type);

    List<ContactDto> getRecentLeads(Pageable pageable);

    void deleteContact(UUID id);
}
