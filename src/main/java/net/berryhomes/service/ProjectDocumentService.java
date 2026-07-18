package net.berryhomes.service;

import net.berryhomes.model.dto.ProjectDocumentDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProjectDocumentService {

    ProjectDocumentDto uploadDocument(UUID projectId, MultipartFile file);

    void deleteDocument(UUID documentId);
}
