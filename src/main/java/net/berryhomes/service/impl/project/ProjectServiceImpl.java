package net.berryhomes.service.impl.project;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.aop.Loggable;
import net.berryhomes.model.dto.ProjectDocumentDto;
import net.berryhomes.model.dto.ProjectDto;
import net.berryhomes.model.dto.ProjectImageDto;
import net.berryhomes.model.entity.Project;
import net.berryhomes.exception.business.ProjectNotFoundException;
import net.berryhomes.mapper.ProjectMapper;
import net.berryhomes.model.entity.ProjectDocument;
import net.berryhomes.model.entity.ProjectImage;
import net.berryhomes.repository.ProjectRepository;
import net.berryhomes.service.FileStorageService;
import net.berryhomes.service.ProjectDocumentService;
import net.berryhomes.service.ProjectImageService;
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
    private final ProjectImageService projectImageService;
    private final ProjectDocumentService projectDocumentService;


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
        log.info("Creating project with id {}", project.getId());

        ProjectDocumentDto savedProjectDocumentDto = projectDocumentService.uploadDocument(project.getId(), projectDocument);
        ProjectDocument savedProjectDocument = projectMapper.toProjectDocumentEntity(savedProjectDocumentDto);
        log.info("Saving project document with id {}", savedProjectDocument.getId());

        List<ProjectImage> savedProjectImages = projectImages.stream()
                .map(image -> projectImageService.uploadImage(project.getId(), image))
                .map(projectMapper::toProjectImageEntity)
                .toList();
        log.info("Saving project image document with id {}", savedProjectImages.get(0).getId());

        project.setProjectImages(savedProjectImages);
        project.setProjectDocuments(List.of(savedProjectDocument));
        return projectMapper.toProjectDto(projectRepository.save(project));
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
    public ProjectDto updateProject(UUID projectId, ProjectDto projectDto) {
        Project existingProject = projectRepository.findById(projectId).orElseThrow(() -> {
            log.info("Project with id {} not found", projectId);
            return new ProjectNotFoundException(String.format("Project with id %s not found", projectId));
        });

        projectMapper.updateProjectFromDto(projectDto, existingProject);
        log.info("Updating project with id {}", projectDto.id());

        return projectMapper.toProjectDto(projectRepository.save(existingProject));
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
        project.getProjectDocuments().forEach(doc -> fileStorageService.deleteFile(doc.getFilePath()));
        projectRepository.delete(project);

        log.info("Project and all its physical files successfully deleted: {}", id);
    }
}
