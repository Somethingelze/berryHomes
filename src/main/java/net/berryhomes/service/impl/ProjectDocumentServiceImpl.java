package net.berryhomes.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.aop.Loggable;
import net.berryhomes.model.dto.ProjectDocumentDto;
import net.berryhomes.model.entity.Project;
import net.berryhomes.model.entity.ProjectDocument;
import net.berryhomes.exception.business.ProjectFileNotFoundException;
import net.berryhomes.exception.business.ProjectNotFoundException;
import net.berryhomes.mapper.ProjectMapper;
import net.berryhomes.repository.ProjectDocumentRepository;
import net.berryhomes.repository.ProjectRepository;
import net.berryhomes.service.ProjectDocumentService;
import net.berryhomes.service.impl.FileService.FileStorageServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
@Loggable
@RequiredArgsConstructor
public class ProjectDocumentServiceImpl implements ProjectDocumentService {

    private final ProjectDocumentRepository projectDocumentRepository;
    private final ProjectRepository projectRepository;
    private final FileStorageServiceImpl fileStorageService;
    private final ProjectMapper projectMapper;

    private static final String UPLOAD_SUB_DIR = "documents/";


    @Override
    @Transactional
    public ProjectDocumentDto uploadDocument(UUID projectId, String titleRu, String titleEng, MultipartFile file) {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> {
            log.info("Try to find project with id {} not found", projectId);
            return new ProjectNotFoundException(String.format("Project with id %s not found", projectId));
        });
        String relativePath = fileStorageService.saveFile(file, UPLOAD_SUB_DIR + project.getId());
        ProjectDocument projectDocument = ProjectDocument.builder()
                .project(project)
                .titleRu(titleRu)
                .titleEn(titleEng)
                .filePath(relativePath)
                .build();

        return projectMapper.toProjectDocumentDto(projectDocumentRepository.save(projectDocument));
    }

    @Override
    @Transactional
    public void deleteDocument(UUID documentId) {
        ProjectDocument projectDocument = projectDocumentRepository.findById(documentId).orElseThrow(() -> {
            log.info("Try to find document with id {} not found", documentId);
            return new ProjectFileNotFoundException(String.format("Document with id %s not found", documentId));
        });

        fileStorageService.deleteFile(projectDocument.getFilePath());
        projectDocumentRepository.delete(projectDocument);
    }
}
