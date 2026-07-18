package net.berryhomes.controller.admin;

import lombok.RequiredArgsConstructor;
import net.berryhomes.model.ContactStatus;
import net.berryhomes.model.dto.ContactDto;
import net.berryhomes.service.ContactService;
import net.berryhomes.service.FileStorageService;
import net.berryhomes.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/admin/contacts")
@RequiredArgsConstructor
public class AdminContactViewController {

    private final ContactService contactService;
    private final ProjectService projectService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public ModelAndView listContacts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        ModelAndView mav = new ModelAndView("admin/contacts");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<ContactDto> contactPage;

        if (search != null && !search.trim().isEmpty()) {
            String trimmedSearch = search.trim();
            if (trimmedSearch.contains("@")) {
                contactPage = contactService.getByEmail(trimmedSearch, pageable);
            } else if (trimmedSearch.matches(".*\\d+.*")) {
                contactPage = contactService.getByPhone(trimmedSearch, pageable);
            } else {
                contactPage = contactService.getByName(trimmedSearch, pageable);
            }
            mav.addObject("currentSearch", trimmedSearch);
        } else {
            contactPage = contactService.getAll(pageable);
            mav.addObject("currentSearch", "");
        }

        mav.addObject("contactPage", contactPage);
        mav.addObject("statuses", ContactStatus.values());

        return mav;
    }

    @PostMapping("/{id}/status")
    public ModelAndView updateStatus(@PathVariable UUID id,
                                     @RequestParam ContactStatus status,
                                     RedirectAttributes redirectAttributes) {
        contactService.updateContactStatus(id, status);

        redirectAttributes.addFlashAttribute("successMessage", "Status updated successfully!");
        return new ModelAndView("redirect:/admin/contacts");
    }

    @PostMapping("/{id}/delete")
    public ModelAndView deleteContact(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        contactService.deleteContact(id);
        redirectAttributes.addFlashAttribute("successMessage", "Contact deleted successfully!");
        return new ModelAndView("redirect:/admin/contacts");
    }
}

