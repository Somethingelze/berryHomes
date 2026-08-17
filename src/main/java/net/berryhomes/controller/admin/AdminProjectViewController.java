package net.berryhomes.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.berryhomes.model.dto.ProjectDto;
import net.berryhomes.model.entity.ProjectDocument;
import net.berryhomes.service.ContactService;
import net.berryhomes.service.FileStorageService;
import net.berryhomes.service.ProjectDocumentService;
import net.berryhomes.service.ProjectImageService;
import net.berryhomes.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/admin/projects")
@RequiredArgsConstructor
public class AdminProjectViewController {

    private final ProjectService projectService;
    private final ProjectDocumentService projectDocumentService;
    private final ProjectImageService projectImageService;
    private final net.berryhomes.service.AuditService auditService;

    // 1. Просмотр активных проектов
    @GetMapping
    public ModelAndView listActiveProjects(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        ModelAndView mav = new ModelAndView("admin/projects-list");
        Pageable pageable = PageRequest.of(Math.max(page, 0), java.util.Set.of(10, 20, 50, 100).contains(size) ? size : 10, Sort.by("createdAt").descending());

        Page<ProjectDto> activeProjects = projectService.getAllActiveProjects(pageable);

        mav.addObject("projectPage", activeProjects);
        mav.addObject("isArchiveView", false);
        mav.addObject("currentSize", java.util.Set.of(10, 20, 50, 100).contains(size) ? size : 10);
        return mav;
    }

    // 2. Просмотр архивных проектов (Soft Deleted)
    @GetMapping("/archived")
    public ModelAndView listArchivedProjects(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        ModelAndView mav = new ModelAndView("admin/projects-list");
        Pageable pageable = PageRequest.of(Math.max(page, 0), java.util.Set.of(10, 20, 50, 100).contains(size) ? size : 10, Sort.by("createdAt").descending());

        mav.addObject("projectPage", projectService.getAllArchivedProjects(pageable));
        mav.addObject("isArchiveView", true);
        mav.addObject("currentSize", java.util.Set.of(10, 20, 50, 100).contains(size) ? size : 10);
        return mav;
    }

    // 3. Открытие формы создания (Инициализация пустого DTO)
    @GetMapping("/create")
    public ModelAndView createProjectView() {
        ModelAndView mav = new ModelAndView("admin/project-form");
        ProjectDto emptyProject = ProjectDto.builder().build();

        mav.addObject("projectDto", emptyProject);
        return mav;
    }

    // 4. Открытие формы редактирования существующего проекта
    @GetMapping("/{id}/edit")
    public ModelAndView showEditForm(@PathVariable UUID id) {
        ModelAndView mav = new ModelAndView("admin/project-form");

        ProjectDto existingProject = projectService.getProjectById(id);
        mav.addObject("projectDto", existingProject);
        return mav;
    }

    // 5. Сохранение нового или обновление существующего проекта (Multipart)
    @PostMapping("/save")
    public ModelAndView saveProject(
            @ModelAttribute("projectDto") @Valid ProjectDto projectDto,
            BindingResult bindingResult,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles,
            @RequestParam(value = "document", required = false) MultipartFile documentFile,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    log.error("Field: {}, value: {}, message: {}",
                            error.getRejectedValue(),
                            error.getDefaultMessage()));

