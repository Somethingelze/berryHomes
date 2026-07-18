package net.berryhomes.controller.admin;

import lombok.RequiredArgsConstructor;
import net.berryhomes.model.ContactStatus;
import net.berryhomes.model.ContactType;
import net.berryhomes.model.dto.DashboardStatsDto;
import net.berryhomes.service.ContactService;
import net.berryhomes.service.ProjectService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final ContactService contactService;
    private final ProjectService projectService;

    @GetMapping
    public ModelAndView showDashboard() {
        ModelAndView mav = new ModelAndView("admin/dashboard");

        Pageable topFive = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        
        DashboardStatsDto stats = new DashboardStatsDto(
                contactService.countByStatus(ContactStatus.NEW),
                projectService.countActiveProjects(),
                contactService.countByType(ContactType.TENANT),
                contactService.countByType(ContactType.HOMEOWNER),
                contactService.countByType(ContactType.INVESTOR),
                contactService.getRecentLeads(topFive)
        );

        mav.addObject("stats", stats);
        return mav;
    }
}
