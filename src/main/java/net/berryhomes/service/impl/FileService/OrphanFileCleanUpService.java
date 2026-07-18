package net.berryhomes.service.impl.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.model.entity.Project;
import net.berryhomes.model.entity.ProjectDocument;
import net.berryhomes.model.entity.ProjectImage;
import net.berryhomes.repository.ProjectDocumentRepository;
import net.berryhomes.repository.ProjectImageRepository;
import net.berryhomes.repository.ProjectRepository;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrphanFileCleanUpService {

    private final ProjectRepository projectRepository;
    private final ProjectImageRepository projectImageRepository;
    private final ProjectDocumentRepository projectDocumentRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Scheduled(cron = "0 0 3 * * SUN")
    @Transactional(readOnly = true)
    public void cleanUpOrphanFiles() {
        log.info("=== СТАРТ: Фоновая очистка диска от сиротских файлов ===");

        Path rootPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(rootPath)) {
            log.info("Папка загрузок {} не существует. Очистка не требуется.", rootPath);
            return;
        }

        try {
            Set<Path> filesOnDisk = scanDiskFiles(rootPath);
            log.info("Найдено файлов на диске (старше 24 часов): {}", filesOnDisk.size());

            if (filesOnDisk.isEmpty()) {
                log.info("Нет старых файлов для проверки.");
                log.info("=== ЗАВЕРШЕНО: Очистка диска выполнена ===");
                return;
            }

            Set<String> validPathsInDb = collectValidPathsFromDb();
            log.info("Найдено активных путей к файлам в базе данных: {}", validPathsInDb.size());

            int deletedCount = 0;
            for (Path filePath : filesOnDisk) {
                String relativePathStr = rootPath.relativize(filePath).toString().replace("\\", "/");

                if (!validPathsInDb.contains(relativePathStr)) {
                    log.warn("Обнаружен сиротский файл: {}. Физическое удаление с диска...", relativePathStr);
                    Files.deleteIfExists(filePath);
                    deletedCount++;
                }
            }

            log.info("=== УСПЕХ: Очистка завершена. Удалено сиротских файлов: {} ===", deletedCount);
            cleanEmptyDirectories(rootPath);

        } catch (IOException e) {
            log.error("Критическая ошибка во время фоновой очистки файлов: {}", e.getMessage());
        }
    }

    private Set<Path> scanDiskFiles(Path rootPath) throws IOException {
        Instant cutoff = Instant.now().minus(24, ChronoUnit.HOURS);
        Set<Path> fileSet = new HashSet<>();

        try (Stream<Path> paths = Files.walk(rootPath)) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                try {
                    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                    if (attrs.lastModifiedTime().toInstant().isBefore(cutoff)) {
                        fileSet.add(path);
                    }
                } catch (IOException e) {
                    log.error("Не удалось прочитать атрибуты файла: {}", path, e);
                }
            });
        }
        return fileSet;
    }

    private Set<String> collectValidPathsFromDb() {
        Set<String> dbPaths = new HashSet<>();

        projectImageRepository.findAll().stream()
                .map(ProjectImage::getFilePath)
                .filter(path -> path != null && !path.isBlank())
                .forEach(dbPaths::add);

        projectDocumentRepository.findAll().stream()
                .map(ProjectDocument::getFilePath)
                .filter(path -> path != null && !path.isBlank())
                .forEach(dbPaths::add);

        return dbPaths;
    }

    private void cleanEmptyDirectories(Path rootPath) throws IOException {
        try (Stream<Path> paths = Files.walk(rootPath)) {
            paths.filter(Files::isDirectory)
                    .filter(path -> !path.equals(rootPath))
                    .sorted((p1, p2) -> p2.compareTo(p1))
                    .forEach(dir -> {
                        try (Stream<Path> entries = Files.list(dir)) {
                            if (!entries.findFirst().isPresent()) {
                                Files.delete(dir);
                                log.info("Удалена пустая директория: {}", dir);
                            }
                        } catch (IOException e) {
                            log.error("Не удалось очистить директорию: {}", dir, e);
                        }
                    });
        }
    }
}
