package net.berryhomes.service;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;

public interface FileStorageService {
    String saveFile(MultipartFile file, String subDirectory);
    
    void deleteFile(String filePath);
}
