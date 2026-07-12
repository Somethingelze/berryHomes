package net.berryhomes.repository;

import net.berryhomes.model.entity.ProjectDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectDocumentRepository extends JpaRepository<ProjectDocument, UUID> {

    @Modifying
    @Query("DELETE FROM ProjectDocument pd WHERE pd.project.id = :projectId")
    void deleteAllByProjectId(@Param("projectId") UUID projectId);
}