            ModelAndView errorMav = new ModelAndView("admin/project-form");
            errorMav.addObject("projectDto", projectDto);
            return errorMav;
        }

        ProjectDto previous = projectDto.id() == null ? null : projectService.getProjectById(projectDto.id());
        String changes = projectChanges(previous, projectDto, imageFiles, documentFile);
        if (projectDto.id() != null) {
            projectService.updateProjectWithFiles(projectDto.id(), projectDto, imageFiles, documentFile);
            log.info("Project with ID {} updated successfully", projectDto.id());
        } else {
            projectService.createProjectWithFiles(projectDto, imageFiles, documentFile);
            log.info("New project created successfully");
        }

        auditService.record(previous == null ? "CREATE" : "UPDATE", "PROJECT", projectDto.id() == null ? null : projectDto.id().toString(),
                projectDto.address() + " || " + changes);
        redirectAttributes.addFlashAttribute("successMessage", "Investment project saved successfully!");
        return new ModelAndView("redirect:/admin/projects");
    }

    @PostMapping("/save-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveProjectAjax(
            @ModelAttribute("projectDto") @Valid ProjectDto projectDto,
            BindingResult bindingResult,
            @RequestParam(value = "images", required = false) List<MultipartFile> imageFiles,
            @RequestParam(value = "document", required = false) MultipartFile documentFile) {
        if (bindingResult.hasErrors()) {
            String message = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce((first, second) -> first + "; " + second)
                    .orElse("Please check the project fields");
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", message));
        }

        ProjectDto previous = projectDto.id() == null ? null : projectService.getProjectById(projectDto.id());
        String changes = projectChanges(previous, projectDto, imageFiles, documentFile);
        ProjectDto savedProject = projectDto.id() == null
                ? projectService.createProjectWithFiles(projectDto, imageFiles, documentFile)
                : projectService.updateProjectWithFiles(projectDto.id(), projectDto, imageFiles, documentFile);
        auditService.record(previous == null ? "CREATE" : "UPDATE", "PROJECT", savedProject.id().toString(),
                savedProject.address() + " || " + changes);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Investment project saved successfully!",
                "projectId", savedProject.id().toString(),
                "editUrl", "/admin/projects/" + savedProject.id() + "/edit"));
    }

    @PostMapping("/media/images/order")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateImageOrder(@RequestParam UUID projectId,
                                                                 @RequestParam List<UUID> imageIds) {
        projectImageService.updateSortOrder(projectId, imageIds);
        return ResponseEntity.ok(Map.of("success", true, "message", "Image order saved"));
    }

    @PostMapping("/media/images/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteProjectImages(@RequestParam UUID projectId,
                                                                    @RequestParam List<UUID> imageIds) {
        projectImageService.deleteImages(projectId, imageIds);
        return ResponseEntity.ok(Map.of("success", true, "message", "Selected images deleted"));
    }

    @PostMapping("/media/document/{docId}/delete-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteProjectDocumentAjax(@PathVariable UUID docId) {
        projectDocumentService.deleteDocument(docId);
        return ResponseEntity.ok(Map.of("success", true, "message", "Document deleted"));
    }

    // 6. Мягкое удаление (Архивация)
    @PostMapping("/{id}/archive")
    public ModelAndView archiveProject(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        String label = projectService.getProjectByIdIncludingArchived(id).address();
        projectService.archiveProject(id);
        auditService.record("ARCHIVE", "PROJECT", id.toString(), label);
        redirectAttributes.addFlashAttribute("successMessage", "Project archived successfully!");
        return new ModelAndView("redirect:/admin/projects");
    }

    // 7. Восстановление из архива
    @PostMapping("/{id}/restore")
    public ModelAndView restoreProject(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        String label = projectService.getProjectByIdIncludingArchived(id).address();
        projectService.restoreProject(id);
        auditService.record("RESTORE", "PROJECT", id.toString(), label);
        redirectAttributes.addFlashAttribute("successMessage", "Project restored successfully!");
        return new ModelAndView("redirect:/admin/projects/archived");
    }

    // Безвозвратное удаление доступно администратору только для архивного проекта.
    @PostMapping("/{id}/delete")
    public ModelAndView deleteProject(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        String label = projectService.getProjectByIdIncludingArchived(id).address();
        projectService.deleteProject(id);
        auditService.record("DELETE", "PROJECT", id.toString(), label + " · Permanently deleted from archive");
        redirectAttributes.addFlashAttribute("successMessage", "Project permanently deleted.");
        return new ModelAndView("redirect:/admin/projects/archived");
    }

    // 8. Поштучное удаление фотографии из галереи проекта
    @PostMapping("/media/image/{imageId}/delete")
    public ModelAndView deleteProjectImage(@PathVariable UUID imageId,
                                           @RequestParam UUID projectId,
                                           RedirectAttributes redirectAttributes) {
        projectImageService.deleteImage(imageId);
        redirectAttributes.addFlashAttribute("successMessage", "Image deleted successfully!");
        return new ModelAndView("redirect:/admin/projects/" + projectId + "/edit");
    }

    // 9. Поштучное удаление прикрепленного документа проекта
    @PostMapping("/media/document/{docId}/delete")
    public ModelAndView deleteProjectDocument(@PathVariable UUID docId,
                                              @RequestParam UUID projectId,
                                              RedirectAttributes redirectAttributes) {
        projectDocumentService.deleteDocument(docId);
        redirectAttributes.addFlashAttribute("successMessage", "Document deleted successfully!");
        return new ModelAndView("redirect:/admin/projects/" + projectId + "/edit");
    }

    // 10. Изменение номера сортировки картинки (Смена обложки)
    @PostMapping("/media/image/{imageId}/sort")
    public ModelAndView updateImageSortOrder(@PathVariable UUID imageId,
                                             @RequestParam UUID projectId,
                                             @RequestParam Integer sortOrder,
                                             RedirectAttributes redirectAttributes) {
        projectImageService.updateSortOrder(imageId, sortOrder);
        redirectAttributes.addFlashAttribute("successMessage", "Display order updated!");
        return new ModelAndView("redirect:/admin/projects/" + projectId + "/edit");
    }

    private String projectChanges(ProjectDto old, ProjectDto updated, List<MultipartFile> images, MultipartFile document) {
        if (old == null) return "Project created";
        List<String> changes = new ArrayList<>();
        changed(changes, "Address", old.address(), updated.address());
        changed(changes, "City / ZIP", old.cityZip(), updated.cityZip());
        changed(changes, "Purchase price", old.purchasePrice(), updated.purchasePrice());
        changed(changes, "Monthly rent", old.monthlyRent(), updated.monthlyRent());
        changed(changes, "Renovation budget", old.renovationBudget(), updated.renovationBudget());
        changed(changes, "Annual NOI", old.estNoiAnnual(), updated.estNoiAnnual());
        changed(changes, "Total investment", old.totalInvestment(), updated.totalInvestment());
        changed(changes, "Cash-on-cash return", old.cashOnCashReturn(), updated.cashOnCashReturn());
        changed(changes, "Estimated payback", old.estPayback(), updated.estPayback());
        long addedImages = images == null ? 0 : images.stream().filter(file -> file != null && !file.isEmpty()).count();
        if (addedImages > 0) changes.add("Images added: " + addedImages);
        if (document != null && !document.isEmpty()) changes.add("Project document replaced");
        return changes.isEmpty() ? "Saved without field changes" : String.join("; ", changes);
    }

    private void changed(List<String> changes, String field, String oldValue, String newValue) {
        if (!Objects.equals(oldValue, newValue)) changes.add(field + ": “" + value(oldValue) + "” → “" + value(newValue) + "”");
    }

    private String value(String value) { return value == null || value.isBlank() ? "empty" : value.trim(); }
}
