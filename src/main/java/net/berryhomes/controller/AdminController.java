package net.berryhomes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.berryhomes.model.dto.ProjectDto;
import net.berryhomes.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProjectService projectService;

    @GetMapping("/projects/all")
    public ResponseEntity<Page<ProjectDto>> getProjects(@PageableDefault(size = 20, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ProjectDto> projects = projectService.getAllProjects(pageable);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/projects")
    public ResponseEntity<ProjectDto> getProject(UUID id) {

        ProjectDto projectDto = projectService.getProject(id);
        return ResponseEntity.ok(projectDto);
    }

    @PostMapping("/create")
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto projectDto) {

        ProjectDto createdProject = projectService.createProject(projectDto);
        return ResponseEntity.ok(createdProject);
    }

    @PostMapping("/update")
    public ResponseEntity<ProjectDto> updateProject(@Valid @RequestBody ProjectDto projectDto) {
        ProjectDto updatedProject = projectService.updateProject(projectDto.getId(), projectDto);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/projects/archive")
    public void archiveProject(@Valid @RequestBody UUID projectId) {
        projectService.archivedProject(projectId);
    }

    @DeleteMapping("/projects/delete")
    public void deleteProject(@Valid @RequestBody UUID projectId) {
        projectService.deleteProject(projectId);
    }





}

