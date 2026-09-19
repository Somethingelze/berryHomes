package net.berryhomes.controller;

import lombok.RequiredArgsConstructor;
import net.berryhomes.service.project.ProjectService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainViewController {

    private final ProjectService projectService;

    @GetMapping
    public ModelAndView index() {
        return new ModelAndView("index");
    }
}
