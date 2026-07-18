package net.berryhomes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.berryhomes.model.ContactStatus;
import net.berryhomes.model.ContactType;
import net.berryhomes.model.dto.ContactDto;
import net.berryhomes.model.dto.ProjectDto;
import net.berryhomes.service.ContactService;
import net.berryhomes.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/investors")
@RequiredArgsConstructor
public class InvestorsViewController {

    private final ContactService contactService;
    private final ProjectService projectService;

    @GetMapping
    public ModelAndView showInvestorCenter() {
        ModelAndView mav = new ModelAndView("investors");

        Pageable pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());

        Page<ProjectDto> projectPage = projectService.getAllActiveProjects(pageable);

        List<ProjectDto> recentProjects = projectPage.getContent();
        mav.addObject("projects", recentProjects);

        ContactDto emptyForm = ContactDto.builder()
                .contactType(ContactType.INVESTOR)
                .contactStatus(ContactStatus.NEW)
                .build();

        mav.addObject("contactDto", emptyForm);
        return mav;
    }

    @PostMapping("/message")
    public ModelAndView handleInvestorMessage(@ModelAttribute("contactDto") @Valid ContactDto dto,
                                              BindingResult bindingResult,
                                              RedirectAttributes redirectAttributes) {
        Pageable pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());

        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("investors");
            mav.addObject("projects", projectService.getAllActiveProjects(pageable).getContent());
            return mav;
        }

        contactService.saveContact(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Thank you! Our investment manager will contact you shortly.");
        return new ModelAndView("redirect:/investors");
    }
}