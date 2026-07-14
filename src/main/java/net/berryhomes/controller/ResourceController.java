package net.berryhomes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.berryhomes.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final FileStorageService  fileStorageService;

    //TODO определять директорию автоматически
    @PostMapping("/upload")
    public ResponseEntity uploadFile(@Valid @RequestBody MultipartFile file, String subDirectory) {
        fileStorageService.saveFile(file, subDirectory);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete")
    public ResponseEntity deleteFile(@Valid @RequestBody MultipartFile file) {
        fileStorageService.deleteFile(file.getOriginalFilename());
        return ResponseEntity.ok().build();
    }
}
