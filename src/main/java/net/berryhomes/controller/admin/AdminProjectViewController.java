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
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/admin/projects")
@RequiredArgsConstructor
public class AdminProjectViewController {

    private final ProjectService projectService;
    private final ProjectDocumentService projectDocumentService;
    private final ProjectImageService projectImageService;

    // 1. Просмотр активных проектов
    @GetMapping
    public ModelAndView listActiveProjects(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        ModelAndView mav = new ModelAndView("admin/projects-list");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ProjectDto> activeProjects = projectService.getAllActiveProjects(pageable);

        mav.addObject("projectPage", activeProjects);
        mav.addObject("isArchiveView", false);
        return mav;
    }

    // 2. Просмотр архивных проектов (Soft Deleted)
    @GetMapping("/archived")
    public ModelAndView listArchivedProjects(@RequestParam(defaultValue = "0") int page) {
        ModelAndView mav = new ModelAndView("admin/projects-list");
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());

        mav.addObject("projectPage", projectService.getAllArchivedProjects(pageable));
        mav.addObject("isArchiveView", true);
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

            bindingResult.getGlobalErrors().forEach(error ->
                    log.error("Global error: {}", error));

            ModelAndView errorMav = new ModelAndView("admin/project-form");
            errorMav.addObject("projectDto", projectDto);
            return errorMav;
        }

        if (projectDto.id() != null) {
            projectService.updateProjectWithFiles(projectDto.id(), projectDto, imageFiles, documentFile);
            log.info("Project with ID {} updated successfully", projectDto.id());
        } else {
            projectService.createProjectWithFiles(projectDto, imageFiles, documentFile);
            log.info("New project created successfully");
        }

        redirectAttributes.addFlashAttribute("successMessage", "Investment project saved successfully!");
        return new ModelAndView("redirect:/admin/projects");
    }

    // 6. Мягкое удаление (Архивация)
    @PostMapping("/{id}/archive")
    public ModelAndView archiveProject(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        projectService.archiveProject(id);
        redirectAttributes.addFlashAttribute("successMessage", "Project archived successfully!");
        return new ModelAndView("redirect:/admin/projects");
    }

    // 7. Восстановление из архива
    @PostMapping("/{id}/restore")
    public ModelAndView restoreProject(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        projectService.restoreProject(id);
        redirectAttributes.addFlashAttribute("successMessage", "Project restored successfully!");
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

    @InitBinder
    public void initBinder(org.springframework.web.bind.WebDataBinder binder) {
        binder.setDisallowedFields("createdAt", "updatedAt", "deletedAt");
    }
}
