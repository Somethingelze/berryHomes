package net.berryhomes.service;

import net.berryhomes.model.dto.ProjectImageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProjectImageService {

    ProjectImageDto uploadImage(UUID projectId, MultipartFile file);

    void deleteImage(UUID imageId);

    void updateSortOrder(UUID imageId, Integer sortOrder);
}