package net.berryhomes.repository;

import net.berryhomes.model.ContactStatus;
import net.berryhomes.model.ContactType;
import net.berryhomes.model.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    Optional<Page<Contact>> findAllByEmailContainingIgnoreCase(String email, Pageable pageable);

    Optional<Page<Contact>> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<Page<Contact>> findAllByPhoneContainingIgnoreCase(String phone, Pageable pageable);

    Optional<Page<Contact>> findByType(ContactType type, Pageable pageable);

    Optional<Page<Contact>> findByStatus(ContactStatus status, Pageable pageable);

    Optional<Page<Contact>> findByTypeAndStatus(ContactType type, ContactStatus status, Pageable pageable);

    long countByStatus(ContactStatus contactStatus);

    long countByType(ContactType contactType);

}
