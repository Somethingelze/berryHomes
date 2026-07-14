package net.berryhomes.repository;

import net.berryhomes.model.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

    Page<Contact> findAllByEmail(String email, Pageable pageable);

    Page<Contact> findAllByName(String name, Pageable pageable);

    Page<Contact> findAllByPhone(String phone, Pageable pageable);
}
