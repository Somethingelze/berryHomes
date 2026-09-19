package net.berryhomes.service.dociment;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String saveFile(MultipartFile file, String subDirectory);
    
    void deleteFile(String filePath);

    java.nio.file.Path resolveFile(String filePath);
}
