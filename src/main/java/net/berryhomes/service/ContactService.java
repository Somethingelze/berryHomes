package net.berryhomes.service;

import net.berryhomes.model.ContactStatus;
import net.berryhomes.model.ContactType;
import net.berryhomes.model.dto.ContactDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ContactService {

    @Transactional
    ContactDto saveContact(ContactDto contactDto);

    @Transactional(readOnly = true)
    ContactDto getById(UUID id, Pageable pageable);

    @Transactional(readOnly = true)
    Page<ContactDto> getByName(String name, Pageable pageable);

    @Transactional(readOnly = true)
    Page<ContactDto> getByEmail(String email, Pageable pageable);

    @Transactional(readOnly = true)
    Page<ContactDto> getByPhone(String phone, Pageable pageable);

    @Transactional(readOnly = true)
    Page<ContactDto> getAll(Pageable pageable);

    @Transactional
    ContactDto updateContactStatus(UUID id, ContactStatus contactStatus);

    long countByStatus(ContactStatus status);

    long countByType(ContactType  type);

     List<ContactDto> getRecentLeads(Pageable pageable);

    void deleteContact(UUID id);
}
