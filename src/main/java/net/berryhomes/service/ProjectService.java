package net.berryhomes.service;

import net.berryhomes.model.dto.ProjectDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface ProjectService {

    ProjectDto createProject(ProjectDto projectDto);
    ProjectDto getProject(UUID id);
    Page<ProjectDto> getProjects(Pageable pageable);
    ProjectDto updateProject(UUID projectId, ProjectDto projectDto);

    @Transactional
    void archivedProject(UUID id);

    void deleteProject(UUID id);


}
