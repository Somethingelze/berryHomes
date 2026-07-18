package net.berryhomes.service.impl.FileService;

import lombok.extern.slf4j.Slf4j;
import net.berryhomes.aop.Loggable;
import net.berryhomes.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@Loggable
public class FileStorageServiceImpl implements FileStorageService {

    private final Path rootLocation;

    public FileStorageServiceImpl(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать корневую директорию для загрузок", e);
        }
    }

    @Override
    public String saveFile(MultipartFile file, String subDirectory) {
        if (file.isEmpty()) {
            log.info("Файл пуст и не может быть сохранен");
            throw new IllegalArgumentException("Файл пуст и не может быть сохранен");
        }

        try {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalFilename.substring(dotIndex);
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            Path targetDir = this.rootLocation.resolve(subDirectory);
            Files.createDirectories(targetDir);

            Path targetLocation = targetDir.resolve(uniqueFilename);

            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return Paths.get(subDirectory).resolve(uniqueFilename).toString().replace("\\", "/");

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении файла на диск", e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isBlank()) return;
        try {
            Path fileToDestroy = this.rootLocation.resolve(filePath).normalize();
            Files.deleteIfExists(fileToDestroy);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось удалить файл с диска: " + filePath, e);
        }
    }
}
