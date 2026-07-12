package net.berryhomes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.model.dto.ProjectImageDto;
import net.berryhomes.model.entity.Project;
import net.berryhomes.model.entity.ProjectImage;
import net.berryhomes.exception.ProjectFileNotFoundException;
import net.berryhomes.exception.ProjectNotFoundException;
import net.berryhomes.mapper.ProjectMapper;
import net.berryhomes.repository.ProjectImageRepository;
import net.berryhomes.repository.ProjectRepository;
import net.berryhomes.service.ProjectImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
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
    public void deleteImage(UUID imageId) {
        ProjectImage projectImage = projectImageRepository.findById(imageId).orElseThrow(() -> {
            log.info("Try to find image with id {} not found", imageId);
            return new ProjectFileNotFoundException(String.format("Image with id %s not found", imageId));
        });
        fileStorageService.deleteFile(projectImage.getFilePath());
        projectImageRepository.delete(projectImage);
    }

    @Override
    @Transactional
    public void updateSortOrder(UUID imageId, Integer sortOrder) {
        ProjectImage projectImage = projectImageRepository.findById(imageId).orElseThrow(() -> {
            log.info("Try to find image with id {} not found", imageId);
            return new ProjectFileNotFoundException(String.format("Image with id %s not found", imageId));
        });
        projectImage.setSortOrder(sortOrder);
        projectImageRepository.save(projectImage);
    }
}
