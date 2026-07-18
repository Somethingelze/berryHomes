package net.berryhomes.service;

import net.berryhomes.model.dto.ProjectDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProjectService {

    ProjectDto createProject(ProjectDto projectDto);

    ProjectDto createProjectWithFiles(ProjectDto projectDto, List<MultipartFile> documentImages, MultipartFile documentFile);

    ProjectDto getProjectById(UUID id);

    Page<ProjectDto> getAllActiveProjects(Pageable pageable);

    Page<ProjectDto> getAllArchivedProjects(Pageable pageable);

    ProjectDto updateProject(UUID projectId, ProjectDto projectDto);

    void archiveProject(UUID id);

    void restoreProject(UUID id);

    void deleteProject(UUID id);


}
