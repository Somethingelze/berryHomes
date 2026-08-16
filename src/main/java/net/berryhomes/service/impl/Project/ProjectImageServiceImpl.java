package net.berryhomes.service.impl.Project;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.aop.Loggable;
import net.berryhomes.model.dto.ProjectImageDto;
import net.berryhomes.model.entity.Project;
import net.berryhomes.model.entity.ProjectImage;
import net.berryhomes.exception.business.ProjectFileNotFoundException;
import net.berryhomes.exception.business.ProjectNotFoundException;
import net.berryhomes.mapper.ProjectMapper;
import net.berryhomes.repository.ProjectImageRepository;
import net.berryhomes.repository.ProjectRepository;
import net.berryhomes.service.ProjectImageService;
import net.berryhomes.service.impl.File.FileStorageServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Loggable
@RequiredArgsConstructor
public class ProjectImageServiceImpl implements ProjectImageService {

    private final ProjectImageRepository projectImageRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final FileStorageServiceImpl fileStorageService;

    private static final String UPLOAD_SUB_DIR = "images/";

    @Override
    @Transactional
    public ProjectImageDto uploadImage(UUID projectId, MultipartFile file) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> {
            log.info("Try to find project with id {} not found", projectId);
            return new ProjectNotFoundException(String.format("Project with id %s not found", projectId));
        });
        String relativePath = fileStorageService.saveFile(file, UPLOAD_SUB_DIR + project.getId());

        ProjectImage projectImage = ProjectImage.builder()
                .project(project)
                .filePath(relativePath)
                .build();

        return projectMapper.toProjectImageDto(projectImageRepository.save(projectImage));
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public void deleteImage(UUID imageId) {
        ProjectImage projectImage = projectImageRepository.findById(imageId).orElseThrow(() -> {
            log.info("Try to find image with id {} not found", imageId);
            return new ProjectFileNotFoundException(String.format("Image with id %s not found", imageId));
        });
        deleteFileAfterCommit(projectImage.getFilePath());
        projectImageRepository.delete(projectImage);
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public void updateSortOrder(UUID imageId, Integer sortOrder) {
        ProjectImage projectImage = projectImageRepository.findById(imageId).orElseThrow(() -> {
            log.info("Try to find image with id {} not found", imageId);
            return new ProjectFileNotFoundException(String.format("Image with id %s not found", imageId));
        });
        projectImage.setSortOrder(sortOrder);
        projectImageRepository.save(projectImage);
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public void updateSortOrder(UUID projectId, List<UUID> imageIds) {
        List<ProjectImage> images = projectImageRepository.findAllById(imageIds);
        validateProjectImages(projectId, imageIds, images);
        for (int index = 0; index < imageIds.size(); index++) {
            UUID imageId = imageIds.get(index);
            ProjectImage image = images.stream().filter(item -> item.getId().equals(imageId)).findFirst().orElseThrow();
            image.setSortOrder(index);
        }
        projectImageRepository.saveAll(images);
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    public void deleteImages(UUID projectId, List<UUID> imageIds) {
        List<ProjectImage> images = projectImageRepository.findAllById(imageIds);
        validateProjectImages(projectId, imageIds, images);
        images.forEach(image -> deleteFileAfterCommit(image.getFilePath()));
        projectImageRepository.deleteAll(images);
    }

    private void validateProjectImages(UUID projectId, List<UUID> imageIds, List<ProjectImage> images) {
        if (images.size() != imageIds.size() || images.stream().anyMatch(image -> !image.getProject().getId().equals(projectId))) {
            throw new ProjectFileNotFoundException("One or more project images were not found");
        }
    }

    private void deleteFileAfterCommit(String filePath) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            public void afterCommit() {
                try {
                    fileStorageService.deleteFile(filePath);
                } catch (RuntimeException exception) {
                    log.error("Database record was deleted, but physical image file {} could not be removed", filePath, exception);
                }
            }
        });
    }
}
