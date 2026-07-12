package net.berryhomes.controller;

import lombok.RequiredArgsConstructor;
import net.berryhomes.model.dto.ProjectDto;
import net.berryhomes.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {

    private final ProjectService projectService;

    @GetMapping
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @GetMapping("/tenants")
    public ModelAndView tenants() {
        return new ModelAndView("tenants");
    }

    @GetMapping("/investors")
    public ModelAndView investors(@PageableDefault(size = 6, sort = "createdAt", direction = org.springframework.data.domain.Sort.Direction.DESC) org.springframework.data.domain.Pageable pageable) {
        ModelAndView modelAndView = new ModelAndView("investors");
        Page<ProjectDto> projectsPage = projectService.getProjects(pageable);
        modelAndView.addObject("projectsPage", projectsPage);
        return modelAndView;
    }
}
