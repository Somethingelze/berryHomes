package net.berryhomes.repository;

import net.berryhomes.model.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProjectImageRepository extends JpaRepository<ProjectImage, UUID> {

    @Modifying
    @Query("DELETE FROM ProjectImage pi WHERE pi.project.id = :projectId")
    void deleteAllByProjectId(@Param("projectId") UUID projectId);
}
