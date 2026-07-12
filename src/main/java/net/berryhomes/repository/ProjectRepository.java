package net.berryhomes.repository;

import net.berryhomes.model.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT p FROM Project p WHERE p.id = :id AND p.deletedAt IS NULL")
    Optional<Project> findActiveById(@Param("id") UUID id);

    Page<Project> findAllByDeletedAtIsNull(Pageable pageable);

}
