package net.berryhomes.service.impl.Project;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.aop.Loggable;
import net.berryhomes.model.dto.ProjectDto;
import net.berryhomes.model.entity.Project;
import net.berryhomes.exception.business.ProjectNotFoundException;
import net.berryhomes.mapper.ProjectMapper;
import net.berryhomes.model.entity.ProjectDocument;
import net.berryhomes.model.entity.ProjectImage;
import net.berryhomes.repository.ProjectRepository;
import net.berryhomes.service.FileStorageService;
import net.berryhomes.service.ProjectService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Loggable
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final FileStorageService fileStorageService;


    @Override
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public ProjectDto createProject(ProjectDto projectDto) {
        Project project = projectRepository.save(projectMapper.toProjectEntity(projectDto));
        log.info("Creating project with id {}", project.getId());
        return projectMapper.toProjectDto(project);
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public ProjectDto createProjectWithFiles(ProjectDto projectDto, List<MultipartFile> projectImages, MultipartFile projectDocument) {
        Project project = projectRepository.save(projectMapper.toProjectEntity(projectDto));
        log.info("Creating project via form submission");

        if (projectImages != null && !projectImages.isEmpty()) {
            projectImages.stream()
                    .filter(image -> image != null && !image.isEmpty())
                    .forEach(image -> {
                        String relativePath = fileStorageService.saveFile(image, "images/" + project.getId());
                        project.getProjectImages().add(ProjectImage.builder()
                                .project(project)
                                .filePath(relativePath)
                                .build());
                    });
            log.info("Attached {} images for project", project.getProjectImages().size());
        }

        if (projectDocument != null && !projectDocument.isEmpty()) {
            String relativePath = fileStorageService.saveFile(projectDocument, "documents/" + project.getId());
            project.setProjectDocument(ProjectDocument.builder()
                    .project(project)
                    .filePath(relativePath)
                    .build());
            log.info("Attached project document");
        }

        return projectMapper.toProjectDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto getProjectById(UUID id) {
        Project project = projectRepository.findActiveById(id).orElseThrow(() -> {
            log.info("Project with id {} not found", id);
            return new ProjectNotFoundException(String.format("Project with id %s not found", id));
        });
        log.info("Getting project with id {}", project.getId());
        return projectMapper.toProjectDto(project);
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable(value = "projects", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProjectDto> getAllActiveProjects(Pageable pageable) {
        log.info("Getting all active projects");
        return projectRepository.findAllByDeletedAtIsNull(pageable)
                .map(projectMapper::toProjectDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "projects", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProjectDto> getAllArchivedProjects(Pageable pageable)   {
        log.info("Getting all archived projects");
        return projectRepository.findAllByDeletedAtIsNotNull(pageable)
                .map(projectMapper::toProjectDto);
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public ProjectDto updateProjectWithFiles(UUID id, ProjectDto projectDto, List<MultipartFile> projectImages, MultipartFile projectDocument) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + id));
        log.info("Updating project with id {}", id);

        projectMapper.updateProjectFromDto(projectDto, project);

        if (projectImages != null && !projectImages.isEmpty()) {
            projectImages.stream()
                    .filter(image -> image != null && !image.isEmpty())
                    .forEach(image -> {
                        String relativePath = fileStorageService.saveFile(image, "images/" + project.getId());

                        project.getProjectImages().add(ProjectImage.builder()
                                .project(project)
                                .filePath(relativePath)
                                .build());
                    });
            log.info("Added new images. Total images now: {}", project.getProjectImages().size());
        }

        if (projectDocument != null && !projectDocument.isEmpty()) {
            if (project.getProjectDocument() != null) {
                fileStorageService.deleteFile(project.getProjectDocument().getFilePath());
            }

            String relativePath = fileStorageService.saveFile(projectDocument, "documents/" + project.getId());

            ProjectDocument doc = ProjectDocument.builder()
                    .project(project)
                    .filePath(relativePath)
                    .build();

            project.setProjectDocument(doc);
            log.info("Updated project document");
        }

        Project updatedProject = projectRepository.save(project);
        return projectMapper.toProjectDto(updatedProject);
    }


    @Override
    public long countActiveProjects() {
        return projectRepository.countByDeletedAtIsNull();
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public void archiveProject(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found: " + id));
        project.setDeletedAt(ZonedDateTime.now());

        log.info("Archiving project with id {}", id);
        projectRepository.save(project);
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public void restoreProject(UUID id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found: " + id));
        project.setDeletedAt(null);

        log.info("Restore project with id {}", id);
        projectRepository.save(project);
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public void deleteProject(UUID id) {
        log.info("Starting hard delete for project with id {}", id);

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found: " + id));

        project.getProjectImages().forEach(img -> fileStorageService.deleteFile(img.getFilePath()));
        fileStorageService.deleteFile(project.getProjectDocument().getFilePath());
        projectRepository.delete(project);

        log.info("Project and all its physical files successfully deleted: {}", id);
    }
}
