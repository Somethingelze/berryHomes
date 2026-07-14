package net.berryhomes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.model.dto.ProjectDto;
import net.berryhomes.model.entity.Project;
import net.berryhomes.exception.business.ProjectNotFoundException;
import net.berryhomes.mapper.ProjectMapper;
import net.berryhomes.repository.ProjectRepository;
import net.berryhomes.service.FileStorageService;
import net.berryhomes.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final FileStorageService fileStorageService;


    @Override
    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        Project project = projectRepository.save(projectMapper.toProjectEntity(projectDto));
        log.info("Creating project with id {}", project.getId());
        return projectMapper.toProjectDto(project);
    }

    @Override
    public ProjectDto getProject(UUID id) {
        Project project = projectRepository.findActiveById(id).orElseThrow(() -> {
            log.info("Project with id {} not found", id);
            return new ProjectNotFoundException(String.format("Project with id %s not found", id));
        });
        log.info("Getting project with id {}", project.getId());
        return projectMapper.toProjectDto(project);
    }


    @Override
    public Page<ProjectDto> getAllProjects(Pageable pageable) {
        log.info("Getting all projects");
        return projectRepository.findAll(pageable)
                .map(projectMapper::toProjectDto);

    }

    @Override
    @Transactional
    public ProjectDto updateProject(UUID projectId, ProjectDto projectDto) {
        Project existingProject = projectRepository.findById(projectId).orElseThrow(() -> {
            log.info("Project with id {} not found", projectId);
            return new ProjectNotFoundException(String.format("Project with id %s not found", projectId));
        });

        projectMapper.updateProjectFromDto(projectDto, existingProject);
        log.info("Updating project with id {}", projectDto.getId());

        return projectMapper.toProjectDto(projectRepository.save(existingProject));
    }

    @Transactional
    @Override
    public void archivedProject(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found: " + id));
        project.setDeletedAt(ZonedDateTime.now());

        log.info("Archiving project with id {}", id);
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteProject(UUID id) {
        log.info("Starting hard delete for project with id {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found: " + id));

        if (project.getReportFilePath() != null) {
            fileStorageService.deleteFile(project.getReportFilePath());
        }

        project.getProjectImages().forEach(img -> fileStorageService.deleteFile(img.getFilePath()));
        project.getProjectDocuments().forEach(doc -> fileStorageService.deleteFile(doc.getFilePath()));
        projectRepository.delete(project);

        log.info("Project and all its physical files successfully deleted: {}", id);
    }
}
