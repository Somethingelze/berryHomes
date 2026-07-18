//package net.berryhomes.controller;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import net.berryhomes.service.FileStorageService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController("/resources")
//@RequiredArgsConstructor
//public class ResourceController {
//
//    private final FileStorageService  fileStorageService;
//
//    //TODO определять директорию автоматически
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam(defaultValue = "general") String subDirectory) {
//        String savedPath = fileStorageService.saveFile(file, subDirectory);
//        return ResponseEntity.ok(savedPath);
//    }
//
//    @PostMapping("/delete")
//    public ResponseEntity<Void> deleteFile(@RequestParam("filePath") String filePath) {
//        fileStorageService.deleteFile(filePath);
//        return ResponseEntity.ok().build();
//    }
//}
